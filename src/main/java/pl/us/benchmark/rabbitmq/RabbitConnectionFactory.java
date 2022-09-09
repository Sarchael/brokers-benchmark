package pl.us.benchmark.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class RabbitConnectionFactory {
  private static RabbitConnectionFactory INSTANCE;

  private static final String CONNECTION_NAME_PREFIX = "BENCHMARK_CONNECTION_";

  private final ConnectionFactory factory;
  private Map<Channel, Connection> channelsMap;
  private List<String> declaredQueues;

  private RabbitConnectionFactory(boolean localInstance) {
    this.channelsMap = Collections.synchronizedMap(new HashMap<>());
    this.declaredQueues = new ArrayList<>();
    this.factory = new ConnectionFactory();
    this.factory.setHost(localInstance ? RabbitProperties.HOST_LOCAL : RabbitProperties.HOST);
    this.factory.setPort(RabbitProperties.PORT);
    this.factory.setUsername(RabbitProperties.USERNAME);
    this.factory.setPassword(RabbitProperties.PASSWORD);
  }

  public synchronized static RabbitConnectionFactory getInstance() {
    return INSTANCE;
  }

  public synchronized static RabbitConnectionFactory getInstance(boolean localInstance) {
    if (INSTANCE == null)
      INSTANCE = new RabbitConnectionFactory(localInstance);
    return INSTANCE;
  }

  public synchronized Channel createChannel(String queueName, String connectionNamePostfix) throws IOException, TimeoutException {
    Connection connection = factory.newConnection(CONNECTION_NAME_PREFIX + connectionNamePostfix);
    Channel channel = connection.createChannel();
    if (!declaredQueues.contains(queueName)) {
      channel.queueDeclare(queueName, false, false, false, null);
      declaredQueues.add(queueName);
    }
    channelsMap.put(channel, connection);
    return channel;
  }

  public void closeAllConnectionsForce() {
    closeAllConnectionsGracefully();
  }

  public void closeAllConnectionsGracefully() {
    synchronized (channelsMap) {
      for (Map.Entry<Channel, Connection> entry : channelsMap.entrySet()) {
        try {
          entry.getValue().close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
