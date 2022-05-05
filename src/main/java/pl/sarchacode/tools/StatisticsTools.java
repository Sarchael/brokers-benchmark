package pl.sarchacode.tools;

import pl.sarchacode.params.BenchmarkParameters;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
}
