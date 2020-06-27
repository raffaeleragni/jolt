package com.github.raffaeleragni.jolt;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public final class PortChecker {

  private PortChecker() {
  }

  public static boolean portOccupied(int port) {
    return ! portAvailable(port);
  }

  public static boolean portAvailable(int port) {
    ServerSocket ss = null;
    DatagramSocket ds = null;
    try {
      ss = new ServerSocket(port);
      ss.setReuseAddress(true);
      ds = new DatagramSocket(port);
      ds.setReuseAddress(true);
      return true;
    } catch (IOException e) {
    } finally {
      if (ds != null) {
        ds.close();
      }
      if (ss != null) {
        try {
          ss.close();
        } catch (IOException e) {
        }
      }
    }

    return false;
  }
}
