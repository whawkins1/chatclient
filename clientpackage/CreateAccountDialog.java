package clientpackage;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public final class CreateAccountDialog extends JDialog 
                               implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private final ConnectionToServer fCts;
	private JButton fOkButton;
	private JButton fCancelButton;
	private final JTextField fUsernameField;
    private final JPasswordField fPassField;
    private final JPasswordField fPassRetypeField;
    private String fUsername;
	
	public CreateAccountDialog(ConnectionToServer aCts) {
		this.fCts = aCts;
		fOkButton = makeButton("OK", "ADD_ACCOUNT", "Click To Add Account");
		    fCancelButton = makeButton("CANCEL", "CANCEL", "Cancel Account Creation");
		final JPanel buttonPanel = new JPanel();
	    buttonPanel.add(fOkButton);
	    buttonPanel.add(fCancelButton);
		final JPanel createAccountPanel = new JPanel();
	    createAccountPanel.setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    c.anchor = GridBagConstraints.EAST;
	    c.gridx = 0;
	    c.gridy = 0;
	    c.insets = new Insets(2, 0, 2, 0);
	    final JLabel usernameLabel = new JLabel();
	    usernameLabel.setText("Username:");
	    createAccountPanel.add(usernameLabel, c);
	    c.gridx = 1;
	    fUsernameField = new JTextField(15);
	    createAccountPanel.add(fUsernameField, c);
	    c.anchor = GridBagConstraints.EAST;
	    c.gridx = 0;
	    c.gridy = 1;
	    final JLabel passwordLabel = new JLabel();
	    passwordLabel.setText("Password:");
	    createAccountPanel.add(passwordLabel, c);
	    c.anchor = GridBagConstraints.EAST;
	    c.gridx = 1;
	    c.gridy = 1;
	    fPassField = new JPasswordField(15);
	    createAccountPanel.add(fPassField, c);
	    c.anchor = GridBagConstraints.EAST;
	    c.gridx = 0;
	    c.gridy = 2;
	    final JLabel passwordRetypeLabel = new JLabel();
	    passwordRetypeLabel.setText("Retype Password: ");
	    createAccountPanel.add(passwordRetypeLabel);
	    c.anchor = GridBagConstraints.EAST;
	    c.gridx = 1;
	    c.gridy = 2;
	    fPassRetypeField = new JPasswordField(15);
	    createAccountPanel.add(fPassRetypeField);	   	    
	    setLayout(new BorderLayout());
	    setTitle("Create Account");
	    setModal(false);
	    add(createAccountPanel, BorderLayout.NORTH);
	    add(buttonPanel, BorderLayout.SOUTH);
	    pack();
	    setLocationRelativeTo(null);
	    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    setVisible(true);
	}
	
	public final void actionPerformed(ActionEvent ae) {
        final String command = ae.getActionCommand();
        if(command.equals("ADD_ACCOUNT")) {
	        fUsername = fUsernameField.getText();
	        final String pass = new String(fPassField.getPassword());
	        final String passRetype = new String(fPassRetypeField.getPassword());
	        
	        if(!(pass.equals(passRetype)))  {
	        	createErrorMessage("Passwords Do not Match, Please Try Again.");
	        	fUsernameField.selectAll();
	            fUsernameField.setCaretPosition(fUsername.length());   
	        } else if(fUsername.equals("") || pass.equals("") || passRetype.equals("")){  
	    	     JOptionPane.showMessageDialog(this, 
                                            "All Information Must be Completed.", 
                                            "Create Account Error",
                                            JOptionPane.ERROR_MESSAGE);
           } else if(fUsername.startsWith("*")) {
	            	 createErrorMessage("Username cannot start with an Asterick(*), Please Try Again."); 
	       } else {
	    	         fCts.sendMessage("CREATE_ACCOUNT " + "USERNAME " + fUsername + " PASSWORD " + pass);	    	         
		   }
	   } else if(command.equals("CANCEL")) {
    	   dispose();
       }
	}
	
	public final String getUsername() {       
		return this.fUsername;
	}
	
	private final void createErrorMessage(String aMessage) {
		JOptionPane.showMessageDialog(this, 
                                      aMessage, 
                                      "Create Account Error",
                                      JOptionPane.ERROR_MESSAGE);
	}
	  private final JButton makeButton(String label, String aAc, String toolTipText)    {
          JButton b = new JButton(label);
          b.setActionCommand(aAc);
          b.addActionListener(this);
          b.setToolTipText(toolTipText);
         return b;
  }
}
