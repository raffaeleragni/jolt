package com.github.raffaeleragni.jolt;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ServerRouter {

  final Map<String, Consumer<Envelope>> envelopeConsumers;

  public ServerRouter() {
    envelopeConsumers = new LinkedHashMap<>();
  }

  void route(String route, Envelope envelope) {
    Optional.ofNullable(envelopeConsumers.get(route))
      .ifPresent(consumer -> consumer.accept(envelope));
  }

  void register(String route, Consumer<Envelope> consumer) {
    envelopeConsumers.put(route, consumer);
  }

}
