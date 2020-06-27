package com.github.raffaeleragni.jolt;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.stream.Collectors;
import org.mockito.invocation.InvocationOnMock;
import org.skyscreamer.jsonassert.JSONAssert;

public final class StringStreamer {

  private StringStreamer() {
  }

  public static ByteArrayInputStream stringToInputStream(String message) {
    return new ByteArrayInputStream(message.getBytes(UTF_8));
  }

  public static String inputStreamToString(InvocationOnMock arg) {
    var inputStream = arg.getArgument(0, InputStream.class);
    String result = new BufferedReader(new InputStreamReader(inputStream)).lines().parallel().collect(Collectors.joining("\n"));
    return result;
  }
}
