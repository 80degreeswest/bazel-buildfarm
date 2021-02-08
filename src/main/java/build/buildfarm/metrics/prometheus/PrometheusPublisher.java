package build.buildfarm.metrics.prometheus;

import io.prometheus.client.Summary;
import io.prometheus.client.exporter.HTTPServer;
import java.io.IOException;
import java.util.logging.Logger;

public class PrometheusPublisher {
  private static final Logger logger = Logger.getLogger(PrometheusPublisher.class.getName());
  private static HTTPServer server;
  private static final Summary queueSizeSummary = Summary.build().name("queue_size").help("Queue Size.").register();
  private static final Summary preQueueSizeSummary = Summary.build().name("pre_queue_size").help("Pre Queue Size.").register();
  private static final Summary dispatchedOperationsSummary = Summary.build().name("dispatched_operations_size").help("Dispatched Operations Size.").register();
  private static final Summary workerPoolSizeSummary = Summary.build().name("worker_pool_size").help("Active Worker Pool Size.").register();

  public static void startHttpServer(int port) {
    try {
      server = new HTTPServer(port);
      logger.info("Started Prometheus HTTP Server on port " + port);
    } catch (IOException e) {
      logger.severe("Could not start Prometheus HTTP Server on port " + port);
    }
  }

  public static void stopHttpServer() {
    server.stop();
  }

  public static void updateQueueSize(int queueSize) {
    queueSizeSummary.observe(queueSize);
  }

  public static void updatePreQueueSize(int preQueueSize) {
    preQueueSizeSummary.observe(preQueueSize);
  }

  public static void updateDispatchedOperationsSize(int numDispatchedOperations) {
    dispatchedOperationsSummary.observe(numDispatchedOperations);
  }

  public static void updateWorkerPoolSize(int numWorkers) {
    workerPoolSizeSummary.observe(numWorkers);
  }
}
