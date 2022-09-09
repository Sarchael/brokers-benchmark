package pl.us.benchmark;

import pl.us.benchmark.kafka.*;
import pl.us.benchmark.params.BenchmarkParameters;
import pl.us.benchmark.params.BenchmarkParametersParser;
import pl.us.benchmark.rabbitmq.*;
import pl.us.benchmark.tools.StatisticsTools;

import java.io.IOException;
import java.util.*;

public class Benchmark {

  private BenchmarkParameters benchmarkParameters;
  private Timer statisticsWorkerTimer;

  private List<RabbitWorker> rabbitProducerThreads = new ArrayList<>();
  private List<RabbitWorker> rabbitConsumerThreads = new ArrayList<>();
  private RabbitStatisticsWorker rabbitStatisticsWorkerThread;

  private List<ApacheKafkaWorker> kafkaProducerThreads = new ArrayList<>();
  private List<ApacheKafkaWorker> kafkaConsumerThreads = new ArrayList<>();
  private ApacheKafkaStatisticsWorker kafkaStatisticsWorkerThread;

  private long startTimestamp;

  public Benchmark(String[] args) {
    benchmarkParameters = BenchmarkParametersParser.parse(Arrays.asList(args));
  }

  public void start() {
    if (Boolean.TRUE.equals(benchmarkParameters.getStatisticsTool())) {
      try {
        StatisticsTools.generateFullStats(benchmarkParameters.getPairResults());
      } catch (IOException exc) {
        exc.printStackTrace();
      }
    } else {
      if (benchmarkParameters.getNumberOfMessages() != null)
        MessagePool.getInstance()
                   .setParams(benchmarkParameters.getNumberOfMessages(),
                              benchmarkParameters.getPackageSize());
      switch (benchmarkParameters.getBroker()) {
        case RABBITMQ -> startRabbitBenchmark();
        case KAFKA -> startKafkaBenchmark();
      }
    }
  }

  public void startRabbitBenchmark() {
    if (benchmarkParameters.getNumberOfProducers() != null)
      prepareRabbitProducerThreads();
    if (benchmarkParameters.getNumberOfConsumers() != null)
      prepareRabbitConsumerThreads();
    this.startTimestamp = System.currentTimeMillis();
    if (benchmarkParameters.getNumberOfProducers() != null)
      runRabbitProducers();
    if (benchmarkParameters.getNumberOfConsumers() != null)
      runRabbitConsumers();
    runRabbitStatsProvider();
    if (benchmarkParameters.getBenchmarkDuration() != null)
      runRabbitTimeoutTimer();
    else if (benchmarkParameters.getNumberOfConsumers() != null)
      runRabbitOutOfMessagesTimer();
    else
      waitAndSaveRabbitStats();
  }

  public void startKafkaBenchmark() {
    if (benchmarkParameters.getNumberOfProducers() != null)
      prepareKafkaProducerThreads();
    if (benchmarkParameters.getNumberOfConsumers() != null)
      prepareKafkaConsumerThreads();
    if (benchmarkParameters.getNumberOfProducers() != null)
      runKafkaProducers();
    if (benchmarkParameters.getNumberOfConsumers() != null)
      runKafkaConsumers();
    runKafkaStatsProvider();
    if (benchmarkParameters.getBenchmarkDuration() != null)
      runKafkaTimeoutTimer();
    else
      waitAndSaveKafkaStats();
  }

