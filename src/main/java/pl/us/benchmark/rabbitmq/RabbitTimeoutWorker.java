package pl.us.benchmark.rabbitmq;

import pl.us.benchmark.params.BenchmarkParameters;
import pl.us.benchmark.tools.StatisticsTools;

import java.util.*;

public class RabbitTimeoutWorker extends TimerTask {
  private List<RabbitWorker> producers;
  private List<RabbitWorker> consumers;
  private RabbitStatisticsWorker statisticsWorker;
  private Timer statisticsTimer;
  private BenchmarkParameters benchmarkParameters;
  private RabbitApiClient rabbitApiClient;

  public RabbitTimeoutWorker(List<RabbitWorker> producers,
                             List<RabbitWorker> consumers,
                             RabbitStatisticsWorker statisticsWorker,
                             Timer statisticsTimer,
                             BenchmarkParameters benchmarkParameters) {
    this.producers = producers;
    this.consumers = consumers;
    this.statisticsWorker = statisticsWorker;
    this.statisticsTimer = statisticsTimer;
    this.benchmarkParameters = benchmarkParameters;
    this.rabbitApiClient = new RabbitApiClient(benchmarkParameters.getBrokerOnLocalhost());
  }

  @Override
  public void run() {
    try {
      RabbitStats rabbitStats = rabbitApiClient.getCurrentStats();

      statisticsTimer.cancel();

      Thread.sleep(1000);
      List<Long> consumersStats = statisticsWorker.getConsumersStats();
      List<Long> producersStats = statisticsWorker.getProducersStats();
      statisticsWorker.cancel();

      producers.forEach(RabbitWorker::stopWorker);
      consumers.forEach(Thread::interrupt);

      StatisticsTools.saveStats(consumersStats, producersStats, benchmarkParameters,
        OptionalLong.of(rabbitStats.getConsumedMessages()), OptionalLong.of(rabbitStats.getPublishedMessages()), 1);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }
}
