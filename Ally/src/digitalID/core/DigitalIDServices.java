/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package digitalID.core;

import core.Block;
import core.BlockChain;
import java.security.PublicKey;
import utils.Conversion;

/**
 *
 * @author skillz
 */
public class DigitalIDServices {
    
    public static PublicKey getPublicKey(String name, BlockChain blockChain) {
        for ( Block b : blockChain.getChain()) {
            try {
                DigitalID id = (DigitalID) Conversion.stringToObject(b.getData());
                //é um registo de Digital ID
                if (id.getName().equalsIgnoreCase(name))
                    return (PublicKey) Conversion.stringToObject(id.getPublicKey());
            } catch (Exception e) {
            }
        }
        return null;
    }
    
}
