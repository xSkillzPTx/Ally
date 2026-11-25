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

    /**
     * Procura a identidade registada com esta chave pública.
     *
     * @return o DigitalID, ou null se a chave não estiver registada
     */
    public static DigitalID getByPublicKey(PublicKey pub, BlockChain blockChain) {
        for (Block b : blockChain.getChain()) {
            try {
                Object o = Conversion.stringToObject(b.getData());
                if (o instanceof DigitalID id) {
                    PublicKey k = Conversion.stringToObject(id.getPublicKey());
                    if (k.equals(pub)) {
                        return id;
                    }
                }
            } catch (Exception e) {
                // este bloco não é um DigitalID, ignorar
            }
        }
        return null;
    }

    /**
     * A chave das Finanças é a que está no bloco génesis (bloco 0).
     * Não se procura pelo nome "Financas", porque qualquer pessoa pode
     * registar um DigitalID com esse nome.
     */
    public static PublicKey getIssuerKey(BlockChain blockChain) throws Exception {
        DigitalID genesis = Conversion.stringToObject(blockChain.getChain().get(0).getData());
        return Conversion.stringToObject(genesis.getPublicKey());
    }

}
