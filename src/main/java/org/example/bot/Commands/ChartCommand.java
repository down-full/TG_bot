package org.example.bot.commands;

import java.io.File;
import java.time.LocalDate;
import org.example.bot.core.Executer;
import org.example.bot.utils.ChartGenerator;
import org.example.bot.utils.DateParser;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ChartCommand implements Command {

    private final Executer bot;

    public ChartCommand(Executer bot) {
        this.bot = bot;
    }

    @Override
    public String getName() {
        return "/chart";
    }

    @Override
    public String getDescription() {
        return "График курса валют. Формат: /chart <Из валюты> <В валюту> <Дата от> <Дата до>";
    }

    @Override
public void execute(Long chatId, String messageText) {
        String[] parts = messageText.trim().split("\\s+", 4);
        if (parts.length < 4) {
            sendError(chatId,
                 "Неверный формат команды.\nПример: /chart USD RUB 01.01.2023 10.01.2023");
            return;
        }

        String baseCurrency = parts[1].toUpperCase();
        String targetCurrency = parts[2].toUpperCase();
        String datePart = parts[3].trim();

        LocalDate fromDate;
        LocalDate toDate;

        DateParser.ParsedDateRange range = DateParser.parseRange(datePart);
        if (range != null) {
            fromDate = range.from;
            toDate = range.to;
        } else {
            // если не диапазон пытаемся распарсить две даты через пробел
            String[] dates = datePart.split("\\s+");
            if (dates.length == 2) {
                fromDate = DateParser.parseSingleDate(dates[0]);
                toDate = DateParser.parseSingleDate(dates[1]);
            } else if (dates.length == 1) {
                sendError(chatId, "Не возможно предоставить график за один день");
                return;
            } else {
                sendError(chatId, "Не удалось распознать даты."
                + " Используйте корректные форматы или диапазон через дефис.");
                return;
            }
        }

        if (fromDate == null || toDate == null) {
            sendError(chatId, "Не удалось распознать даты."
            + "Ознакомьтесь с инструкцией и проверьте формат.");
            return;
        }

        try {
            File tempFile = File.createTempFile("chart_", ".png");
            ChartGenerator.generatePngChart(baseCurrency,
                                            targetCurrency,
                                            fromDate, 
                                            toDate, 
                                            tempFile.getAbsolutePath());
            sendFile(chatId, tempFile);
            tempFile.deleteOnExit();
            //sendError(chatId, "Распарсилось как " + fromDate + " ------ " + toDate); - для теста
        } catch (Exception e) {
            sendError(chatId, "Ошибка при генерации графика: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void sendError(Long chatId, String text) {
        bot.sendMessage(chatId, text);
    }

    private void sendFile(Long chatId, File file) {
        SendDocument document = new SendDocument();
        document.setChatId(chatId.toString());
        document.setDocument(new InputFile(file));
        try {
            bot.execute(document); 
        } catch (TelegramApiException e) {
            e.printStackTrace();
            bot.sendMessage(chatId, "Ошибка при отправке файла: " + e.getMessage());
        }
    } 
}
