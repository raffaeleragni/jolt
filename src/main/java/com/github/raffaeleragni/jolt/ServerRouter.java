package com.github.raffaeleragni.jolt;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ServerRouter {

  private final Map<String, Consumer<Envelope>> envelopeConsumers;
  private final Map<String, BiConsumer<Envelope, InputStream>> fullConsumers;
  private final Transformer transformer;

  public ServerRouter(Transformer transformer) {
    this.envelopeConsumers = new LinkedHashMap<>();
    this.fullConsumers = new LinkedHashMap<>();
    this.transformer = transformer;
  }

  public void route(InputStream inputStream) {
    var envelope = transformer.envelope(inputStream);
    route(envelope.route, envelope, inputStream);
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
