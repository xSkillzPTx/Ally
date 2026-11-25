package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.nio.file.NoSuchFileException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.AEADBadTagException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import core.BlockChain;
import digitalID.core.DigitalID;
import digitalID.core.DigitalIDServices;
import utils.SecurityUtils;

public class LoginDialog extends JDialog {

  /** o papel deduz-se da chave: a chave do bloco génesis é a das Finanças */
  public enum Role {
    EMISSOR, UTILIZADOR
  }

  private final JTextField pathField = new JTextField(25);
  private final JPasswordField passwordField = new JPasswordField(25);
  private final BlockChain blockChain;

  private PrivateKey privateKey;
  private PublicKey publicKey;
  private DigitalID identity;
  private Role role;

  public LoginDialog(Frame owner, BlockChain chain) {
    super(owner, "Ally - Login", true);
    this.blockChain = chain;
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JButton btnBrowse = new JButton("...");
    btnBrowse.addActionListener(e -> browse());
    JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
    pathPanel.add(pathField, BorderLayout.CENTER);
    pathPanel.add(btnBrowse, BorderLayout.EAST);

    JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
    form.add(new JLabel("Chave privada:"));
    form.add(pathPanel);
    form.add(new JLabel("Password:"));
    form.add(passwordField);

    JButton btnRegister = new JButton("Criar identidade");
    btnRegister.addActionListener(e -> register());
    JButton btnLogin = new JButton("Entrar");
    btnLogin.addActionListener(e -> login());
    getRootPane().setDefaultButton(btnLogin);

    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttons.add(btnRegister);
    buttons.add(btnLogin);

    JPanel content = new JPanel(new BorderLayout(10, 10));
    content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    content.add(form, BorderLayout.CENTER);
    content.add(buttons, BorderLayout.SOUTH);
    setContentPane(content);

    pack();
    setLocationRelativeTo(owner);
  }

  private void browse() {
    JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
    fc.setFileFilter(new FileNameExtensionFilter("Chave privada (*.priv)", "priv"));
    if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      pathField.setText(fc.getSelectedFile().getAbsolutePath());
    }
  }

  private void register() {
    RegisterDialog reg = new RegisterDialog(this, blockChain);
    reg.setVisible(true);
    // se criou uma identidade, preenche logo o caminho
    if (reg.getCreatedPath() != null) {
      pathField.setText(reg.getCreatedPath() + ".priv");
      passwordField.requestFocusInWindow();
    }
  }

  private void login() {
    try {
      String path = pathField.getText().trim().replaceAll("\\.priv$", "");

      // 1. desbloquear a chave privada com a password
      PrivateKey priv = SecurityUtils.loadPrivateKey(path, new String(passwordField.getPassword()));

      // 2. carregar a pública e confirmar que formam um par
      PublicKey pub = SecurityUtils.loadPublicKey(path);
      byte[] challenge = new byte[32];
      new SecureRandom().nextBytes(challenge);
      if (!SecurityUtils.verifySign(challenge, SecurityUtils.sign(challenge, priv), pub)) {
        throw new Exception("A chave privada não corresponde à chave pública");
      }

      // 3. a chave pública está registada na blockchain?
      DigitalID id = DigitalIDServices.getByPublicKey(pub, blockChain);
      if (id == null) {
        throw new Exception("Identidade não registada na blockchain");
      }

      // 4. papel: é a chave do bloco génesis (Finanças)?
      PublicKey issuer = DigitalIDServices.getIssuerKey(blockChain);
      role = pub.equals(issuer) ? Role.EMISSOR : Role.UTILIZADOR;

      privateKey = priv;
      publicKey = pub;
      identity = id;
      System.out.println("Login OK: " + id.getName() + " como " + role);
      dispose();
    } catch (AEADBadTagException e) {
      error("Password errada");
    } catch (NoSuchFileException e) {
      error("Ficheiro não encontrado: " + e.getMessage());
    } catch (Exception e) {
      error(e.getMessage());
    }
  }

  private void error(String msg) {
    System.out.println("Login falhou: " + msg);
    JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    passwordField.setText("");
  }

  public PrivateKey getPrivateKey() {
    return privateKey;
  }

  public PublicKey getPublicKey() {
    return publicKey;
  }

  public DigitalID getIdentity() {
    return identity;
  }

  public Role getRole() {
    return role;
  }
}
