package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class SleeplessNights {

    private static final LocalTime NIGHT_START = LocalTime.of(0, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);

    public static final SleepAnalysisFunction SLEEPLESS_NIGHTS_COUNT = sessions -> {
        if (sessions.isEmpty()) return 0L;

        LocalDateTime firstStart = sessions.get(0).fallAsleep();
        LocalDateTime lastEnd = sessions.get(sessions.size() - 1).wakeUp();

        // Определяем первую потенциальную ночь
        LocalDate firstNight = firstStart.toLocalTime().isAfter(LocalTime.NOON)
                ? firstStart.toLocalDate().plusDays(1)
                : firstStart.toLocalDate();

        // Последняя потенциальная ночь
        LocalDate lastNight = lastEnd.toLocalDate();

        long totalNights = firstNight.datesUntil(lastNight.plusDays(1)).count();

        long sleepless = firstNight.datesUntil(lastNight.plusDays(1))
                .filter(night -> !hasSleepDuringNight(sessions, night))
                .count();

        return sleepless;
    };

    private static boolean hasSleepDuringNight(List<SleepSession> sessions, LocalDate night) {
        LocalDateTime nightStart = LocalDateTime.of(night, NIGHT_START);
        LocalDateTime nightEnd = LocalDateTime.of(night, NIGHT_END);

        return sessions.stream().anyMatch(s ->
                s.fallAsleep().isBefore(nightEnd) && s.wakeUp().isAfter(nightStart)
        );
    }
}
