import io.prometheus.metrics.exporter.httpserver.HTTPServer;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {
  public static void main(String[] args) {
    log.info("Application is running. Press 'e' to stop.");

    try (HTTPServer server = HTTPServer.builder().port(9400).buildAndStart()) {
      JvmMetrics.builder().register();
      log.info("HTTPServer listening on http://localhost:{}/metrics", server.getPort());
      NginxDataReader nginxDataReader = NginxDataReader.create();
      nginxDataReader.processLogData();

      while (true) {
        if (System.in.available() > 0) {
          int input = System.in.read();
          if (input == 'e' || input == 'E') {
            log.info("Exiting application.");
            break;
          } else {
            log.info("You pressed: {}", (char) input);
          }
        }
      }
    } catch (IOException e) {
      log.error("Error occurred while running the application.", e);
    }
  }
}
