package com.github.raffaeleragni.jolt;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;

public class Server {

  private final ServerContainer container;
  private final ServerRouter router;
  private final Transformer transformer;

  public Server(int port) {
    this.transformer = new GSONTransformer(makeGson());
    this.router = new ServerRouter(transformer);
    this.container = new ServerContainer(port, i -> this.router.route(i));
  }

  public void register(String route, Consumer<Envelope> consumer) {
    router.register(route, consumer);
  }

  public <T> void register(String route, BiConsumer<Envelope, T> consumer, Class<T> clazz) {
    router.register(route, consumer, clazz);
  }

  public void start() {
    container.start();
  }

  public void stop() {
    container.stop();
  }

  private Gson makeGson() {
    var gsonBuilder = new GsonBuilder();
    gsonBuilder = Converters.registerAll(gsonBuilder);
    gsonBuilder = gsonBuilder.registerTypeAdapterFactory(new GsonJava8TypeAdapterFactory());
    return gsonBuilder.create();
  }

}
