package gui;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import core.BlockChain;

public class App {

  public static void main(String[] args) {
    BlockChain blockChain;
    try {
      blockChain = new BlockChain();
    } catch (Exception e) {
      System.out.println("Erro ao carregar a blockchain: " + e);
      JOptionPane.showMessageDialog(null, "Erro ao carregar a blockchain: " + e.getMessage());
      return;
    }

    SwingUtilities.invokeLater(() -> {
      LoginDialog login = new LoginDialog(null, blockChain);
      login.setVisible(true); // modal: espera aqui até a janela fechar

      if (login.getPrivateKey() == null) { // cancelou / fechou a janela
        System.exit(0);
      }

      System.out.println("Entrou " + login.getIdentity().getName() + " como " + login.getRole());
      // TODO: abrir a janela principal (MainScreen) com o papel e as chaves
    });
  }
}
