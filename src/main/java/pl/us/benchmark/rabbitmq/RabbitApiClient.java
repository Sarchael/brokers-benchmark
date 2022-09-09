package pl.us.benchmark.rabbitmq;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RabbitApiClient {
  private boolean brokerOnLocalhost;

  public RabbitApiClient(boolean brokerOnLocalhost) {
    this.brokerOnLocalhost = brokerOnLocalhost;
  }

  public RabbitStats getCurrentStats() throws Exception {
    URL url = new URL("http://" +
                      (brokerOnLocalhost ? RabbitProperties.HOST_LOCAL : RabbitProperties.HOST) +
                      ":15672" +
                      "/api/overview");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Accept", "application/json");
    conn.setRequestProperty("Authorization", "Basic cmFiYml0c2VydmljZTpyYWJiaXRzZXJ2aWNl");
    if (conn.getResponseCode() != 200) {
      throw new RuntimeException("Failed : HTTP Error code : " + conn.getResponseCode());
    }
    InputStreamReader in = new InputStreamReader(conn.getInputStream());
    BufferedReader br = new BufferedReader(in);
    String response = "";
    String output;
    while ((output = br.readLine()) != null) {
      response += output;
    }
    conn.disconnect();

    RabbitStats rabbitStats = new RabbitStats();
    JSONObject stats = new JSONObject(response).getJSONObject("message_stats");
    if (stats.has("deliver_get"))
      rabbitStats.setConsumedMessages(stats.getLong("deliver_get"));
    if (stats.has("publish"))
      rabbitStats.setPublishedMessages(stats.getLong("publish"));

    return rabbitStats;
  }
}
