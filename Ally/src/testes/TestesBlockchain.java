/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package testes;

import core.BlockChain;
import digitalID.core.DigitalIDServices;
import java.io.IOException;

/**
 *
 * @author skillz
 */
public class TestesBlockchain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        BlockChain bc = new BlockChain();
        bc.addData("agua");
        bc.addData("fresca");
        System.out.println(bc);
        System.out.println(DigitalIDServices.getPublicKey("Financas", bc));
    }
    
}
