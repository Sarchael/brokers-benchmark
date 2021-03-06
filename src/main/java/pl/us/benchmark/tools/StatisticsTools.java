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
                               BenchmarkParameters benchmarkParameters) throws IOException {
    int time = 5;
    FileWriter out = new FileWriter(createFileName(benchmarkParameters));
    out.write("time,consuming,producing\n");
    for (int i = 0; i < consumersStats.size(); i++) {
      out.write(time + "," + consumersStats.get(i) + "," + producersStats.get(i) + "\n");
      time += 5;
    }
    out.close();
  }

  public static void generateFullStats(Boolean pairResults) throws IOException {
    File folder = new File(".");
    List<File> listOfFiles = Arrays.asList(folder.listFiles());
    Map<String, List<List<Integer>>> parsedFiles = new HashMap<>();

    for (File file : listOfFiles) {
      if (file.getName().endsWith(".csv") && !file.getName().endsWith("FullStats.csv") && !file.getName().endsWith("FullStatsPaired.csv")) {
        List<List<Integer>> records = getRecordsFromFile(file);
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
                statistic.getProducers().equals(pair.getProducers())) {
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

  private static Statistic generateStatisticFromRawData(String name, List<List<Integer>> results) {
    Statistic statistic = new Statistic();
    statistic.setName(name);
    List<String> parameters = Arrays.asList(name.split("-"));
    fillStatisticWithBenchmarkParams(statistic, parameters);
    fillStatisticWithBenchmarkResults(statistic, results);
    return statistic;
  }

  private static void fillStatisticWithBenchmarkParams(Statistic statistic, List<String> parameters) {
    parameters.forEach(x -> {
      switch(x.charAt(0)) {
        case 'S' -> statistic.setSize(Integer.parseInt(x.substring(1)));
        case 'Q' -> statistic.setQueues(Integer.parseInt(x.substring(1)));
        case 'P' -> statistic.setProducers(Integer.parseInt(x.substring(1)));
        case 'C' -> statistic.setConsumers(Integer.parseInt(x.substring(1)));
        default -> statistic.setBroker(Broker.getByName(x));
      }
    });
  }

  private static void fillStatisticWithBenchmarkResults(Statistic statistic, List<List<Integer>> results) {
    Integer totalReceivedMessages = results.stream()
                                           .mapToInt(x -> x.get(0))
                                           .sum();
    Long zeroValuesReceiversCount = results.stream()
                                           .mapToInt(x -> x.get(0))
                                           .filter(x -> x == 0)
                                           .count();
    Integer totalSentMessages = results.stream()
                                       .mapToInt(x -> x.get(1))
                                       .sum();
    Long zeroValuesSendersCount = results.stream()
                                         .mapToInt(x -> x.get(1))
                                         .filter(x -> x == 0)
                                         .count();
    Integer numberOfRecords = results.size();

    statistic.setTotalThroughputMessagesIn(totalReceivedMessages / (int)(numberOfRecords - zeroValuesReceiversCount));
    statistic.setQueueThroughputMessagesIn(totalReceivedMessages / (int)(numberOfRecords - zeroValuesReceiversCount) / statistic.getQueues());
    statistic.setTotalThroughputTransferIn(statistic.getTotalThroughputMessagesIn() * statistic.getSize() / (1024 * 1024));
    statistic.setQueueThroughputTransferIn(statistic.getQueueThroughputMessagesIn() * statistic.getSize() / (1024 * 1024));

    statistic.setTotalThroughputMessagesOut(totalSentMessages / (int)(numberOfRecords - zeroValuesSendersCount));
    statistic.setQueueThroughputMessagesOut(totalSentMessages / (int)(numberOfRecords - zeroValuesSendersCount) / statistic.getQueues());
    statistic.setTotalThroughputTransferOut(statistic.getTotalThroughputMessagesOut() * statistic.getSize() / (1024 * 1024));
    statistic.setQueueThroughputTransferOut(statistic.getQueueThroughputMessagesOut() * statistic.getSize() / (1024 * 1024));
  }

  private static String createFileName(BenchmarkParameters benchmarkParameters) {
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

  private static List<List<Integer>> getRecordsFromFile(File file) throws IOException  {
    List<List<Integer>> records = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      int counter = 0;
      while ((line = br.readLine()) != null) {
        if (counter > 0) { //pomijamy nag????wek
          List<String> valuesStr = Arrays.asList(line.split(","));
          List<Integer> values = valuesStr.stream()
                                          .map(Integer::parseInt)
                                          .collect(Collectors.toList());
          values.remove(0); //usuwamy wska??nik czasu
          records.add(values);
        }
        counter ++;
      }
    }
    return records;
  }

  private static void saveFullStats(List<Statistic> stats) throws IOException {
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
    String fileName = df.format(new Date()) + "-FullStats.csv";
    FileWriter out = new FileWriter(fileName);

    out.write(
      "Broker,Rozmiar wiadomo??ci,Liczba kolejek/topic??w,Liczba producent??w,Liczba konsument??w," +
      "Liczba odebranych wiadomo??ci ????cznie,Liczba odebranych wiadomo??ci per kolejka," +
      "Rozmiar odebranych wiadomo??ci ????cznie,Rozmiar odebranych wiadomo??ci per kolejka," +
      "Liczba wys??anych wiadomo??ci ????cznie,Liczba wys??anych wiadomo??ci per kolejka," +
      "Rozmiar wys??anych wiadomo??ci ????cznie,Rozmiar wys??anych wiadomo??ci per kolejka\n"
    );

    for (Statistic s : stats) {
      out.write(s.getBroker().name() + "," + s.getSize() + "," + s.getQueues() + "," + s.getProducers() + "," +
                s.getConsumers() + "," + s.getTotalThroughputMessagesIn() + "," + s.getQueueThroughputMessagesIn() + "," +
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
    //FileWriter out = new FileWriter(fileName);

    out.write(
      "Rozmiar wiadomo??ci,Liczba kolejek/topic??w,Liczba producent??w,Liczba konsument??w," +
      "(KAFKA) Liczba odebranych wiadomo??ci ????cznie,(KAFKA) Liczba odebranych wiadomo??ci per kolejka," +
      "(KAFKA) Rozmiar odebranych wiadomo??ci ????cznie,(KAFKA) Rozmiar odebranych wiadomo??ci per kolejka," +
      "(KAFKA) Liczba wys??anych wiadomo??ci ????cznie,(KAFKA) Liczba wys??anych wiadomo??ci per kolejka," +
      "(KAFKA) Rozmiar wys??anych wiadomo??ci ????cznie,(KAFKA) Rozmiar wys??anych wiadomo??ci per kolejka," +
      "(RABBITMQ) Liczba odebranych wiadomo??ci ????cznie,(RABBITMQ) Liczba odebranych wiadomo??ci per kolejka," +
      "(RABBITMQ) Rozmiar odebranych wiadomo??ci ????cznie,(RABBITMQ) Rozmiar odebranych wiadomo??ci per kolejka," +
      "(RABBITMQ) Liczba wys??anych wiadomo??ci ????cznie,(RABBITMQ) Liczba wys??anych wiadomo??ci per kolejka," +
      "(RABBITMQ) Rozmiar wys??anych wiadomo??ci ????cznie,(RABBITMQ) Rozmiar wys??anych wiadomo??ci per kolejka\n"
    );

    for (Map<Broker, Statistic> pairedStats : stats) {
      Statistic sk = pairedStats.get(Broker.KAFKA);
      Statistic sr = pairedStats.get(Broker.RABBITMQ);
      out.write(
        sk.getSize() + "," + sk.getQueues() + "," + sk.getProducers() + "," + sk.getConsumers() + "," +
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
