package com.github.raffaeleragni.jolt;

public final class CheckedExceptionWrapper {

  private CheckedExceptionWrapper() {
  }

  public static <T> T exwrap(Wrapper<T> fn) {
    try {
      return fn.get();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public static void exwrap(VoidWrapper fn) {
    try {
      fn.get();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @FunctionalInterface
  public interface Wrapper<T> {
    T get() throws Exception;
  }

  @FunctionalInterface
  public interface VoidWrapper {
    void get() throws Exception;
  }

}
