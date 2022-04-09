package pl.sarchacode;

import pl.sarchacode.params.BenchmarkParameters;
import pl.sarchacode.params.BenchmarkParametersParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Benchmark {
  private static final Logger logger = Logger.getLogger(Benchmark.class.getCanonicalName());

  private BenchmarkParameters benchmarkParameters;
  private List<Runnable> producerThreads = new ArrayList<>();

  public Benchmark(String[] args) {
    benchmarkParameters = BenchmarkParametersParser.parse(Arrays.asList(args));
  }

  public void start() {
    prepareProducerThreads();
    runProducers();
  }

  private void prepareProducerThreads() {
    int queueNumber = 0;
    for (int i = 0; i < benchmarkParameters.getNumberOfProducers(); i++) {
      producerThreads.add(new Producer(i + 1,
                                       queueNumber + 1,
                                       prepareMessage(benchmarkParameters.getMessageSize()),
                                       benchmarkParameters.getNumberOfMessages()));
      if (++queueNumber >= benchmarkParameters.getNumberOfQueues())
        queueNumber = 0;
    }
    logger.info("Producers threads are ready");
  }

  private void runProducers() {
    producerThreads.forEach(x -> new Thread(x, "Producer-" + ((Producer) x).getProducerNumber()).start());
    logger.info("Producers threads are running");
  }

  private String prepareMessage(Integer numberOfMessages) {
    char[] chars = new char[numberOfMessages];
    Arrays.fill(chars, 'x');
    return new String(chars);
  }
}
