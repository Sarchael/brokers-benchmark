package pl.us.benchmark.rabbitmq;

import pl.us.benchmark.MessagePool;
import pl.us.benchmark.params.BenchmarkParameters;
import pl.us.benchmark.tools.StatisticsTools;

import java.util.*;

public class RabbitOutOfMessagesWorker extends TimerTask {
  private List<RabbitWorker> consumers;
  private RabbitStatisticsWorker statisticsWorker;
  private Timer statisticsTimer;
  private Timer ownTimer;
  private BenchmarkParameters benchmarkParameters;
  private MessagePool messagePool;

  public RabbitOutOfMessagesWorker(List<RabbitWorker> consumers,
                                   RabbitStatisticsWorker statisticsWorker,
                                   Timer statisticsTimer,
                                   Timer ownTimer,
                                   BenchmarkParameters benchmarkParameters) {
    this.consumers = consumers;
    this.statisticsWorker = statisticsWorker;
    this.statisticsTimer = statisticsTimer;
    this.ownTimer = ownTimer;
    this.benchmarkParameters = benchmarkParameters;
    this.messagePool = MessagePool.getInstance();
  }

  @Override
  public void run() {
    if (consumers.stream().mapToLong(RabbitWorker::getProcessedMessages).sum() >= messagePool.getMessageCount()) {
      try {
        long finishTimestamp = System.currentTimeMillis();
        ownTimer.cancel();
        statisticsTimer.cancel();
        Thread.sleep(1000);

        List<Long> consumersStats = statisticsWorker.getConsumersStats();
        List<Long> producersStats = statisticsWorker.getProducersStats();

        statisticsWorker.cancel();
        OptionalLong consumersAvgTime = consumers.stream().mapToLong(x -> finishTimestamp - x.getStartTimestamp()).max();
        consumers.forEach(Thread::interrupt);

        RabbitConnectionFactory.getInstance().closeAllConnectionsForce();

        StatisticsTools.saveStats(consumersStats, producersStats, benchmarkParameters, consumersAvgTime, OptionalLong.empty(), 1000);
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        System.exit(0);
      }
    }
  }
}
