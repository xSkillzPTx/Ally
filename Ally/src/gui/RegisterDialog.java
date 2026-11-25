package gui;

import java.awt.GridLayout;
import java.awt.Window;
import java.io.File;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.Base64;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import core.BlockChain;
import digitalID.core.DigitalID;
import utils.Conversion;
import utils.SecurityUtils;

public class RegisterDialog extends JDialog {

  private final JPasswordField passwordField = new JPasswordField(20);
  private final JPasswordField confirmField = new JPasswordField(20);
  private final BlockChain blockChain;

  /** caminho da identidade criada (sem extensão), ou null se não foi criada */
  private String createdPath;

  public RegisterDialog(Window owner) {
    super(owner, "Criar identidade", ModalityType.APPLICATION_MODAL);
    this.blockChain = BlockChain.getInstance();
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JButton btnCreate = new JButton("Criar identidade");
    btnCreate.addActionListener(e -> create());
    getRootPane().setDefaultButton(btnCreate);

    JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
    form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    form.add(new JLabel("Password:"));
    form.add(passwordField);
    form.add(new JLabel("Confirmar password:"));
    form.add(confirmField);
    form.add(new JLabel());
    form.add(btnCreate);

    setContentPane(form);
    pack();
    setLocationRelativeTo(owner);
  }

  private void create() {
    char[] pw = passwordField.getPassword();
    if (pw.length < 4) {
      JOptionPane.showMessageDialog(this, "A password tem de ter pelo menos 4 caracteres");
      return;
    }
    if (!Arrays.equals(pw, confirmField.getPassword())) {
      JOptionPane.showMessageDialog(this, "As passwords não coincidem");
      return;
    }
    try {
      // 1. onde guardar
      JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
      fc.setDialogTitle("Guardar identidade como...");
      if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
        return;
      }
      String path = fc.getSelectedFile().getAbsolutePath().replaceAll("\\.(priv|pub)$", "");
      if (new File(path + ".priv").exists()) {
        JOptionPane.showMessageDialog(this, "Já existe uma identidade com esse nome");
        return;
      }

      // 2. gerar o par de chaves e gravar (a privada fica cifrada com a password)
      KeyPair par = SecurityUtils.generateKeyPair(2048);
      SecurityUtils.savePublicKey(par.getPublic(), path);
      SecurityUtils.savePrivateKey(par.getPrivate(), path, new String(pw));

      // 3. DigitalID: id = hash da chave pública (pseudónimo), autoassinado
      String pubTxt = Conversion.objectToString(par.getPublic());
      String id = SecurityUtils.getHash(pubTxt);
      byte[] sig = SecurityUtils.sign((id + pubTxt).getBytes(), par.getPrivate());
      DigitalID did = new DigitalID(id, pubTxt, Base64.getEncoder().encodeToString(sig));

      // 4. registar na blockchain
      blockChain.addData(Conversion.objectToString(did));

      createdPath = path;
      System.out.println("Identidade criada: " + id + " -> " + path + ".priv");
      JOptionPane.showMessageDialog(this, "Identidade criada:\n" + path + ".priv");
      dispose();
    } catch (Exception e) {
      System.out.println("Erro ao criar identidade: " + e);
      JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
  }

  public String getCreatedPath() {
    return createdPath;
  }
}
