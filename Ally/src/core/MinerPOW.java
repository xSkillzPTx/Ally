/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author skillz
 */
public class MinerPOW {

  public static class MinerThr extends Thread {

    AtomicLong nonce;
    String data;
    int dif;

    public MinerThr(AtomicLong nonce, String data, int dif) {
      this.nonce = nonce;
      this.data = data;
      this.dif = dif;
    }

    public void run() {
      String zeros = String.format("%0" + dif + "d", 0);
      while (nonce.get() == 0) {
        long random = ThreadLocalRandom.current().nextLong();
        String hash = utils.SecurityUtils.getHash(data + random);

        if (hash.startsWith(zeros)) {
          System.out.println(random + " " + hash);
          nonce.set(random);
        }
      }
    }
  }

  public static long getNonceP(String data, int dif) {
    int cores = Runtime.getRuntime().availableProcessors();
    MinerThr thr[] = new MinerThr[cores];
    AtomicLong nonce = new AtomicLong();
    for (int i = 0; i < thr.length; i++) {
      thr[i] = new MinerThr(nonce, data, dif);
      thr[i].start();
    }
    try {
      thr[0].join();
    } catch (InterruptedException ex) {
      System.getLogger(MinerPOW.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
    }
    return nonce.get();
  }

  // public static long getNonce(String data, int dif) {
  // String zeros = String.format("%0" + dif + "d", 0);
  // while (true) {
  // long random = ThreadLocalRandom.current().nextLong();
  // String hash = utils.SecurityUtils.getHash(data + random);
  // if (hash.startsWith(zeros)) {
  // System.out.println(random + " " + hash);
  // return random;
  // }
  // }
  // }
}
