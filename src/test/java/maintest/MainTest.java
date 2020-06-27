package maintest;

import com.github.raffaeleragni.jolt.Server;
import java.io.IOException;
import java.net.Socket;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;

public class MainTest {
  static final String HOST = "localhost";
  static final int PORT = 7000;
  static final String PATH = "/app";

  @Test
  public void testMain() throws IOException, InterruptedException {
    Server server = new Server(PORT);

    boolean[] results = new boolean[2];

    server.register(PATH, envelope -> {
      results[0] = true;
    });
    server.register(PATH, (envelope, body) -> {
      results[1] = true;
    }, String.class);

    server.start();

    sendData(sampleMessage());

    MILLISECONDS.sleep(100);

    server.stop();

    assertThat("First called", results[0], is(true));
    assertThat("Second called", results[1], is(true));
  }

  void sendData(String message) throws IOException {
    try (var sock = new Socket(HOST, PORT)) {
      try (var os = sock.getOutputStream()) {
        os.write(message.getBytes(UTF_8));
        os.flush();
      }
    }
  }

  public String sampleMessage() {
    return "{\n"
      + "  \"uuid\": \"6f780d96-7abd-4cf0-8142-c85275c4b077\",\n"
      + "  \"route\": \"/app\",\n"
      + "  \"headers\": {\n"
      + "    \"token\": \"jwttoken\","
      + "    \"reply-to\": \"b2d29618-ed20-44ed-9830-e2ae8fa26ad0\"\n"
      + "  },\n"
      + "  \"body\": \"String\","
      + "  \"fake\": 5"
      + "}";
  }
}
