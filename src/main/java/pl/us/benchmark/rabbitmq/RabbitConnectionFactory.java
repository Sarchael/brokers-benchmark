package pl.us.benchmark.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RabbitConnectionFactory {
  private static RabbitConnectionFactory INSTANCE;

  private static final String CONNECTION_NAME_PREFIX = "BENCHMARK_CONNECTION_";

  private final ConnectionFactory factory;

  private List<Connection> connections;
  private List<Channel> channels;

  private RabbitConnectionFactory() {
    this.factory = new ConnectionFactory();
    this.connections = new ArrayList<>();
    this.channels = new ArrayList<>();
  }

  public static RabbitConnectionFactory getInstance() {
    if (INSTANCE == null)
      INSTANCE = new RabbitConnectionFactory();
    return INSTANCE;
  }

  public Channel createChannel(String queueName, String connectionNamePostfix, boolean localInstance) throws IOException, TimeoutException {
    factory.setHost(localInstance ? RabbitProperties.HOST_LOCAL : RabbitProperties.HOST);
    factory.setPort(RabbitProperties.PORT);
    factory.setUsername(RabbitProperties.USERNAME);
    factory.setPassword(RabbitProperties.PASSWORD);

    Connection connection = factory.newConnection(CONNECTION_NAME_PREFIX + connectionNamePostfix);
    Channel channel = connection.createChannel();
    channel.queueDeclare(queueName, false, false, false, null);
    connections.add(connection);
    channels.add(channel);
    return channel;
  }

  public void closeConnection(Channel channel) throws IOException, TimeoutException {
    Connection connection = channel.getConnection();
    channels.remove(channel);
    connections.remove(connection);
    channel.close();
    connection.close();
  }

  public void closeAllConnections() {
    for (Channel channel : channels)
      try {
        channel.close();
      } catch (Exception e) {}
    for (Connection connection : connections)
      try {
        connection.close();
      } catch (Exception e) {}
    channels = new ArrayList<>();
    connections = new ArrayList<>();
  }
}
