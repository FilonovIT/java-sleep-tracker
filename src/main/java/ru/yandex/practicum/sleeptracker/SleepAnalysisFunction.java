package ru.yandex.practicum.sleeptracker;

import java.util.List;

@FunctionalInterface
public interface SleepAnalysisFunction {
    Object apply(List<SleepSession> sessions);
}