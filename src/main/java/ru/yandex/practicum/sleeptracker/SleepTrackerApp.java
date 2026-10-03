package ru.yandex.practicum.sleeptracker;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SleepTrackerApp {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Укажите путь к файлу с логом сна.");
            return;
        }

        try {
            List<SleepSession> sessions = SleepLogParser.parse(args[0]);

            Map<String, SleepAnalysisFunction> functions = new LinkedHashMap<>();
            functions.put("Всего сессий сна", SleepAnalysisFunctions.SESSION_COUNT);
            functions.put("Минимальная продолжительность (мин)", SleepAnalysisFunctions.MIN_DURATION);
            functions.put("Максимальная продолжительность (мин)", SleepAnalysisFunctions.MAX_DURATION);
            functions.put("Средняя продолжительность (мин)", SleepAnalysisFunctions.AVERAGE_DURATION);
            functions.put("Сессий с плохим качеством", SleepAnalysisFunctions.BAD_QUALITY_COUNT);
            functions.put("Бессонных ночей", SleeplessNights.SLEEPLESS_NIGHTS_COUNT);
            functions.put("Хронотип пользователя", ChronotypeAnalyzer.CHRONOTYPE);

            functions.forEach((name, fn) -> {
                Object result = fn.apply(sessions);
                System.out.println(name + ": " + result);
            });

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}