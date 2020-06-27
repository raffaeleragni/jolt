package com.github.raffaeleragni.jolt;

import static com.github.raffaeleragni.jolt.PortChecker.portOccupied;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import java.util.function.Consumer;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ServerContainerTest {

  static final String LOCAL = "127.0.0.1";
  static final int PORT = 7000;

  ServerContainer server;
  Consumer<InputStream> inputConsumer;

  @BeforeEach
  public void setup() {
    inputConsumer = mock(Consumer.class);
    server = new ServerContainer(PORT, inputConsumer);
  }

  @AfterEach
  public void teardown() {
    server.stop();
  }

  @Test
  public void testServerIsNotStartedByDefault() {
    assertThat(portOccupied(PORT), is(false));
  }

  @Test
  public void testServerStarts() {
    server.start();

    assertThat(portOccupied(PORT), is(true));
  }

  @Test
  public void testServerStops() {
    server.start();
    server.stop();

    assertThat(portOccupied(PORT), is(false));
  }

  @Test
  public void testServerStopsWithClose() throws IOException {
    server.start();
    server.close();

    assertThat(portOccupied(PORT), is(false));
  }

  @Test
  public void testCanConnectToServer() throws IOException, InterruptedException {
    server.start();

    sendSocketDataToServer();

    MILLISECONDS.sleep(100);

    verify(inputConsumer).accept(any());
  }

  @Test
  public void testCanConnectToServerAfterRestart() throws IOException, InterruptedException {
    server.start();
    server.stop();
    server.start();

    sendSocketDataToServer();

    MILLISECONDS.sleep(100);

    verify(inputConsumer).accept(any());
  }

  void sendSocketDataToServer() throws IOException {
    try (var sock = new Socket(LOCAL, PORT)) {
      try (var os = sock.getOutputStream()) {
        os.write(1);
        os.flush();
      }
    }
  }

}
