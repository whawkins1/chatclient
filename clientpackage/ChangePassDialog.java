package clientpackage;

import java.awt.FlowLayout;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class ChangePassDialog implements ActionListener{
	private final String fUsername;
	private final JPasswordField fNewField;
	private final JPasswordField fConfirmField;
	private final JButton fOkButton;
	private final JButton fCancelButton;
	private final JDialog fDialog;
	private final ConnectionToServer fCts;
   public ChangePassDialog(final String aUsername, final ConnectionToServer aCts) {
	   this.fUsername = aUsername;
	   this.fCts = aCts;
	   final JPanel setPassPanel = new JPanel();
	   setPassPanel.setLayout(new GridBagLayout());
	   final JLabel setPassLabel = new JLabel();
	   setPassLabel.setText("New: ");
	   final GridBagConstraints c1 = ChatSignIn.setConstraints(0, 0);	
	   setPassPanel.add(setPassLabel, c1);
	   fNewField = new JPasswordField(15);
	   final GridBagConstraints c2 = ChatSignIn.setConstraints(1, 0);
	   setPassPanel.add(fNewField, c2);
	   final JLabel confirmPassLabel = new JLabel();
	   confirmPassLabel.setText("Confirm: ");
	   final GridBagConstraints c3 = ChatSignIn.setConstraints(0, 1);
	   setPassPanel.add(confirmPassLabel, c3);
	   fConfirmField = new JPasswordField(15);
	   final GridBagConstraints c4 = ChatSignIn.setConstraints(1, 1);
	   setPassPanel.add(fConfirmField, c4);
	   final JPanel buttonPanel = new JPanel();
	   buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	   fOkButton = createButton("OK", "OK", "Set Password");
	   fCancelButton = createButton("Cancel", "CANCEL", "Cancel Set Password");
	   buttonPanel.add(fOkButton);
	   buttonPanel.add(fCancelButton);
	   fDialog = new JDialog();
	   fDialog.addWindowListener( new WindowAdapter() {
	    	@Override
	    	public final void windowClosing(WindowEvent we) {
		    	closeOperation();
   		}});
	   fDialog.setTitle("Set Password");
	   fDialog.setModal(true);
	   fDialog.add(setPassPanel);
	   fDialog.add(buttonPanel);
	   fDialog.pack();
	   fDialog.setLocationRelativeTo(null);
	   fDialog.setVisible(true);	   
   }
   
	@Override
	public void actionPerformed(ActionEvent ae) {
		final String command = ae.getActionCommand();
		if(command.equals("OK")) {
			final String newPass = fNewField.getPassword().toString().trim();
			final String confirmPass = fConfirmField.getPassword().toString().trim();
			if(newPass.equals(confirmPass)) {
				fCts.sendMessage("CHANGED " + newPass);	
				JOptionPane.showMessageDialog(fDialog, 
	                      "Success, Password Set!",
	                      "Success",
	                       JOptionPane.INFORMATION_MESSAGE);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
				fCts.sendMessage("SIGN_IN " +  "USERNAME " + fUsername + " PASSWORD " + newPass);
				fDialog.dispose();
			} else {
				JOptionPane.showMessageDialog(fDialog, 
                        "Passwords Do Not Match, Please Try Again", 
                        "Error!",
                        JOptionPane.ERROR_MESSAGE);	
				fNewField.setText("");;
				fConfirmField.setText("");
			}
		} else if(command.equals("CANCEL")) {
			final int answer = JOptionPane.showConfirmDialog(fDialog, 
					                      "Password Was Not Changed Close Anyway?",
					                      "Closing",
					                       JOptionPane.YES_NO_OPTION);
			if(answer == JOptionPane.YES_OPTION) {
				fDialog.dispose();	
			} else {
			  fNewField.selectAll();
			  fConfirmField.setText("");
			}			
		}		
	}
	
	private final void closeOperation() {
		JOptionPane.showMessageDialog(fDialog, 
                "Password Was Not Changed Close Anyway?",
                "Closing",
                 JOptionPane.INFORMATION_MESSAGE);
         fDialog.dispose();
	}
	
	private final JButton createButton(final String aTitle, final String aCommand,
			                            final String aToolTipText) {
		final JButton b = new JButton();
		b.setText(aTitle);
		b.setToolTipText(aToolTipText);
		b.addActionListener(this);
		b.setActionCommand(aCommand);
		return b;
	}   
   
}
