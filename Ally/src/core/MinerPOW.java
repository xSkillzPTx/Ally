/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author skillz
 */
public class MinerPOW {

    public static class MinerThr extends Thread {

        AtomicInteger nonce;
        AtomicInteger counter;
        String data;
        int dif;

        public MinerThr(AtomicInteger nonce, AtomicInteger counter, String data, int dif) {
            this.nonce = nonce;
            this.counter = counter;
            this.data = data;
            this.dif = dif;
        }

        public void run() {
            String zeros = String.format("%0" + dif + "d", 0);
            while (nonce.get() == 0) {
                int n = counter.getAndIncrement();
                String hash = utils.SecurityUtils.getHash(data + n);

                if (hash.startsWith(zeros)) {
                    System.out.println(n + " " + hash);
                    nonce.set(n);
                }
            }
        }
    }

    public static int getNonceP(String data, int dif) {
        int cores = Runtime.getRuntime().availableProcessors();
        MinerThr thr[] = new MinerThr[cores];
        AtomicInteger nonce = new AtomicInteger();
        AtomicInteger counter = new AtomicInteger();
        for (int i = 0; i < thr.length; i++) {
            thr[i] = new MinerThr(nonce, counter, data, dif);
            thr[i].start();
        }
        try {
            thr[0].join();
        } catch (InterruptedException ex) {
            System.getLogger(MinerPOW.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return nonce.get();
    }

    public static int getNonce(String data, int dif) {
        String zeros = String.format("%0" + dif + "d", 0);
        int counter = 0;
        while (true) {
            String hash = utils.SecurityUtils.getHash(data + counter);
            if (hash.startsWith(zeros)) {
                System.out.println(counter + " " + hash);
                return counter;
            }
            counter++;
        }
    }
}
