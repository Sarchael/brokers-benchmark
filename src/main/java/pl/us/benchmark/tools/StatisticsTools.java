package pl.us.benchmark.tools;

import pl.us.benchmark.Broker;
import pl.us.benchmark.params.BenchmarkParameters;

import java.io.*;
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

  public static void generateFullStats() throws IOException {
    File folder = new File(".");
    List<File> listOfFiles = Arrays.asList(folder.listFiles());
    Map<String, List<List<Integer>>> parsedFiles = new HashMap<>();
    for (File file : listOfFiles) {
      if (file.getName().endsWith(".csv") && !file.getName().endsWith("FullStats.csv")) {
        List<List<Integer>> records = getRecordsFromFile(file);
        parsedFiles.put(file.getName().substring(16, file.getName().lastIndexOf('.')), records);
        List<Statistic> statistics = parsedFiles.entrySet()
                                                .stream()
                                                .map(x -> generateStatisticFromRawData(x.getKey(), x.getValue()))
                                                .collect(Collectors.toList());
        saveFullStats(statistics);
      }
    }
  }

  private static Statistic generateStatisticFromRawData(String name, List<List<Integer>> results) {
    Statistic statistic = new Statistic();
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
    Integer totalReceivedMessages = results.stream().mapToInt(x -> x.get(0)).sum();
    Integer totalSentMessages = results.stream().mapToInt(x -> x.get(1)).sum();
    Integer numberOfRecords = results.size();

    statistic.setTotalThroughputMessagesIn(totalReceivedMessages / numberOfRecords);
    statistic.setQueueThroughputMessagesIn(totalReceivedMessages / numberOfRecords / statistic.getQueues());
    statistic.setTotalThroughputTransferIn(statistic.getTotalThroughputMessagesIn() * statistic.getSize() / (1024 * 1024));
    statistic.setQueueThroughputTransferIn(statistic.getQueueThroughputMessagesIn() * statistic.getSize() / (1024 * 1024));

    statistic.setTotalThroughputMessagesOut(totalSentMessages / numberOfRecords);
    statistic.setQueueThroughputMessagesOut(totalSentMessages / numberOfRecords / statistic.getQueues());
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
        if (counter > 2) { //pomijamy nagłówek i statystyki z pierwszych 10 sekund (warm up)
          List<String> valuesStr = Arrays.asList(line.split(","));
          List<Integer> values = valuesStr.stream()
                                          .map(Integer::parseInt)
                                          .collect(Collectors.toList());
          values.remove(0);
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

    out.write("broker,message_size,queues/topics,producers,consumers,total_throughput_messages_in," +
                  "queue/topic_throughput_messages_in,total_throughput_transfer_in,queue/topic_throughput_transfer_out," +
                  "total_throughput_messages_out,queue/topic_throughput_messages_out,total_throughput_transfer_out," +
                  "queue/topic_throughput_transfer_out\n");

    for (Statistic s : stats) {
      out.write(s.getBroker().name() + "," + s.getSize() + "," + s.getQueues() + "," + s.getProducers() + "," +
                    s.getConsumers() + "," + s.getTotalThroughputMessagesIn() + "," + s.getQueueThroughputMessagesIn() + "," +
                    s.getTotalThroughputTransferIn() + "," + s.getQueueThroughputTransferIn() + "," +
                    s.getTotalThroughputMessagesOut() + "," + s.getQueueThroughputMessagesOut() + "," +
                    s.getTotalThroughputTransferOut() + "," + s.getQueueThroughputTransferOut() + "\n");
    }

    out.close();
  }
}
