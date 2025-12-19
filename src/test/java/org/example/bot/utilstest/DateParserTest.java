package org.example.bot.utilstest;

import org.example.bot.utils.DateParser;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.Assert.*;

public class DateParserTest {

    private LocalDate now;

    @Before
    public void setUp() {
        now = LocalDate.now();
    }

    @Test
    public void testRelativeToday() { 
        LocalDate d = DateParser.parseSingleDate("today");
        assertEquals(now, d);
    }

    @Test
    public void testRelativeTodayRussian() {
        LocalDate d = DateParser.parseSingleDate("сегодня");
        assertEquals(now, d);
    }

    @Test
    public void testRelativeLastMonth() { 
        LocalDate d = DateParser.parseSingleDate("last month");
        assertEquals(now.minusMonths(1), d);
    }

    @Test
    public void testRelativeLastYear() {
        LocalDate d = DateParser.parseSingleDate("год назад");
        assertEquals(now.minusYears(1), d);
    }

    @Test
    public void testDMY() {
        LocalDate d = DateParser.parseSingleDate("12.05.2023");
        assertEquals(LocalDate.of(2023, 5, 12), d);
    }

    @Test
    public void testYMD() {
        LocalDate d = DateParser.parseSingleDate("2023-01-15");
        assertEquals(LocalDate.of(2023, 1, 15), d);
    }

    @Test
    public void testDMYShort() {
        LocalDate d = DateParser.parseSingleDate("01.02.24");
        assertEquals(LocalDate.of(2024, 2, 1), d);
    }

    @Test
    public void testDMSlash() {
        LocalDate d = DateParser.parseSingleDate("07/11/2022");
        assertEquals(LocalDate.of(2022, 11, 7), d);
    }

    @Test
    public void testDMDash() {
        LocalDate d = DateParser.parseSingleDate("03-09-2020");
        assertEquals(LocalDate.of(2020, 9, 3), d);
    }

    @Test
    public void testTextMonthDayMonthYear() {
        LocalDate d = DateParser.parseSingleDate("12 марта 2023");
        assertEquals(LocalDate.of(2023, Month.MARCH, 12), d);
    }

    @Test
    public void testTextMonthNoDay() {
        LocalDate d = DateParser.parseSingleDate("май 2021");
        assertEquals(LocalDate.of(2021, Month.MAY, 1), d);
    }

    @Test
    public void testTextMonthEnglish() {
        LocalDate d = DateParser.parseSingleDate("7 august 2020");
        assertEquals(LocalDate.of(2020, Month.AUGUST, 7), d);
    }

    @Test
    public void testRangeSimple() {
        DateParser.ParsedDateRange r = DateParser.parseRange("01.02.2020 - 05.02.2020");
        assertNotNull(r);
        assertEquals(LocalDate.of(2020, 2, 1), r.from);
        assertEquals(LocalDate.of(2020, 2, 5), r.to);
    }

    @Test
    public void testRangeDifferentFormats() {
        DateParser.ParsedDateRange r = DateParser.parseRange("2020-01-01 — 15.01.2020");
        assertNotNull(r);
        assertEquals(LocalDate.of(2020, 1, 1), r.from);
        assertEquals(LocalDate.of(2020, 1, 15), r.to);
    }

    @Test
    public void testInvalidDate() {
        assertNull(DateParser.parseSingleDate("abcdef"));
    }

    @Test
    public void testInvalidRange() {
        assertNull(DateParser.parseRange("something - wrong"));
    }
}
