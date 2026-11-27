package gui;

import java.awt.Label;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JLabel;

import core.Block;
import core.BlockChain;
import digitalID.core.DigitalID;

public class MainScreen extends JFrame {
  private BlockChain blockChain = BlockChain.getInstance();
  private JLabel title;

  public MainScreen() {
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setBounds(new Rectangle(200, 200));
    this.title = new JLabel(DigitalID.getCurrentID().getName());
    this.add(title);
  }
}
