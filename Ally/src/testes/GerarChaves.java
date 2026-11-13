/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package testes;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import utils.SecurityUtils;

/**
 *
 * @author skillz
 */
public class GerarChaves {

    /**
     * @param args the command line arguments
     */
    /*public static void main(String[] args) throws Exception {
        //Gerar o par de chaves RSA com 2048 bits
        KeyPair par = SecurityUtils.generateKeyPair(2048);

        // Separar as duas chaves
        PublicKey pub = par.getPublic();
        PrivateKey priv = par.getPrivate();

        //gravar cada uma das chaves num ficheiro
        SecurityUtils.savePublicKey(pub, "Financas");
        SecurityUtils.savePrivateKey(priv, "Financas", "123qwe");
    }
*/ //está comentado porque senão ia gerar chaves novas por cima das antigas
}
