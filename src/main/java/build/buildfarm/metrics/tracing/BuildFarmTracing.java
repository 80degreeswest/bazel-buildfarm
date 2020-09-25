package build.buildfarm.metrics.tracing;

public interface BuildFarmTracing {
  void sendTrace(String key, String value);
}
