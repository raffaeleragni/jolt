package com.github.raffaeleragni.jolt;

import com.github.raffaeleragni.jolt.CheckedExceptionWrapper.VoidWrapper;
import com.github.raffaeleragni.jolt.CheckedExceptionWrapper.Wrapper;
import java.io.IOException;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class CheckedExceptionWrapperTest {

  @Test
  public void testOKTyped() {
    Wrapper<Integer> sup = () -> 5;

    var value = CheckedExceptionWrapper.exwrap(sup);

    assertThat(value, is(5));
  }

  @Test
  public void testOKVoid() {
    Integer[] i = new Integer[1];
    VoidWrapper sup = () -> i[0] = 4;

    CheckedExceptionWrapper.exwrap(sup);

    assertThat(i[0], is(4));
  }

  @Test
  public void testChecked() {
    var wrapper = new VoidWrapper() {
      @Override
      public void get() throws Exception {
        throw new IOException();
      }
    };

    assertThrows(RuntimeException.class, () -> {
      CheckedExceptionWrapper.exwrap(wrapper);
    });
  }

  @Test
  public void testCheckedWithTypeReturned() {
    var wrapper = new Wrapper<String>() {
      @Override
      public String get() throws Exception {
        throw new IOException();
      }
    };

    assertThrows(RuntimeException.class, () -> {
      CheckedExceptionWrapper.exwrap(wrapper);
    });
  }
}
