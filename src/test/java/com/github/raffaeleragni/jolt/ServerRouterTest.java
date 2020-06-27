package com.github.raffaeleragni.jolt;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.EMPTY_MAP;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.mockito.invocation.InvocationOnMock;

public class ServerRouterTest {

  private Transformer transformer;
  private ServerRouter router;

  @BeforeEach
  public void setup() {
    transformer = mock(Transformer.class);
    router = new ServerRouter(transformer);
  }

  @Test
  public void testWithBody() {
    Envelope envelope = new Envelope(UUID.randomUUID(), "my route", EMPTY_MAP);
    setupTransformerToString();
    setupTransformerEnvelope(envelope);

    String message = "message";

    Consumer<Envelope> consumerEnvelope = mock(Consumer.class);
    BiConsumer<Envelope, String> consumer = mock(BiConsumer.class);

    router.register("my route", consumerEnvelope);
    router.register("my route", consumer, String.class);

    router.route(stringToInputStream(message));

    verify(consumerEnvelope).accept(envelope);
    verify(consumer).accept(envelope, "message");
  }

  private void setupTransformerEnvelope(Envelope envelope) {
    given(transformer.envelope(any()))
      .willReturn(envelope);
  }

  private void setupTransformerToString() {
    given(transformer.transform(any(), any()))
      .willAnswer(arg -> inputStreamToString(arg));
  }

  private static ByteArrayInputStream stringToInputStream(String message) {
    return new ByteArrayInputStream(message.getBytes(UTF_8));
  }

  private static String inputStreamToString(InvocationOnMock arg) {
    var inputStream = arg.getArgument(0, InputStream.class);
    String result = new BufferedReader(new InputStreamReader(inputStream)).lines().parallel().collect(Collectors.joining("\n"));
    return result;
  }
}
