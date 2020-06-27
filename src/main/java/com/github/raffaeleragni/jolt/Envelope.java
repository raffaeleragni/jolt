package com.github.raffaeleragni.jolt;

import java.util.UUID;

public class Envelope {
  public final UUID uuid;
  public final String route;

  public Envelope(UUID uuid, String route) {
    this.uuid = uuid;
    this.route = route;
  }
}
