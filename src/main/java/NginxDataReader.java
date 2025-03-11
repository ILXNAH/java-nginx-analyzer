import io.prometheus.metrics.core.metrics.Counter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class NginxDataReader {
  static final Pattern LOG_PATTERN =
      Pattern.compile(
          "^(\\S+) - - \\[([^\\]]+)\\] \"([^\"]+)\" (\\d+) (\\d+) \"([^\"]*)\" \"(.+?)\"$",
          Pattern.UNIX_LINES);

  private static Counter statusCodesCounter;

  private NginxDataReader() {
    statusCodesCounter =
        Counter.builder()
            .name("nginxlog_status_codes_total")
            .help("Total number of status codes from nginx log")
            .labelNames("status")
            .register();
  }

  public static NginxDataReader create() {
    return new NginxDataReader();
  }

  public void processLogData() {
    try (InputStream inputStream =
            getClass().getClassLoader().getResourceAsStream("accesslog-filtered.log");
        BufferedReader reader =
            new BufferedReader(
                new InputStreamReader(
                    Objects.requireNonNull(inputStream), StandardCharsets.UTF_8))) {

      for (String l = reader.readLine(); l != null; l = reader.readLine()) {
        parseLine(l.trim()).ifPresent(NginxDataReader::metricLogEntry);
      }

    } catch (IOException e) {
      log.error("Error occurred while reading the file.", e);
    }
  }

  private static void metricLogEntry(NginxLogEntry nginxLogEntry) {
    statusCodesCounter.labelValues(String.valueOf(nginxLogEntry.statusCode())).inc();
  }

  static Optional<NginxLogEntry> parseLine(String line) {
    Matcher matcher = LOG_PATTERN.matcher(line);
    if (matcher.matches()) {
      String ipAddress = matcher.group(1);
      String dateTime = matcher.group(2);
      String request = matcher.group(3);
      int statusCode = Integer.parseInt(matcher.group(4));
      int responseSize = Integer.parseInt(matcher.group(5));
      String userAgent = matcher.group(6);

      Optional<NginxLogEntry> maybeNginxLogEntry =
          Optional.of(
              new NginxLogEntry(ipAddress, dateTime, request, statusCode, responseSize, userAgent));

      return maybeNginxLogEntry;
    }
    log.warn("Line does not match the pattern: {}", line);
    return Optional.empty();
  }
}