  private void runKafkaTimeoutTimer() {
    Timer timer = new Timer("Timeout");
    TimerTask task = new ApacheKafkaTimeoutWorker(kafkaProducerThreads, kafkaConsumerThreads, kafkaStatisticsWorkerThread,
                                                  statisticsWorkerTimer, benchmarkParameters);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.SECOND, benchmarkParameters.getBenchmarkDuration());
    timer.schedule(task, calendar.getTime());
  }

  private void runKafkaStatsProvider() {
    Timer timer = new Timer("Statistics");
    ApacheKafkaStatisticsWorker task = new ApacheKafkaStatisticsWorker(kafkaProducerThreads, kafkaConsumerThreads);
    timer.schedule(task, 5000L, 5000L);
    kafkaStatisticsWorkerThread = task;
    statisticsWorkerTimer = timer;
  }

  private void runKafkaConsumers() {
    kafkaConsumerThreads.forEach(x -> new Thread(x, "Consumer-" + x.getWorkerNumber()).start());
  }

  private void runKafkaProducers() {
    kafkaProducerThreads.forEach(x -> new Thread(x, "Producer-" + x.getWorkerNumber()).start()); }

  private void prepareKafkaConsumerThreads() {
    int topicNumber = 0;
    for (int i = 0; i < benchmarkParameters.getNumberOfConsumers(); i++) {
      kafkaConsumerThreads.add(new ApacheKafkaConsumer(i + 1,
                                                       topicNumber + 1,
                                                       Optional.ofNullable(benchmarkParameters.getBrokerOnLocalhost()),
                                                       benchmarkParameters.getNumberOfMessages() == null));
      if (++topicNumber >= benchmarkParameters.getNumberOfQueues())
        topicNumber = 0;
    }
  }

  private void prepareKafkaProducerThreads() {
    int topicNumber = 0;
    for (int i = 0; i < benchmarkParameters.getNumberOfProducers(); i++) {
      kafkaProducerThreads.add(new ApacheKafkaProducer(i + 1,
                                                       topicNumber + 1,
                                                       prepareMessage(benchmarkParameters.getMessageSize()),
                                                       Optional.ofNullable(benchmarkParameters.getBrokerOnLocalhost()),
                                                       benchmarkParameters.getNumberOfMessages() == null));
      if (++topicNumber >= benchmarkParameters.getNumberOfQueues())
        topicNumber = 0;
    }
  }

  private void runRabbitTimeoutTimer() {
    Timer timer = new Timer("Timeout");
    TimerTask task = new RabbitTimeoutWorker(rabbitProducerThreads, rabbitConsumerThreads, rabbitStatisticsWorkerThread,
                                             statisticsWorkerTimer, benchmarkParameters);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.SECOND, benchmarkParameters.getBenchmarkDuration());
    timer.schedule(task, calendar.getTime());
  }

  private void runRabbitOutOfMessagesTimer() {
    Timer timer = new Timer("OutOfMessages");
    TimerTask task = new RabbitOutOfMessagesWorker(rabbitConsumerThreads, rabbitStatisticsWorkerThread,
      statisticsWorkerTimer, timer, benchmarkParameters);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.SECOND, 5);
    timer.schedule(task, calendar.getTime(), 1000);
  }

  private void runRabbitStatsProvider() {
    Timer timer = new Timer("Statistics");
    RabbitStatisticsWorker task = new RabbitStatisticsWorker(rabbitProducerThreads, rabbitConsumerThreads);
    timer.schedule(task, 5000L, 5000L);
    rabbitStatisticsWorkerThread = task;
    statisticsWorkerTimer = timer;
  }

  private void prepareRabbitProducerThreads() {
    int queueNumber = 0;
    for (int i = 0; i < benchmarkParameters.getNumberOfProducers(); i++) {
      rabbitProducerThreads.add(new RabbitProducer(i + 1,
                                                   queueNumber + 1,
                                                   prepareMessage(benchmarkParameters.getMessageSize()),
                                                   Optional.ofNullable(benchmarkParameters.getBrokerOnLocalhost()),
                                                   benchmarkParameters.getNumberOfMessages() == null));
      if (++queueNumber >= benchmarkParameters.getNumberOfQueues())
        queueNumber = 0;
    }
  }

  private void prepareRabbitConsumerThreads() {
    int queueNumber = 0;
    for (int i = 0; i < benchmarkParameters.getNumberOfConsumers(); i++) {
      rabbitConsumerThreads.add(new RabbitConsumer(i + 1,
                                                   queueNumber + 1,
                                                   Optional.ofNullable(benchmarkParameters.getBrokerOnLocalhost()),
                                                   benchmarkParameters.getNumberOfMessages() == null));
      if (++queueNumber >= benchmarkParameters.getNumberOfQueues())
        queueNumber = 0;
    }
  }

  private void runRabbitProducers() {
    rabbitProducerThreads.forEach(x -> new Thread(x, "Producer-" + x.getWorkerNumber()).start());
  }

  private void runRabbitConsumers() {
    rabbitConsumerThreads.forEach(x -> new Thread(x, "Consumer-" + x.getWorkerNumber()).start());
  }

  private String prepareMessage(Integer messageSize) {
    char[] chars = new char[messageSize];
    Arrays.fill(chars, 'x');
    return new String(chars);
  }

  private void waitAndSaveRabbitStats() {
    try {
      if (rabbitProducerThreads != null && !rabbitProducerThreads.isEmpty()) {
        boolean stop = false;
        while (!stop) {
          Thread.sleep(500);
          stop = true;
          for (RabbitWorker rabbitWorker : rabbitProducerThreads)
            stop &= !rabbitWorker.isRunning();
        }
      }

      RabbitConnectionFactory.getInstance().closeAllConnectionsForce();

      long finishTimestamp = System.currentTimeMillis();

      statisticsWorkerTimer.cancel();
      Thread.sleep(1000);
      List<Long> consumersStats = rabbitStatisticsWorkerThread.getConsumersStats();
      List<Long> producersStats = rabbitStatisticsWorkerThread.getProducersStats();
      rabbitStatisticsWorkerThread.cancel();
      OptionalLong producersAvgTime = OptionalLong.of(finishTimestamp - this.startTimestamp);

      StatisticsTools.saveStats(consumersStats, producersStats, benchmarkParameters, OptionalLong.empty(), producersAvgTime, 1000);
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
    }
  }

  private void waitAndSaveKafkaStats() {
    try {
      if (kafkaProducerThreads != null && !kafkaProducerThreads.isEmpty()) {
        boolean stop = false;
        while (!stop) {
          Thread.sleep(500);
          stop = true;
          for (ApacheKafkaWorker kafkaWorker : kafkaProducerThreads)
            stop &= !kafkaWorker.isRunning();
        }
      }

      if (kafkaConsumerThreads != null && !kafkaConsumerThreads.isEmpty()) {
        boolean stop = false;
        while (!stop) {
          Thread.sleep(500);
          stop = true;
          for (ApacheKafkaWorker kafkaWorker : kafkaConsumerThreads)
            stop &= !kafkaWorker.isRunning();
        }
      }

      statisticsWorkerTimer.cancel();
      Thread.sleep(1000);
      List<Long> consumersStats = kafkaStatisticsWorkerThread.getConsumersStats();
      List<Long> producersStats = kafkaStatisticsWorkerThread.getProducersStats();
      kafkaStatisticsWorkerThread.cancel();
      OptionalLong consumersAvgTime = OptionalLong.empty();
      OptionalLong producersAvgTime = OptionalLong.empty();

      if (kafkaConsumerThreads != null && !kafkaConsumerThreads.isEmpty()) {
        consumersAvgTime = OptionalLong.of(kafkaConsumerThreads.stream().mapToLong(x -> x.getFinishTimestamp()).max().getAsLong() -
          kafkaConsumerThreads.stream().mapToLong(x -> x.getStartTimestamp()).min().getAsLong());
      }
      if (kafkaProducerThreads != null && !kafkaProducerThreads.isEmpty()) {
        producersAvgTime = OptionalLong.of(kafkaProducerThreads.stream().mapToLong(x -> x.getFinishTimestamp()).max().getAsLong() -
          kafkaProducerThreads.stream().mapToLong(x -> x.getStartTimestamp()).min().getAsLong());
      }

      StatisticsTools.saveStats(consumersStats, producersStats, benchmarkParameters, consumersAvgTime, producersAvgTime, 1000);
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
    }
  }
}
