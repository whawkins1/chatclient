package clientpackage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;

public final class RequestBuddyDialog 
                             implements ActionListener {
    private final JFrame fParent;
    private final List<String> fBuddyList;
    private final ConnectionToServer fCts;
    private final JDialog fDialog;
    private final String fUsername;
    private final JTextField fBuddyRequestField;

	public RequestBuddyDialog(JFrame aParent, String aUsername, List<String> aBuddyList,
			                  ConnectionToServer aCts) {
		this.fParent = aParent;
		this.fUsername = aUsername;
		this.fBuddyList = aBuddyList;
		this.fCts = aCts;
		final JPanel requestBuddyPanel = new JPanel();
		requestBuddyPanel.setLayout(new GridBagLayout());
		fBuddyRequestField = new JTextField(16);
	    final JLabel usernameBuddyLabel  = new JLabel("Username") ;
	    final GridBagConstraints c1 = setConstraints(0, 0);
	    requestBuddyPanel.add(usernameBuddyLabel, c1);
	    final GridBagConstraints c2 = setConstraints(1, 0);
	    requestBuddyPanel.add(fBuddyRequestField, c2);
	    final JButton requestButton = createButton("Request", "REQUEST");
	    final JButton closeButton = createButton("Close", "CLOSE");
	    final JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	    buttonPanel.add(requestButton);
	    buttonPanel.add(closeButton);
	    fDialog = new JDialog(fParent, "Request Buddy", true);
	    final JRootPane rootPane = fDialog.getRootPane();
	    rootPane.setDefaultButton(requestButton);
	    fDialog.setLayout(new BorderLayout());
	    fDialog.add(requestBuddyPanel, BorderLayout.NORTH);
	    fDialog.add(buttonPanel, BorderLayout.SOUTH);
	    fDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    fDialog.pack();
	    fDialog.setLocationRelativeTo(fParent);
	    fDialog.setVisible(true);
	}
	
	        @Override
			public final void actionPerformed(ActionEvent ae)   {
				final String command = ae.getActionCommand();
				if(command.equals("REQUEST")) {
					final String buddyName = fBuddyRequestField.getText();
					String errorMessage = "";
		            if(fBuddyList.contains(buddyName)) {
		            	errorMessage = "Buddy Is Already On Buddy List";
		            } else if(buddyName.equals(fUsername)) {
		            	JOptionPane.showMessageDialog(fParent,
		                         errorMessage, 
		                         "Cannot Add Yourself as a Buddy, Please Try Again.",
		                         JOptionPane.INFORMATION_MESSAGE);
		               fBuddyRequestField.selectAll();
			           fBuddyRequestField.setCaretPosition(buddyName.length());
			           fBuddyRequestField.setText("");
		            } else {
		            	JOptionPane.showMessageDialog(fParent,
		                         "Request Sent", 
		                         "Buddy Request",
		                         JOptionPane.ERROR_MESSAGE);
		                fCts.sendMessage("REQUEST_BUDDY " + buddyName);	
		                fDialog.dispose();
		            }
				} else if(command.equals("CLOSE")) {
					fDialog.dispose();
				}
			}
			
			private final JButton createButton(String aTitle, String aCommand) {
				final JButton b = new JButton();
				b.setText(aTitle);
				b.setActionCommand(aCommand);
				b.addActionListener(this);
				return b;
			}
		
			private final GridBagConstraints setConstraints(int x, int y) {
				GridBagConstraints c = new GridBagConstraints();
				c.anchor = GridBagConstraints.EAST;
				c.gridx = x;
				c.gridy = y;
				c.insets = new Insets(2, 2 , 2, 2);
				return c;
			}
}