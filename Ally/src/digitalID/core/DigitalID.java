/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package digitalID.core;

import java.io.Serializable;

/**
 *
 * @author skillz
 */
public class DigitalID implements Serializable {

    private final String name;
    private final String publicKey;
    private final String signature;

    public DigitalID(String name, String publicKey, String signature) {
        this.name = name;
        this.publicKey = publicKey;
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "DigitalID{" + "name=" + name + "\n publicKey=" + publicKey + "\n signature=" + signature + '}';
    }

    public String getName() {
        return name;
    }

    public String getPublicKey() {
        return publicKey;
    }
    
    private static final long serialVersionUID = 202611131108L;
}
