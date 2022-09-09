package pl.us.benchmark.tools;

import pl.us.benchmark.Broker;
import pl.us.benchmark.params.BenchmarkParameters;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsTools {

  public static void saveStats(List<Long> consumersStats, List<Long> producersStats,
                               BenchmarkParameters benchmarkParameters, OptionalLong consumerSummary,
                               OptionalLong producerSummary, int division) throws IOException {
    int time = 5;
    FileWriter out = new FileWriter(createFileName(benchmarkParameters));
    out.write("time,consuming,producing\n");
    int size = 0;
    if (consumersStats.size() > producersStats.size())
      size = consumersStats.size();
    else
      size = producersStats.size();
    for (int i = 0; i < size; i++) {
      out.write(time + "," + (i < consumersStats.size() ? consumersStats.get(i) : 0) + "," +
                                 (i < producersStats.size() ? producersStats.get(i) : 0) + "\n");
      time += 5;
    }
    if (consumerSummary.isPresent() || producerSummary.isPresent()) {
      out.write("x," + (int)(consumerSummary.orElse(0) / division) + "," + (int)(producerSummary.orElse(0) / division));
    }
    out.close();
  }

  public static void generateFullStats(Boolean pairResults) throws IOException {
    File folder = new File(".");
    List<File> listOfFiles = Arrays.asList(folder.listFiles());
    Map<String, Map<StatisticType, Object>> parsedFiles = new HashMap<>();

    for (File file : listOfFiles) {
      if (file.getName().endsWith(".csv") && !file.getName().endsWith("FullStats.csv") && !file.getName().endsWith("FullStatsPaired.csv")) {
        Map<StatisticType, Object> records = getRecordsFromFile(file);
        parsedFiles.put(file.getName().substring(16, file.getName().lastIndexOf('.')), records);
      }
    }

    List<Statistic> statistics =
      parsedFiles.entrySet()
                 .stream()
                 .map(x -> generateStatisticFromRawData(x.getKey(), x.getValue()))
                 .collect(Collectors.toList());

    if (Boolean.TRUE.equals(pairResults)) {
      List<Map<Broker, Statistic>> pairedStatsList = new ArrayList<>();
      for (Statistic statistic : statistics) {
        if (statistic.getBroker() == Broker.KAFKA) {
          for (Statistic pair : statistics) {
            if (pair.getBroker() == Broker.RABBITMQ && statistic.getSize().equals(pair.getSize()) &&
                statistic.getQueues().equals(pair.getQueues()) && statistic.getConsumers().equals(pair.getConsumers()) &&
                statistic.getProducers().equals(pair.getProducers()) && (statistic.getNumberOfMessages().equals(pair.getNumberOfMessages()) ||
                (statistic.getPairingString() != null && statistic.getPairingString().equals(pair.getPairingString())))) {
              Map<Broker, Statistic> pairedStats = new HashMap<>();
              pairedStats.put(statistic.getBroker(), statistic);
              pairedStats.put(pair.getBroker(), pair);
              pairedStatsList.add(pairedStats);
            }
          }
        }
      }
      saveFullStatsPaired(pairedStatsList);
    } else {
      saveFullStats(statistics);
    }
  }

  private static Statistic generateStatisticFromRawData(String name, Map<StatisticType, Object> results) {
    Statistic statistic = new Statistic();
    statistic.setName(name);
    List<String> parameters = Arrays.asList(name.split("-"));
    fillStatisticWithBenchmarkParams(statistic, parameters);
    if (statistic.getNumberOfMessages() != null && statistic.getNumberOfMessages() != 0)
      fillStatisticWithBenchmarkResultsMessageCountMode(statistic, results);
    else
      fillStatisticWithBenchmarkResultsTimeMode(statistic, results);
    return statistic;
  }

  private static void fillStatisticWithBenchmarkParams(Statistic statistic, List<String> parameters) {
    parameters.forEach(x -> {
      switch(x.charAt(0)) {
        case 'S' -> statistic.setSize(Integer.parseInt(x.substring(1)));
        case 'Q' -> statistic.setQueues(Integer.parseInt(x.substring(1)));
        case 'P' -> statistic.setProducers(Integer.parseInt(x.substring(1)));
        case 'C' -> statistic.setConsumers(Integer.parseInt(x.substring(1)));
        case 'N' -> statistic.setNumberOfMessages(Integer.parseInt(x.substring(1)));
        case 'R' -> statistic.setPairingString(x.substring(1));
        default -> statistic.setBroker(Broker.getByName(x));
      }
    });
  }

  private static void fillStatisticWithBenchmarkResultsMessageCountMode(Statistic statistic, Map<StatisticType, Object> resultsMap) {
    List<Integer> overall = null;
    if (resultsMap.containsKey(StatisticType.OVERALL))
      overall = (List<Integer>) resultsMap.get(StatisticType.OVERALL);

    statistic.setConsumingTime(overall.get(0));
    statistic.setProducingTime(overall.get(1));

    if (statistic.getConsumers() != null && statistic.getConsumers() != 0) {
      statistic.setTotalThroughputMessagesIn(statistic.getNumberOfMessages());
      statistic.setQueueThroughputMessagesIn(statistic.getTotalThroughputMessagesIn() / statistic.getQueues());
      statistic.setTotalThroughputTransferIn((int) (((long) statistic.getNumberOfMessages() * statistic.getSize()) / (1024 * 1024)));
      statistic.setQueueThroughputTransferIn(statistic.getTotalThroughputTransferIn() / statistic.getQueues());

      statistic.setTotalThroughputMessagesOut(0);
      statistic.setQueueThroughputMessagesOut(0);
      statistic.setTotalThroughputTransferOut(0);
      statistic.setQueueThroughputTransferOut(0);
    } else {
      statistic.setTotalThroughputMessagesOut(statistic.getNumberOfMessages());
      statistic.setQueueThroughputMessagesOut(statistic.getTotalThroughputMessagesOut() / statistic.getQueues());
      statistic.setTotalThroughputTransferOut((int) (((long) statistic.getNumberOfMessages() * statistic.getSize()) / (1024 * 1024)));
      statistic.setQueueThroughputTransferOut(statistic.getTotalThroughputTransferOut() / statistic.getQueues());

      statistic.setTotalThroughputMessagesIn(0);
      statistic.setQueueThroughputMessagesIn(0);
      statistic.setTotalThroughputTransferIn(0);
      statistic.setQueueThroughputTransferIn(0);
    }
  }

  private static void fillStatisticWithBenchmarkResultsTimeMode(Statistic statistic, Map<StatisticType, Object> resultsMap) {
    List<Integer> overall = null;
    if (resultsMap.containsKey(StatisticType.OVERALL))
      overall = (List<Integer>) resultsMap.get(StatisticType.OVERALL);

    statistic.setTotalThroughputMessagesIn(overall.get(0));
    statistic.setQueueThroughputMessagesIn(statistic.getTotalThroughputMessagesIn() / statistic.getQueues());
    statistic.setTotalThroughputTransferIn((int) (((long) statistic.getTotalThroughputMessagesIn() * statistic.getSize()) / (1024 * 1024)));
    statistic.setQueueThroughputTransferIn((int) (((long) statistic.getQueueThroughputMessagesIn() * statistic.getSize()) / (1024 * 1024)));

    statistic.setTotalThroughputMessagesOut(overall.get(1));
    statistic.setQueueThroughputMessagesOut(statistic.getTotalThroughputMessagesOut() / statistic.getQueues());
    statistic.setTotalThroughputTransferOut((int) (((long) statistic.getTotalThroughputMessagesOut() * statistic.getSize()) / (1024 * 1024)));
    statistic.setQueueThroughputTransferOut((int) (((long) statistic.getQueueThroughputMessagesOut() * statistic.getSize()) / (1024 * 1024)));

    statistic.setConsumingTime(0);
    statistic.setProducingTime(0);
  }

  private static String createFileName(BenchmarkParameters benchmarkParameters) {
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
    String fileName = df.format(new Date());
    fileName += "-" + benchmarkParameters.getBroker().name().toLowerCase();
    fileName += "-S" + benchmarkParameters.getMessageSize();
    fileName += "-Q" + benchmarkParameters.getNumberOfQueues();
    fileName += "-P" + (benchmarkParameters.getNumberOfProducers() != null ? benchmarkParameters.getNumberOfProducers() : 0);
    fileName += "-C" + (benchmarkParameters.getNumberOfConsumers() != null ? benchmarkParameters.getNumberOfConsumers() : 0);
    fileName += "-N" + (benchmarkParameters.getNumberOfMessages() != null ? benchmarkParameters.getNumberOfMessages() : 0);
    if (benchmarkParameters.getPairingString() != null)
      fileName += "-R" + benchmarkParameters.getPairingString();
    fileName += ".csv";
    return fileName;
  }

  private static Map<StatisticType, Object> getRecordsFromFile(File file) throws IOException  {
    Map<StatisticType, Object> results = new HashMap<>();
    List<List<Integer>> records = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      int counter = 0;
      while ((line = br.readLine()) != null) {
        if (counter > 0) { //pomijamy nagłówek
          List<String> valuesStr = new ArrayList<>(Arrays.asList(line.split(",")));
          String label = valuesStr.remove(0); //usuwamy wskaznik
          List<Integer> values = valuesStr.stream()
                                          .map(Integer::parseInt)
                                          .collect(Collectors.toList());
          if (label.equalsIgnoreCase("x"))
            results.put(StatisticType.OVERALL, values);
          else
            records.add(values);
        }
        counter ++;
      }
    }
    results.put(StatisticType.MSG_PER_SEC, records);
    return results;
  }

  private static void saveFullStats(List<Statistic> stats) throws IOException {
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
    String fileName = df.format(new Date()) + "-FullStats.csv";
    FileWriter out = new FileWriter(fileName);

    out.write(
      "Broker,Rozmiar wiadomości,Liczba kolejek/topiców,Liczba wiadomości,Liczba producentów,Liczba konsumentów," +
      "Czas przetworzenia wiadomości,Liczba odebranych wiadomości łącznie,Liczba odebranych wiadomości per kolejka," +
      "Rozmiar odebranych wiadomości łącznie,Rozmiar odebranych wiadomości per kolejka," +
      "Liczba wysłanych wiadomości łącznie,Liczba wysłanych wiadomości per kolejka," +
      "Rozmiar wysłanych wiadomości łącznie,Rozmiar wysłanych wiadomości per kolejka\n"
    );

    for (Statistic s : stats) {
      int processingTime = s.getConsumingTime() != 0 ? s.getConsumingTime() : s.getProducingTime();
      out.write(s.getBroker().name() + "," + s.getSize() + "," + s.getQueues() + "," + s.getNumberOfMessages() + "," + s.getProducers() + "," +
                s.getConsumers() + "," + processingTime + "," + s.getTotalThroughputMessagesIn() + "," + s.getQueueThroughputMessagesIn() + "," +
                s.getTotalThroughputTransferIn() + "," + s.getQueueThroughputTransferIn() + "," +
                s.getTotalThroughputMessagesOut() + "," + s.getQueueThroughputMessagesOut() + "," +
                s.getTotalThroughputTransferOut() + "," + s.getQueueThroughputTransferOut() + "\n");
    }

    out.close();
  }

  private static void saveFullStatsPaired(List<Map<Broker, Statistic>> stats) throws IOException {
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
    String fileName = df.format(new Date()) + "-FullStatsPaired.csv";
    OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8);

    out.write(
      "Rozmiar wiadomości,Liczba kolejek/topiców,(KAFKA) Liczba wiadomości,(RABBITMQ) Liczba wiadomości," +
      "Liczba producentów,Liczba konsumentów,(KAFKA) Czas przetworzenia wiadomości,(RABBITMQ) Czas przetworzenia wiadomości," +
      "(KAFKA) Liczba odebranych wiadomości łącznie,(KAFKA) Liczba odebranych wiadomości per kolejka," +
      "(KAFKA) Rozmiar odebranych wiadomości łącznie,(KAFKA) Rozmiar odebranych wiadomości per kolejka," +
      "(KAFKA) Liczba wysłanych wiadomości łącznie,(KAFKA) Liczba wysłanych wiadomości per kolejka," +
      "(KAFKA) Rozmiar wysłanych wiadomości łącznie,(KAFKA) Rozmiar wysłanych wiadomości per kolejka," +
      "(RABBITMQ) Liczba odebranych wiadomości łącznie,(RABBITMQ) Liczba odebranych wiadomości per kolejka," +
      "(RABBITMQ) Rozmiar odebranych wiadomości łącznie,(RABBITMQ) Rozmiar odebranych wiadomości per kolejka," +
      "(RABBITMQ) Liczba wysłanych wiadomości łącznie,(RABBITMQ) Liczba wysłanych wiadomości per kolejka," +
      "(RABBITMQ) Rozmiar wysłanych wiadomości łącznie,(RABBITMQ) Rozmiar wysłanych wiadomości per kolejka\n"
    );

    for (Map<Broker, Statistic> pairedStats : stats) {
      Statistic sk = pairedStats.get(Broker.KAFKA);
      Statistic sr = pairedStats.get(Broker.RABBITMQ);
      int processingTimeKafka = sk.getConsumingTime() != 0 ? sk.getConsumingTime() : sk.getProducingTime();
      int processingTimeRabbit = sr.getConsumingTime() != 0 ? sr.getConsumingTime() : sr.getProducingTime();
      out.write(
        sk.getSize() + "," + sk.getQueues() + "," + sk.getNumberOfMessages() + "," +
        sr.getNumberOfMessages() + "," + sk.getProducers() + "," + sk.getConsumers() + "," +
        processingTimeKafka + "," + processingTimeRabbit + "," +
        sk.getTotalThroughputMessagesIn() + "," + sk.getQueueThroughputMessagesIn() + "," +
        sk.getTotalThroughputTransferIn() + "," + sk.getQueueThroughputTransferIn() + "," +
        sk.getTotalThroughputMessagesOut() + "," + sk.getQueueThroughputMessagesOut() + "," +
        sk.getTotalThroughputTransferOut() + "," + sk.getQueueThroughputTransferOut() + "," +
        sr.getTotalThroughputMessagesIn() + "," + sr.getQueueThroughputMessagesIn() + "," +
        sr.getTotalThroughputTransferIn() + "," + sr.getQueueThroughputTransferIn() + "," +
        sr.getTotalThroughputMessagesOut() + "," + sr.getQueueThroughputMessagesOut() + "," +
        sr.getTotalThroughputTransferOut() + "," + sr.getQueueThroughputTransferOut() + "\n");
    }

    out.close();
  }
}
