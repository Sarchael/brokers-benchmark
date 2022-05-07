package pl.us.benchmark;

public enum Broker {
  RABBITMQ, KAFKA;

  public static Broker getByName(String name) {
    for (Broker broker : Broker.values())
      if (broker.name().equalsIgnoreCase(name))
        return broker;
    throw new IllegalArgumentException("No Broker found by name: " + name);
  }
}
