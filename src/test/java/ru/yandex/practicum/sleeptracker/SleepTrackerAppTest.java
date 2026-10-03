package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepTrackerAppTest {
    private SleepSession session(String from, String to, SleepQuality q) {
        return new SleepSession(
                LocalDateTime.parse(from),
                LocalDateTime.parse(to),
                q
        );
    }

    // --- SESSION_COUNT ---
    @Test
    void sessionCountEmpty() {
        assertEquals(0, SleepAnalysisFunctions.SESSION_COUNT.apply(List.of()));
    }

    @Test
    void sessionCountNonEmpty() {
        List<SleepSession> s = List.of(
                session("2025-10-01T22:15", "2025-10-02T08:00", SleepQuality.GOOD),
                session("2025-10-02T23:00", "2025-10-03T08:00", SleepQuality.NORMAL)
        );
        assertEquals(2, SleepAnalysisFunctions.SESSION_COUNT.apply(s));
    }

    // --- MIN/MAX/AVERAGE ---
    @Test
    void minDuration() {
        List<SleepSession> s = List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD), // 480
                session("2025-10-02T23:00", "2025-10-03T07:00", SleepQuality.NORMAL) // 480
        );
        assertEquals(480L, SleepAnalysisFunctions.MIN_DURATION.apply(s));
    }

    @Test
    void maxDuration() {
        List<SleepSession> s = List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD),
                session("2025-10-02T23:00", "2025-10-03T09:00", SleepQuality.NORMAL)
        );
        assertEquals(600L, SleepAnalysisFunctions.MAX_DURATION.apply(s));
    }

    @Test
    void averageDuration() {
        List<SleepSession> s = List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD), // 480
                session("2025-10-02T23:00", "2025-10-03T09:00", SleepQuality.NORMAL) // 600
        );
        assertEquals(540.0, SleepAnalysisFunctions.AVERAGE_DURATION.apply(s));
    }

    // --- BAD_QUALITY_COUNT ---
    @Test
    void badQualityCount() {
        List<SleepSession> s = List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.BAD),
                session("2025-10-02T23:00", "2025-10-03T09:00", SleepQuality.GOOD),
                session("2025-10-03T23:00", "2025-10-04T09:00", SleepQuality.BAD)
        );
        assertEquals(2L, SleepAnalysisFunctions.BAD_QUALITY_COUNT.apply(s));
    }

    @Test
    void badQualityCountNone() {
        List<SleepSession> s = List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD)
        );
        assertEquals(0L, SleepAnalysisFunctions.BAD_QUALITY_COUNT.apply(s));
    }

    // --- SLEEPLESS NIGHTS ---
    @Test
    void noSleeplessNights() {
        List<SleepSession> s = List.of(
                session("2025-10-01T22:00", "2025-10-02T08:00", SleepQuality.GOOD)
        );
        assertEquals(0L, SleeplessNights.SLEEPLESS_NIGHTS_COUNT.apply(s));
    }

    @Test
    void oneSleeplessNight() {
        // Спим днём, ночью не спим
        List<SleepSession> s = List.of(
                session("2025-10-01T14:00", "2025-10-01T16:00", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T16:00", SleepQuality.GOOD)
        );
        assertEquals(1L, SleeplessNights.SLEEPLESS_NIGHTS_COUNT.apply(s));
    }

    @Test
    void sleepCrossesMidnight() {
        List<SleepSession> s = List.of(
                session("2025-10-01T23:00", "2025-10-02T03:00", SleepQuality.GOOD)
        );
        assertEquals(0L, SleeplessNights.SLEEPLESS_NIGHTS_COUNT.apply(s));
    }

    @Test
    void firstSessionAfterNoon() {
        // Первая сессия после 12:00 — потенциальная ночь следующая
        List<SleepSession> s = List.of(
                session("2025-10-01T14:00", "2025-10-01T16:00", SleepQuality.GOOD)
        );
        assertEquals(0L, SleeplessNights.SLEEPLESS_NIGHTS_COUNT.apply(s));
    }

    // --- CHRONOTYPE ---
    @Test
    void owlChronotype() {
        List<SleepSession> s = List.of(
                session("2025-10-01T23:30", "2025-10-02T10:00", SleepQuality.GOOD),
                session("2025-10-02T23:30", "2025-10-03T10:00", SleepQuality.GOOD)
        );
        assertEquals(Chronotype.OWL, ChronotypeAnalyzer.CHRONOTYPE.apply(s));
    }

    @Test
    void larkChronotype() {
        List<SleepSession> s = List.of(
                session("2025-10-01T21:00", "2025-10-02T06:00", SleepQuality.GOOD),
                session("2025-10-02T21:00", "2025-10-03T06:00", SleepQuality.GOOD)
        );
        assertEquals(Chronotype.LARK, ChronotypeAnalyzer.CHRONOTYPE.apply(s));
    }

    @Test
    void tieResultsInPigeon() {
        List<SleepSession> s = List.of(
                session("2025-10-01T23:30", "2025-10-02T10:00", SleepQuality.GOOD), // OWL
                session("2025-10-02T21:00", "2025-10-03T06:00", SleepQuality.GOOD)  // LARK
        );
        assertEquals(Chronotype.PIGEON, ChronotypeAnalyzer.CHRONOTYPE.apply(s));
    }

    @Test
    void daySessionsIgnoredInChronotype() {
        List<SleepSession> s = List.of(
                session("2025-10-01T14:00", "2025-10-01T16:00", SleepQuality.GOOD),
                session("2025-10-01T23:30", "2025-10-02T10:00", SleepQuality.GOOD)
        );
        assertEquals(Chronotype.OWL, ChronotypeAnalyzer.CHRONOTYPE.apply(s));
    }
}