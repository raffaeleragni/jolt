package com.github.raffaeleragni.jolt;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class Envelope {
  public final UUID uuid;
  public final String route;
  public final Map<String, String> headers;

  public Envelope(UUID uuid, String route, Map<String, String> headers) {
    this.uuid = uuid;
    this.route = route;
    this.headers = Collections.unmodifiableMap(headers);
  }
}
