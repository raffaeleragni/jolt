package com.github.raffaeleragni.jolt;

import java.io.InputStream;

public interface Transformer {

  public Envelope envelope(InputStream message);

  public <T> T parse(InputStream message, Class<T> clazz);

}
