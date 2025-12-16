package org.example.bot.Utils;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.*;

public class DateParser {

    private static final DateTimeFormatter DMY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DMY_SHORT = DateTimeFormatter.ofPattern("dd.MM.yy");
    private static final DateTimeFormatter DM_SLASH = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DM_DASH = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final Map<String, Month> monthNames;

    static {
        monthNames = new HashMap<>();

        addMonth(monthNames, Month.JANUARY, "январь", "января", "янв", "jan", "january");
        addMonth(monthNames, Month.FEBRUARY, "февраль", "февраля", "фев", "feb", "february");
        addMonth(monthNames, Month.MARCH, "март", "марта", "мар", "mar", "march");
        addMonth(monthNames, Month.APRIL, "апрель", "апреля", "апр", "apr", "april");
        addMonth(monthNames, Month.MAY, "май", "мая", "may");
        addMonth(monthNames, Month.JUNE, "июнь", "июня", "июн", "jun", "june");
        addMonth(monthNames, Month.JULY, "июль", "июля", "июл", "jul", "july");
        addMonth(monthNames, Month.AUGUST, "август", "августа", "авг", "aug", "august");
        addMonth(monthNames, Month.SEPTEMBER, "сентябрь", "сентября", "сен", "sep", "september");
        addMonth(monthNames, Month.OCTOBER, "октябрь", "октября", "окт", "oct", "october");
        addMonth(monthNames, Month.NOVEMBER, "ноябрь", "ноября", "ноя", "nov", "november");
        addMonth(monthNames, Month.DECEMBER, "декабрь", "декабря", "дек", "dec", "december");
    }

    private static void addMonth(Map<String, Month> map, Month month, String... names) {
        for (String name : names) {
            map.put(name.toLowerCase(), month);
        }
    }

    public static LocalDate parseSingleDate(String input) {
        input = input.trim().toLowerCase();

        // 1) relative: today, yesterday, last month
        LocalDate rel = parseRelative(input);
        if (rel != null) return rel;

        // 2) possible formats: dd.mm.yyyy, yyyy-mm-dd
        LocalDate num = parseNumFormats(input);
        if (num != null) return num;

        // 3) text month: 1 may 2023, май 2023
        LocalDate text = parseTextMonth(input);
        if (text != null) return text;

        return null;
    }

    public static ParsedDateRange parseRange(String input) {
        input = input.trim().toLowerCase();

        Matcher m = Pattern.compile("(.+)\\s*[-–—]\\s*(.+)").matcher(input);
        if (m.matches()) {
            LocalDate from = parseSingleDate(m.group(1).trim());
            LocalDate to   = parseSingleDate(m.group(2).trim());
            if (from != null && to != null)
                return new ParsedDateRange(from, to);
        }

        return null;
    }

    private static LocalDate parseRelative(String s) {
        LocalDate now = LocalDate.now();

        switch (s) {

            case "сегодня": 
            case "today": 
                return now;

            case "last month":
            case "месяц назад":
                return now.minusMonths(1);

            case "last half year":
            case "полгода назад":
                return now.minusMonths(6);

            case "last year":
            case "год назад":
                return now.minusYears(1);
        }
        return null;
    }

    private static LocalDate parseNumFormats(String s) {
        try { return LocalDate.parse(s, DMY); } catch (Exception ignored) {}
        try { return LocalDate.parse(s, YMD); } catch (Exception ignored) {}
        try { return LocalDate.parse(s, DMY_SHORT); } catch (Exception ignored) {}
        try { return LocalDate.parse(s, DM_SLASH); } catch (Exception ignored) {}
        try { return LocalDate.parse(s, DM_DASH); } catch (Exception ignored) {}

        return null;
    }

    private static LocalDate parseTextMonth(String s) {

        String[] parts = s.split("\\s+");

        if (parts.length == 2) {
            // "май 2023"
            if (monthNames.containsKey(parts[0])) {
                Month m = monthNames.get(parts[0]);
                int year = Integer.parseInt(parts[1]);
                return LocalDate.of(year, m, 1);
            }
        }

        if (parts.length == 3) {
            try {
                int day = Integer.parseInt(parts[0]);
                Month m = monthNames.get(parts[1]);
                int year = Integer.parseInt(parts[2]);
                return LocalDate.of(year, m, day);
            } catch (Exception ignored) {}
        }

        return null;
    }

    public static class ParsedDateRange {
        public LocalDate from;
        public LocalDate to;

        public ParsedDateRange(LocalDate from, LocalDate to) {
            this.from = from;
            this.to = to;
        }
    }
}

