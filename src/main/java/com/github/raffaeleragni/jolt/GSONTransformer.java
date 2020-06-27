package com.github.raffaeleragni.jolt;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.LinkedHashMap;
import java.util.Optional;
import static java.util.Optional.empty;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GSONTransformer {

  private final Gson gson;

  public GSONTransformer(Gson gson) {
    this.gson = gson;
  }

  public Envelope envelope(InputStream message) {

    try (var reader = gson.newJsonReader(new InputStreamReader(message, UTF_8))) {
      Optional<UUID> uuid = empty();
      Optional<String> route = empty();
      var headers = new LinkedHashMap<String, String>();

      reader.beginObject();
      while (reader.hasNext()) {
        var name = reader.nextName();
        if ("uuid".equals(name)) {
          uuid = Optional.of(UUID.fromString(reader.nextString()));
        } else if ("route".equals(name)) {
          route = Optional.of(reader.nextString());
        } else if ("headers".equals(name)) {
          // read headers
          reader.beginObject();
          while (reader.hasNext()) {
            var headerName = reader.nextName();
            var headerValue = reader.nextString();
            headers.put(headerName, headerValue);
          }
          reader.endObject();
        } else if ("body".equals(name)) {
          reader.skipValue();
        } else {
          reader.skipValue();
        }
      }
      reader.endObject();

      return new Envelope(
        uuid.orElseThrow(),
        route.orElseThrow(),
        headers);

    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public <T> T parse(InputStream message, Class<T> clazz) {

    try (var reader = gson.newJsonReader(new InputStreamReader(message, UTF_8))) {
      Optional<T> result = empty();
      reader.beginObject();
      while (reader.hasNext()) {
        var name = reader.nextName();
        if ("uuid".equals(name)) {
          reader.skipValue();
        } else if ("route".equals(name)) {
          reader.skipValue();
        } else if ("headers".equals(name)) {
          reader.skipValue();
        } else if ("body".equals(name)) {
          result = Optional.of(gson.fromJson(reader, clazz));
        } else {
          reader.skipValue();
        }
      }
      reader.endObject();

      return result.orElseThrow();

    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

}
