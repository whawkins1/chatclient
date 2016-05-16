package clientpackage;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class AboutDialog {
	  public AboutDialog(final JFrame aParent) {
    	  final JPanel iconPanel = new JPanel();
    	  final ImageIcon imageIcon = new ImageIcon("C:\\Users\\Bill\\Desktop\\chatheader.png");
    	  final JLabel imageLabel = new JLabel(imageIcon);
    	  iconPanel.setLayout(new BorderLayout());
    	  iconPanel.add(imageLabel, BorderLayout.CENTER);
		  final JPanel infoPanel = new JPanel();
		  infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		  final JLabel copyrightLabel = new JLabel("Copyright: 2016");
		  infoPanel.add(copyrightLabel);
		  final JDialog dialog = new JDialog(aParent, "JChat Information", false);
          dialog.setIconImage(imageIcon.getImage());
		  dialog.setLayout(new BorderLayout());
		  dialog.add(iconPanel, BorderLayout.WEST);
		  dialog.add(infoPanel, BorderLayout.EAST);
		  dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		  dialog.pack();
		  dialog.setLocationRelativeTo(aParent);
		  dialog.setVisible(true);
	  }
}