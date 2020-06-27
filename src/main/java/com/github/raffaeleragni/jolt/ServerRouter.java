package com.github.raffaeleragni.jolt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ServerRouter {

  private static final int MAX_BYTES = 64 * 1024;
  private static final int BUFFER_SIZE = 8192;

  private final Map<String, Consumer<Envelope>> envelopeConsumers;
  private final Map<String, BiConsumer<Envelope, InputStream>> fullConsumers;
  private final Transformer transformer;

  public ServerRouter(Transformer transformer) {
    this.envelopeConsumers = new LinkedHashMap<>();
    this.fullConsumers = new LinkedHashMap<>();
    this.transformer = transformer;
  }

  public void route(InputStream inputStream) {
    var bytes = inputToArray(inputStream);
    CheckedExceptionWrapper.exwrap(() -> {
      try (var input = new ByteArrayInputStream(bytes)) {
        var envelope = transformer.envelope(input);
        try (var input2 = new ByteArrayInputStream(bytes)) {
          route(envelope.route, envelope, input2);
        }
      }
    });
  }

  private byte[] inputToArray(InputStream inputStream) {
    return CheckedExceptionWrapper.exwrap(() -> {
      try (var array = new ByteArrayOutputStream(MAX_BYTES)) {
        byte[] buffer = new byte[BUFFER_SIZE];
        int n;
        while (-1 != (n = inputStream.read(buffer))) {
          array.write(buffer, 0, n);
        }
        return array.toByteArray();
      }
    });
  }

  private <T> void route(String route, Envelope envelope, InputStream message) {
    Optional.ofNullable(envelopeConsumers.get(route))
      .ifPresent(consumer -> consumer.accept(envelope));
    Optional.ofNullable(fullConsumers.get(route))
      .ifPresent(consumer -> consumer.accept(envelope, message));
  }

  public void register(String route, Consumer<Envelope> consumer) {
    envelopeConsumers.put(route, consumer);
  }

  public <T> void register(String route, BiConsumer<Envelope, T> consumer, Class<T> clazz) {
    fullConsumers.put(route,
      (envelope, message) ->
        consumer.accept(envelope, transformer.parse(message, clazz)));
  }

}
