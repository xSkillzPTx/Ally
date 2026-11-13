/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

import digitalID.core.DigitalID;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import utils.Conversion;
import utils.SecurityUtils;

/**
 *
 * @author skillz
 */
public class BlockChain {

    public static int DIFICUTLY = 3;
    public static final String FILENAME = "blockchain.bc";
    ArrayList<Block> chain = new ArrayList<>();

    public BlockChain() throws IOException {
        //ler a blockchain apartir do ficheiro
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILENAME))) {
            chain = (ArrayList<Block>) in.readObject();
        } catch (Exception e) {
            reset();
        }
    }

    public void reset() {
        try {
            //cria uma nova blockchain
            String prev = "000000";

            //carregar a chave pública das Finanças do ficheiro
            PublicKey pub = SecurityUtils.loadPublicKey("Financas");

            //convertar a chave em texto
            String pubTxt = Conversion.objectToString(pub);

            /*
             * Uma auto-assinatura não comprova nada, porque qualquer pessoa
             * pode gerar chaves e chamar-se "Financas", a confiança vem de o
             * registo estar no bloco 0, que é igual em todos os nós.
             */
            
            // criar o DigitalID das Finanças, sem assinatura
            DigitalID genesis = new DigitalID("Financas", pubTxt, null);
            System.out.println("Genesis " + genesis.toString());

            //converter o DigitalID em texto, isto é o que vai para o bloco
            String data = Conversion.objectToString(genesis);

            //minerar e criar o bloco
            int nonce = MinerPOW.getNonceP(prev + data + DIFICUTLY, DIFICUTLY);
            String current = utils.SecurityUtils.getHash(prev + data + DIFICUTLY + nonce);
            Block b = new Block(prev, data, nonce, DIFICUTLY, current);
            chain.add(b);
        } catch (Exception ex) {
            System.getLogger(BlockChain.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public void addData(String data) {
        String prev = chain.getLast().getCurrentHash();
        int nonce = MinerPOW.getNonceP(prev + data + DIFICUTLY, DIFICUTLY);
        String current = utils.SecurityUtils.getHash(prev + data + DIFICUTLY + nonce);
        Block b = new Block(prev, data, nonce, DIFICUTLY, current);
        chain.add(b);
        //guardar dados
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILENAME))) {
            out.writeObject(chain);
        } catch (Exception e) {

        }
    }

    public Block getLastBlock() {
        return chain.getLast();
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        for (Block block : chain) {
            txt.append("\n").append(block.toString());
        }
        return txt.toString();
    }

    public ArrayList<Block> getChain() {
        return chain;
    }

}
