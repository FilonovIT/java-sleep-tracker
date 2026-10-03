package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;

public record SleepSession(
        LocalDateTime fallAsleep,
        LocalDateTime wakeUp,
        SleepQuality quality
) {
    public long durationMinutes() {
        return java.time.Duration.between(fallAsleep, wakeUp).toMinutes();
    }
}
