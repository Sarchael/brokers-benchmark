package pl.us.benchmark.params;

import pl.us.benchmark.Broker;

import java.util.List;

public class BenchmarkParametersParser {

  public static BenchmarkParameters parse(List<String> args) {
    BenchmarkParameters params = new BenchmarkParameters();
    validateArgs(args);

    for (int i = 0; i < args.size(); i += 2)
      setParameter(params, args.get(i), args.get(i + 1));

    validateParameters(params);
    return params;
  }

  private static void setParameter(BenchmarkParameters params, String key, String value) {
    switch (key) {
      case "-b", "--broker" -> params.setBroker(Broker.getByName(value));
      case "-l", "--localhost" -> params.setBrokerOnLocalhost(Boolean.parseBoolean(value));
      case "-q", "--queues" -> params.setNumberOfQueues(Integer.parseInt(value));
      case "-p", "--producers" -> params.setNumberOfProducers(Integer.parseInt(value));
      case "-c", "--consumers" -> params.setNumberOfConsumers(Integer.parseInt(value));
      case "-s", "--size" -> params.setMessageSize(Integer.parseInt(value));
      case "-t", "--time" -> params.setBenchmarkDuration(Integer.parseInt(value));
      case "-st", "--statistics-tool" -> params.setStatisticsTool(Boolean.parseBoolean(value));
      case "-pr", "--pair-results" -> params.setPairResults(Boolean.parseBoolean(value));
      default -> throw new IllegalArgumentException("Unknown parameter \"" + key + "\". Available parameters are: -b, -l, -q, -p, -c, -s, -t, -st, -pr");
    }
  }

  private static void validateArgs(List<String> args) {
    if (args == null || args.size() == 0)
      throw new IllegalArgumentException("No parameters available!");

    if (args.size() % 2 == 1)
      throw new IllegalArgumentException("Wrong parameters format!");
  }

  private static void validateParameters(BenchmarkParameters params) {
    if (Boolean.TRUE.equals(params.getStatisticsTool()))
      return;

    if (params.getNumberOfQueues() == null)
      throw new IllegalArgumentException("Number of queues has to be specified!");

    if (params.getBenchmarkDuration() == null)
      throw new IllegalArgumentException("Benchmark duration has to be specified!");

    if (params.getBroker() == null)
      throw new IllegalArgumentException("Broker has to be specified!");

    if (params.getMessageSize() == null)
      throw new IllegalArgumentException("Message size has to be specified!");

    if (params.getNumberOfProducers() != null &&
        (params.getNumberOfProducers() < params.getNumberOfQueues() ||
         params.getNumberOfProducers() % params.getNumberOfQueues() != 0))
      throw new IllegalArgumentException("Number of producers must be a multiple of number of queues!");

    if (params.getNumberOfConsumers() != null &&
        (params.getNumberOfConsumers() < params.getNumberOfQueues() ||
         params.getNumberOfConsumers() % params.getNumberOfQueues() != 0))
      throw new IllegalArgumentException("Number of consumers must be a multiple of number of queues!");
  }
}
