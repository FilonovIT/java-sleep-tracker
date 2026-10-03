package ru.yandex.practicum.sleeptracker;

public class SleepAnalysisFunctions {

    public static final SleepAnalysisFunction SESSION_COUNT =
            sessions -> sessions.size();

    public static final SleepAnalysisFunction MIN_DURATION =
            sessions -> sessions.stream()
                    .mapToLong(SleepSession::durationMinutes)
                    .min()
                    .orElse(0);

    public static final SleepAnalysisFunction MAX_DURATION =
            sessions -> sessions.stream()
                    .mapToLong(SleepSession::durationMinutes)
                    .max()
                    .orElse(0);

    public static final SleepAnalysisFunction AVERAGE_DURATION =
            sessions -> sessions.stream()
                    .mapToLong(SleepSession::durationMinutes)
                    .average()
                    .orElse(0.0);

    public static final SleepAnalysisFunction BAD_QUALITY_COUNT =
            sessions -> sessions.stream()
                    .filter(s -> s.quality() == SleepQuality.BAD)
                    .count();
}
