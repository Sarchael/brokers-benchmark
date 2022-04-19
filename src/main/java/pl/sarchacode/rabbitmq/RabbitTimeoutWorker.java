package pl.sarchacode.rabbitmq;

import pl.sarchacode.params.BenchmarkParameters;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

      saveStats(consumersStats, producersStats);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }

  private void saveStats(List<Long> consumersStats, List<Long> producersStats) throws IOException {
    int time = 5;
    FileWriter out = new FileWriter(createFileName());
    out.write("time,consuming,producing\n");
    for (int i = 0; i < consumersStats.size(); i++) {
      out.write(time + "," + consumersStats.get(i) + "," + producersStats.get(i) + "\n");
      time += 5;
    }
    out.close();
  }

  private String createFileName() {
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
    String fileName = df.format(new Date());
    fileName += "-" + benchmarkParameters.getBroker().name().toLowerCase();
    fileName += "-S" + benchmarkParameters.getMessageSize();
    fileName += "-Q" + benchmarkParameters.getNumberOfQueues();
    fileName += "-P" + benchmarkParameters.getNumberOfProducers();
    fileName += "-C" + benchmarkParameters.getNumberOfConsumers();
    fileName += ".csv";
    return fileName;
  }
}
