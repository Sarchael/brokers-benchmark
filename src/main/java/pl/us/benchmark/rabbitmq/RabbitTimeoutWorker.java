package pl.us.benchmark.rabbitmq;

import pl.us.benchmark.params.BenchmarkParameters;
import pl.us.benchmark.tools.StatisticsTools;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RabbitTimeoutWorker extends TimerTask {
  private List<RabbitWorker> producers;
  private List<RabbitWorker> consumers;
  private RabbitStatisticsWorker statisticsWorker;
  private Timer statisticsTimer;
  private BenchmarkParameters benchmarkParameters;

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
  }

  @Override
  public void run() {
    try {
      statisticsTimer.cancel();

      Thread.sleep(1000);
      List<Long> consumersStats = statisticsWorker.getConsumersStats();
      List<Long> producersStats = statisticsWorker.getProducersStats();
      statisticsWorker.cancel();

      for (RabbitWorker worker : producers)
        worker.stopWorker();
      consumers.forEach(Thread::interrupt);

      Thread.sleep(5000);
      RabbitConnectionFactory.getInstance().closeAllConnections();

      StatisticsTools.saveStats(consumersStats, producersStats, benchmarkParameters);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }
}
