package pl.us.benchmark.kafka;

import pl.us.benchmark.params.BenchmarkParameters;
import pl.us.benchmark.tools.StatisticsTools;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

      Thread.sleep(5000);

      StatisticsTools.saveStats(consumersStats, producersStats, benchmarkParameters);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
