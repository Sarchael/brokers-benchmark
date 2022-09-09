package pl.us.benchmark.kafka;

import pl.us.benchmark.params.BenchmarkParameters;
import pl.us.benchmark.tools.StatisticsTools;

import java.util.*;

public class ApacheKafkaTimeoutWorker extends TimerTask {
  private List<ApacheKafkaWorker> producers;
  private List<ApacheKafkaWorker> consumers;
  private ApacheKafkaStatisticsWorker statisticsWorker;
  private Timer statisticsTimer;
  private BenchmarkParameters benchmarkParameters;

  public ApacheKafkaTimeoutWorker(List<ApacheKafkaWorker> producers,
                                  List<ApacheKafkaWorker> consumers,
                                  ApacheKafkaStatisticsWorker statisticsWorker,
                                  Timer statisticsTimer,
                                  BenchmarkParameters benchmarkParameters) {
    this.producers = producers;
    this.consumers = consumers;
    this.statisticsWorker = statisticsWorker;
    this.statisticsTimer = statisticsTimer;
    this.benchmarkParameters = benchmarkParameters;
  }

  @Override
  public void run() {
    try {
      statisticsTimer.cancel();

      Thread.sleep(1000);
      List<Long> consumersStats = statisticsWorker.getConsumersStats();
      List<Long> producersStats = statisticsWorker.getProducersStats();
      statisticsWorker.cancel();

      producers.forEach(ApacheKafkaWorker::stopWorker);
      consumers.forEach(ApacheKafkaWorker::stopWorker);

      StatisticsTools.saveStats(consumersStats, producersStats, benchmarkParameters, OptionalLong.of(consumersStats.stream().mapToLong(x -> x * 5).sum()),
        OptionalLong.of(producersStats.stream().mapToLong(x -> x * 5).sum()), 1);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
