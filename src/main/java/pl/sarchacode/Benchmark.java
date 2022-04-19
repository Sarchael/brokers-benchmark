package pl.sarchacode;

import pl.sarchacode.params.BenchmarkParameters;
import pl.sarchacode.params.BenchmarkParametersParser;
import pl.sarchacode.rabbitmq.RabbitConsumer;
import pl.sarchacode.rabbitmq.RabbitProducer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class Benchmark {
  private static final Logger logger = Logger.getLogger(Benchmark.class.getCanonicalName());

  private BenchmarkParameters benchmarkParameters;
  private List<Runnable> producerThreads = new ArrayList<>();
  private List<Runnable> consumerThreads = new ArrayList<>();

  public Benchmark(String[] args) {
    benchmarkParameters = BenchmarkParametersParser.parse(Arrays.asList(args));
  }

  public void start() {
    prepareProducerThreads();
    prepareConsumerThreads();
    runProducers();
    runConsumers();
  }

  private void prepareProducerThreads() {
    int queueNumber = 0;
    for (int i = 0; i < benchmarkParameters.getNumberOfProducers(); i++) {
      producerThreads.add(new RabbitProducer(i + 1,
                                             queueNumber + 1,
                                             prepareMessage(benchmarkParameters.getMessageSize()),
                                             Optional.ofNullable(benchmarkParameters.getNumberOfMessages())));
      if (++queueNumber >= benchmarkParameters.getNumberOfQueues())
        queueNumber = 0;
    }
  }

  private void prepareConsumerThreads() {
    int queueNumber = 0;
    for (int i = 0; i < benchmarkParameters.getNumberOfConsumers(); i++) {
      consumerThreads.add(new RabbitConsumer(i + 1,
                                             queueNumber + 1));
      if (++queueNumber >= benchmarkParameters.getNumberOfQueues())
        queueNumber = 0;
    }
  }

  private void runProducers() {
    producerThreads.forEach(x -> new Thread(x, "Producer-" + ((RabbitProducer) x).getWorkerNumber()).start());
    logger.info("Producers threads are running");
  }

  private void runConsumers() {
    consumerThreads.forEach(x -> new Thread(x, "Consumer-" + ((RabbitConsumer) x).getWorkerNumber()).start());
    logger.info("Consumers threads are running");
  }

  private String prepareMessage(Integer messageSize) {
    char[] chars = new char[messageSize];
    Arrays.fill(chars, 'x');
    return new String(chars);
  }
}
