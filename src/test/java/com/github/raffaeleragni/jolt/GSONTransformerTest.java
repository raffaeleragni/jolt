package com.github.raffaeleragni.jolt;

import com.fatboyindustrial.gsonjavatime.Converters;
import static com.github.raffaeleragni.jolt.StringStreamer.stringToInputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GSONTransformerTest {

  Gson gson;
  GSONTransformer transformer;

  @BeforeEach
  public void setup() {
    makeGson();
    transformer = new GSONTransformer(gson);
  }

  void makeGson() {
    var gsonBuilder = new GsonBuilder();
    gsonBuilder = Converters.registerAll(gsonBuilder);
    gsonBuilder = gsonBuilder.registerTypeAdapterFactory(new GsonJava8TypeAdapterFactory());
    gson = gsonBuilder.create();
  }

  @Test
  public void testEnvelopeError() {
    assertThrows(RuntimeException.class, () -> {
      transformer.envelope(sampleInvalidJson());
    });
  }

  @Test
  public void testBodyError() {
    assertThrows(RuntimeException.class, () -> {
      transformer.parse(sampleInvalidJson(), MyBody.class);
    });
  }

  @Test
  public void testEnvelopeParsing() {
    Envelope envelope = transformer.envelope(sampleWithEnvelopeAndBody());

    assertThat(envelope.uuid, is(UUID.fromString("6f780d96-7abd-4cf0-8142-c85275c4b077")));
    assertThat(envelope.route, is("/api/method"));
    assertThat(envelope.headers, hasEntry("token", "jwttoken"));
    assertThat(envelope.headers, hasEntry("reply-to", "b2d29618-ed20-44ed-9830-e2ae8fa26ad0"));
  }

  @Test
  public void testCompleteParsing() {
    MyBody envelope = transformer.parse(sampleWithEnvelopeAndBody(), MyBody.class);

    assertThat(envelope.id, is(1));
    assertThat(envelope.timestamp, is(Instant.parse("2020-06-27T21:19:00.000Z")));
    assertThat(envelope.message, is("this is the message"));
  }

  public InputStream sampleInvalidJson() {
    return stringToInputStream("{");
  }

  public InputStream sampleWithEnvelopeAndBody() {
    return stringToInputStream("{\n"
      + "  \"uuid\": \"6f780d96-7abd-4cf0-8142-c85275c4b077\",\n"
      + "  \"route\": \"/api/method\",\n"
      + "  \"headers\": {\n"
      + "    \"token\": \"jwttoken\","
      + "    \"reply-to\": \"b2d29618-ed20-44ed-9830-e2ae8fa26ad0\"\n"
      + "  },\n"
      + "  \"body\": {\n"
      + "    \"id\": 1,\n"
      + "    \"timestamp\": \"2020-06-27T21:19:00.000Z\",\n"
      + "    \"message\": \"this is the message\""
      + "  }\n,"
      + "  \"fake\": 5"
      + "}");
  }

  public static class MyBody {
    public int id;
    public Instant timestamp;
    public String message;
  }

}
