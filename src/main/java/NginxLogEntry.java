public record NginxLogEntry(
    String ipAddress,
    String dateTime,
    String request,
    int statusCode,
    int responseSize,
    String userAgent) {}
