package pl.sarchacode;

import pl.sarchacode.kafka.*;
import pl.sarchacode.params.BenchmarkParameters;
import pl.sarchacode.params.BenchmarkParametersParser;
import pl.sarchacode.rabbitmq.*;

import java.util.*;
import java.util.logging.Logger;

public class Benchmark {
  private static final Logger logger = Logger.getLogger(Benchmark.class.getCanonicalName());

  private BenchmarkParameters benchmarkParameters;
  private Timer statisticsWorkerTimer;

  private List<RabbitWorker> rabbitProducerThreads = new ArrayList<>();
  private List<RabbitWorker> rabbitConsumerThreads = new ArrayList<>();
  private RabbitStatisticsWorker rabbitStatisticsWorkerThread;

  private List<ApacheKafkaWorker> kafkaProducerThreads = new ArrayList<>();
  private List<ApacheKafkaWorker> kafkaConsumerThreads = new ArrayList<>();
  private ApacheKafkaStatisticsWorker kafkaStatisticsWorkerThread;

  public Benchmark(String[] args) {
    benchmarkParameters = BenchmarkParametersParser.parse(Arrays.asList(args));
  }

  public void start() {
    switch (benchmarkParameters.getBroker()) {
      case RABBITMQ -> startRabbitBenchmark();
      case KAFKA -> startKafkaBenchmark();
    }
  }

  public void startRabbitBenchmark() {
    prepareRabbitProducerThreads();
    prepareRabbitConsumerThreads();
    runRabbitProducers();
    runRabbitConsumers();
    runRabbitStatsProvider();
    if (benchmarkParameters.getBenchmarkDuration() != null)
      runRabbitTimeoutTimer();
  }

  public void startKafkaBenchmark() {
    prepareKafkaProducerThreads();
    prepareKafkaConsumerThreads();
    runKafkaProducers();
    runKafkaConsumers();
    runKafkaStatsProvider();
    if (benchmarkParameters.getBenchmarkDuration() != null)
      runKafkaTimeoutTimer();
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
    logger.info("Consumers threads are running");
  }

  private void runKafkaProducers() {
    kafkaProducerThreads.forEach(x -> new Thread(x, "Producer-" + x.getWorkerNumber()).start());
    logger.info("Producers threads are running");
  }

  private void prepareKafkaConsumerThreads() {
    int topicNumber = 0;
    for (int i = 0; i < benchmarkParameters.getNumberOfConsumers(); i++) {
      kafkaConsumerThreads.add(new ApacheKafkaConsumer(i + 1,
                                                       topicNumber + 1,
                                                       Optional.ofNullable(benchmarkParameters.getBrokerOnLocalhost())));
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
                                                       Optional.ofNullable(benchmarkParameters.getBrokerOnLocalhost())));
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
                                                   Optional.ofNullable(benchmarkParameters.getBrokerOnLocalhost())));
      if (++queueNumber >= benchmarkParameters.getNumberOfQueues())
        queueNumber = 0;
    }
  }

  private void prepareRabbitConsumerThreads() {
    int queueNumber = 0;
    for (int i = 0; i < benchmarkParameters.getNumberOfConsumers(); i++) {
      rabbitConsumerThreads.add(new RabbitConsumer(i + 1,
                                                   queueNumber + 1,
                                                   Optional.ofNullable(benchmarkParameters.getBrokerOnLocalhost())));
      if (++queueNumber >= benchmarkParameters.getNumberOfQueues())
        queueNumber = 0;
    }
  }

  private void runRabbitProducers() {
    rabbitProducerThreads.forEach(x -> new Thread(x, "Producer-" + x.getWorkerNumber()).start());
    logger.info("Producers threads are running");
  }

  private void runRabbitConsumers() {
    rabbitConsumerThreads.forEach(x -> new Thread(x, "Consumer-" + x.getWorkerNumber()).start());
    logger.info("Consumers threads are running");
  }

  private String prepareMessage(Integer messageSize) {
    char[] chars = new char[messageSize];
    Arrays.fill(chars, 'x');
    return new String(chars);
  }
}
