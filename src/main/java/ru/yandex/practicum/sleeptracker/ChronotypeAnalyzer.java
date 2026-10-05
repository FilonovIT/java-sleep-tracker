package ru.yandex.practicum.sleeptracker;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChronotypeAnalyzer {

    public static final SleepAnalysisFunction CHRONOTYPE = sessions -> {
        // Фильтруем только ночные сессии (исключаем дневные)
        final List<SleepSession> nightSessions = sessions.stream()
                .filter(ChronotypeAnalyzer::isNightSession)
                .collect(Collectors.toList());

        if (nightSessions.isEmpty()) return Chronotype.PIGEON;

        Map<Chronotype, Long> counts = nightSessions.stream()
                .map(ChronotypeAnalyzer::classifyNight)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        long owls = counts.getOrDefault(Chronotype.OWL, 0L);
        long larks = counts.getOrDefault(Chronotype.LARK, 0L);
        long pigeons = counts.getOrDefault(Chronotype.PIGEON, 0L);

        long max = Math.max(owls, Math.max(larks, pigeons));

        // Если есть неоднозначность — считаем голубем
        long maxCount = java.util.stream.Stream.of(owls, larks, pigeons)
                .filter(c -> c == max).count();
        if (maxCount > 1) return Chronotype.PIGEON;

        if (owls == max) return Chronotype.OWL;
        if (larks == max) return Chronotype.LARK;
        return Chronotype.PIGEON;
    };

    private static boolean isNightSession(SleepSession s) {
        return s.fallAsleep().toLocalTime().isAfter(LocalTime.of(18, 0))
                || s.fallAsleep().toLocalTime().isBefore(LocalTime.of(6, 0));
    }

    private static Chronotype classifyNight(SleepSession s) {
        LocalTime fall = s.fallAsleep().toLocalTime();
        LocalTime wake = s.wakeUp().toLocalTime();

        if (fall.isAfter(LocalTime.of(23, 0)) && wake.isAfter(LocalTime.of(9, 0))) {
            return Chronotype.OWL;
        }
        if (fall.isBefore(LocalTime.of(22, 0)) && wake.isBefore(LocalTime.of(7, 0))) {
            return Chronotype.LARK;
        }
        return Chronotype.PIGEON;
    }
}
