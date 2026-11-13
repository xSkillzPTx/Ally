/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

import java.io.Serializable;

/**
 *
 * @author skillz
 */
public class Block implements Serializable {

    private final String previousHash;
    private final String data;
    private final int dificulty;
    private final int nonce;
    private final String currentHash;

    public Block(String previousHash, String data, int nonce, int dif, String currentHash) {
        this.previousHash = previousHash;
        this.data = data;
        this.nonce = nonce;
        this.dificulty = dif;
        this.currentHash = currentHash;
    }

    public boolean isValid() {
        //O currentHash vai começar com o número de zeros que está na dificulty
        return currentHash.startsWith(String.format("%0" + dificulty + "d", 0))
                //verificar se o currentHash é valido
                && utils.SecurityUtils.getHash(previousHash + data + dificulty + nonce).equals(currentHash);
    }

    @Override
    public String toString() {
        return currentHash + " [ " + data + " ] " + previousHash + (isValid() ? " OK" : " CORRUPTED");
    }

    public String getCurrentHash() {
        return currentHash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getData() {
        return data;
    }

    public int getDificulty() {
        return dificulty;
    }

    public int getNonce() {
        return nonce;
    }

    private static final long serialVersionUID = 202611131108L;
}
