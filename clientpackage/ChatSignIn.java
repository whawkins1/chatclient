package clientpackage;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

public final class ChatSignIn implements ActionListener{
    private JButton fSignInButton;	
    private JButton fCreateAccountButton;
    private final JTextField fUsernameTextField;
    private final JPasswordField fPasswordField;
    private final JCheckBox fRememberPassCB;
    private final ConnectionToServer fCts;
    private String fUsername;
    private final JDialog fDialog;
    private CreateAccountDialog fCad;
    
	public ChatSignIn() {
		fCts = new ConnectionToServer(this);
		JPanel signinPanel = new JPanel();
	    signinPanel.setLayout(new GridBagLayout());
	    final GridBagConstraints c1 = setConstraints(0,0);
	    final JLabel usernameLabel = new JLabel();
	    usernameLabel.setText("Username: ");
	    signinPanel.add(usernameLabel, c1);
	    final GridBagConstraints c2 = setConstraints(1,0);
	    fUsernameTextField = new JTextField(15);
	    signinPanel.add(fUsernameTextField, c2);
	    GridBagConstraints c3 = setConstraints(0, 1);
	    final JLabel userPasswordLabel = new JLabel();
	    userPasswordLabel.setText("Password: ");
	    signinPanel.add(userPasswordLabel, c3);
        GridBagConstraints c4 = setConstraints(1, 1);
	    fPasswordField = new JPasswordField(15);
	    signinPanel.add(fPasswordField, c4);
	    GridBagConstraints c5 = setConstraints(0, 2);
	    c5.anchor = GridBagConstraints.WEST;
	    c5.gridwidth = 2;
	    fRememberPassCB = new JCheckBox();
	    fRememberPassCB.setText("Remember Password");
	    signinPanel.add(fRememberPassCB, c5);
	    fSignInButton  = makeButton("Sign In", "SIGN_IN", "Sign To Chat Server");
	    fCreateAccountButton  = makeButton("Create Account", "CREATE_ACCOUNT", "Create An Account");
        final JPanel buttonPanel = new JPanel();
        buttonPanel.add(fSignInButton);
        buttonPanel.add(fCreateAccountButton);
	    fDialog = new JDialog();
	    fDialog.setLayout(new BorderLayout());
	    fDialog.setTitle("Sign In");
	    final JRootPane rootPane = fDialog.getRootPane();
	    rootPane.setDefaultButton(fSignInButton);
	    fDialog.add(signinPanel, BorderLayout.NORTH);	
	    fDialog.add(buttonPanel, BorderLayout.SOUTH);
	    fDialog.addWindowListener(new WindowAdapter() {
	    	@Override
	    	public void windowClosing(WindowEvent we) {
	    		fCts.sendMessage("SIGN_OFF");
	    	}
	    });
	    fDialog.pack();
	    fDialog.setLocationRelativeTo(null);
	    fDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    fDialog.setVisible(true);
	}
		
		public final void actionPerformed(ActionEvent ae) {
		     final String command = ae.getActionCommand();	
		     switch(command) {
		     case "SIGN_IN":
				    fUsername = fUsernameTextField.getText();
		            final char[] passCharArr = fPasswordField.getPassword();
		            final String password = String.copyValueOf(passCharArr);
		            if((fUsername.equals("")) || (password.equals(""))) {
		                fUsernameTextField.selectAll();
						 fUsernameTextField.setCaretPosition(fUsername.length());
						 Arrays.fill(passCharArr, '0');
	                   	 password.equals("");
	                   	signInFail("Both Fields Must Be Completed, Please Try Again.");
	               	    fUsernameTextField.requestFocus();
				      } else {
				    	  fCts.sendMessage("SIGN_IN " +  "USERNAME " + fUsername + " PASSWORD " + password);
				      }
		            fPasswordField.setText("");
		            break;
		     case "CREATE_ACCOUNT":
	   	    	    fCad = new CreateAccountDialog(fCts);
	   	    	    break;
		     }   	
	     }
		
		public final void signInSuccess() {
				fDialog.dispose();
				new ChatClient(fUsername, fCts);
		}
		
		public final void signInFail(String aMessage) {
			createErrorMessage(aMessage + ", Please Try Again");
			fUsernameTextField.selectAll();
			 fUsernameTextField.setCaretPosition(fUsername.length());
			 fPasswordField.setText("");
		}
		
		public final void createAccountSuccess(boolean aSuccess) {
			if(aSuccess) {
				final String usernameAccount = fCad.getUsername();
				JOptionPane.showMessageDialog(fDialog, 
	                                          usernameAccount + " Created!", 
	                                          "Create Account",
	                                          JOptionPane.INFORMATION_MESSAGE);
				fCad.dispose();
				fDialog.dispose();
				new ChatClient(usernameAccount, fCts);
			} else {
				createErrorMessage("Duplicate Username, Please Try Again");
			}
		}
		
		private final void createErrorMessage(String aMessage) {
			JOptionPane.showMessageDialog(fDialog, 
	                                      aMessage, 
	                                      "Create Account Error",
	                                      JOptionPane.ERROR_MESSAGE);
		}
	        public final JButton makeButton(String label, String aAc, String toolTipText)    {
	            JButton b = new JButton(label);
	            b.setActionCommand(aAc);
	            b.addActionListener(this);
	            b.setToolTipText(toolTipText);
	           return b;
	    }
	        
	        public final static GridBagConstraints setConstraints(int x, int y) {
	        	GridBagConstraints c = new GridBagConstraints();
	        	c.anchor = GridBagConstraints.EAST;
	        	c.gridx = x;
	        	c.gridy = y;
	        	c.insets = new Insets(2, 0 , 2, 0);
	        	return c;
	        }

     public static void main(String args[]) {
	    	try	    {
			      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			    }	catch(UnsupportedLookAndFeelException ulfe)	 {
			    	ulfe.printStackTrace();
			    	System.out.println("Unsupported Look And Feel");		    	
			    }	catch(IllegalAccessException iae)	{
			    	iae.printStackTrace();
			    	System.out.println("Unsupported Look And Feel");
			    }	catch(InstantiationException ie)	{
			    	ie.printStackTrace();
			    	System.out.println("Unsupported Look And Feel");
			    }   catch(ClassNotFoundException cnfe)	{
			    	cnfe.printStackTrace();
			    	System.out.println("Unsupported Look And Feel");
			    }
	    	System.out.println("Chat Client Starting.....");
	        final Runnable thread = (new Runnable() {
	        	public void run() {
	        		new ChatSignIn();
	        	}
	        });
	        javax.swing.SwingUtilities.invokeLater(thread);
	        
	    }
	}