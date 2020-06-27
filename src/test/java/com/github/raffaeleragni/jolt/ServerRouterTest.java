package com.github.raffaeleragni.jolt;

import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServerRouterTest {

  ServerRouter router;

  @BeforeEach
  public void setup() {
    router = new ServerRouter();
  }

  @Test
  public void testMethod() {
    Envelope envelope = new Envelope(UUID.randomUUID(), "my route");
    Consumer<Envelope> consumer = mock(Consumer.class);
    router.register("my route", consumer);

    router.route("my route", envelope);

    verify(consumer).accept(envelope);
  }
}
