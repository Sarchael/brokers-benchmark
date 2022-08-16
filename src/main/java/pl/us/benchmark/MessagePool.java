package pl.us.benchmark;


import java.util.Optional;

public class MessagePool {
  private Integer messagePool;
  private Integer packageSize;

  private static MessagePool INSTANCE;

  private MessagePool() {}

  public static MessagePool getInstance() {
    if (INSTANCE == null)
      INSTANCE = new MessagePool();
    return INSTANCE;
  }

  public void setParams(Integer messagePool, Integer packageSize) {
    this.messagePool = messagePool;
    this.packageSize = packageSize;
  }

  public synchronized Optional<Integer> getPackage() {
    messagePool -= packageSize;
    return messagePool >= 0 ? Optional.of(packageSize) : Optional.empty();
  }

  public synchronized boolean registerPackage(int size) {
    messagePool -= size;
    return messagePool > 0;
  }

  public int getMessageCount() {
    return messagePool;
  }
}
