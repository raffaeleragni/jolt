package com.github.raffaeleragni.jolt;

import java.net.ServerSocket;
import java.util.Optional;
import static java.util.Optional.empty;
import static com.github.raffaeleragni.jolt.CheckedExceptionWrapper.exwrap;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import static java.util.logging.Level.WARNING;
import java.util.logging.Logger;

public class ServerContainer implements Closeable {
  private static final Logger LOG = Logger.getLogger(ServerContainer.class.getName());

  private Optional<ExecutorService> acceptPool;
  private final int port;
  private final Consumer<InputStream> inputConsumer;

  private Optional<ServerSocket> serverSocket;
  private volatile boolean interrupted;

  public ServerContainer(int port, Consumer<InputStream> inputConsumer) {
    this.port = port;
    this.serverSocket = empty();
    this.acceptPool = empty();
    this.inputConsumer = Objects.requireNonNull(inputConsumer);
  }

  public void start() {
    serverSocket = exwrap(() -> Optional.of(createServerSocket()));
    setupAcceptJobs();
  }

  private ServerSocket createServerSocket() throws IOException {
    return new ServerSocket(port);
  }

  private void setupAcceptJobs() {
    interrupted = false;
    acceptPool = Optional.of(Executors.newFixedThreadPool(1));
    acceptPool.ifPresent(pool -> pool.execute(() -> {
      while (!interrupted)
        acceptNextSocket();
    }));
  }

  private void acceptNextSocket() {
    serverSocket.ifPresent(socket -> {
      try (var s = socket.accept()) {
        consumeInputStream(s);
      } catch (SocketException e) {
        handleAcceptSocketException(e);
      } catch (IOException | RuntimeException e) {
        LOG.log(WARNING, "Socket accept() error", e);
      }
    });
  }

  private void handleAcceptSocketException(SocketException e) {
    var socketWasClosed = e.getMessage().toLowerCase().equals("socket closed");
    // If the server socket was closed before could accept any other connection.
    // This error always happens when the server socket is closed, so it's accounted for?
    if (!socketWasClosed)
      LOG.log(WARNING, "Socket accept() error", e);
  }

  private void consumeInputStream(final Socket s) throws IOException {
    exwrap(() -> {
      try (var input = s.getInputStream()) {
        inputConsumer.accept(input);
      }
    });
  }

  public void stop() {
    teardownAcceptJobs();
    serverSocket.ifPresent(s -> exwrap(() -> s.close()));
    this.serverSocket = empty();
  }

  private void teardownAcceptJobs() {
    interrupted = true;
    acceptPool.ifPresent(pool -> pool.shutdownNow());
    acceptPool = empty();
  }

  @Override
  public void close() throws IOException {
    stop();
  }

}
