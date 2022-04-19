package pl.sarchacode.params;

import pl.sarchacode.Broker;

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
      case "-l", "--localhost" -> params.setBrokerOnLocalhost(Boolean.getBoolean(value));
      case "-q", "--queues" -> params.setNumberOfQueues(Integer.parseInt(value));
      case "-p", "--producers" -> params.setNumberOfProducers(Integer.parseInt(value));
      case "-c", "--consumers" -> params.setNumberOfConsumers(Integer.parseInt(value));
      case "-n", "--messages" -> params.setNumberOfMessages(Integer.parseInt(value));
      case "-s", "--size" -> params.setMessageSize(Integer.parseInt(value));
      default -> throw new IllegalArgumentException("Unknown parameter \"" + key + "\". Available parameters are: -b, -l, -q, -p, -c, -n, -s");
    }
  }

  private static void validateArgs(List<String> args) {
    if (args == null || args.size() == 0)
      throw new IllegalArgumentException("No parameters available");

    if (args.size() % 2 == 1)
      throw new IllegalArgumentException("Wrong parameters format");
  }

  private static void validateParameters(BenchmarkParameters params) {
    if (params.getNumberOfProducers() < params.getNumberOfQueues() ||
        params.getNumberOfProducers() % params.getNumberOfQueues() != 0)
      throw new IllegalArgumentException("Number of producers must be a multiple of number of queues!");

    if (params.getNumberOfConsumers() < params.getNumberOfQueues() ||
        params.getNumberOfConsumers() % params.getNumberOfQueues() != 0)
      throw new IllegalArgumentException("Number of consumers must be a multiple of number of queues!");
  }
}
