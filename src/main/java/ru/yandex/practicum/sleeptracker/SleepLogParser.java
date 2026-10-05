package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SleepLogParser {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static List<SleepSession> parse(String filePath) throws IOException {
        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            return lines
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(SleepLogParser::parseLine)
                    .collect(Collectors.toList());
        }
    }

    private static SleepSession parseLine(String line) {
        String[] parts = line.split(";");
        LocalDateTime fallAsleep = LocalDateTime.parse(parts[0].trim(), FORMATTER);
        LocalDateTime wakeUp = LocalDateTime.parse(parts[1].trim(), FORMATTER);
        SleepQuality quality = SleepQuality.valueOf(parts[2].trim());
        return new SleepSession(fallAsleep, wakeUp, quality);
    }
}
