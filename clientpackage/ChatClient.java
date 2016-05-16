package clientpackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public final class ChatClient   implements ActionListener  {
   private final JFrame fFrame;
    private final JButton requestBuddyButton;
    private final JButton signoffButton;
    private final JButton fInviteButton;
    private final JButton fSendFileButton;
    private final JButton fSendPhotoButton;
    private final JTable fBuddyTable;
    private final JTextPane fConvoPane;
    private final JPanel fStatusBarPanel;
    private final BuddyListTableModel fTableModel;
    private final ConnectionToServer fCts;
    private final String fUsername;
    private String fNameChattingWith;
    private final HTMLEditorKit fHtmlKit;
    private final HTMLDocument fHtmlDoc;
    private List<String> buddyList;
    private final MessageEditPanel fMessageEditPanel;
    
	    public ChatClient(String aUsername, ConnectionToServer aCts) {       
	       this.fUsername = aUsername;
	       this.fCts = aCts;
	       final JToolBar fToolBar = new JToolBar(SwingConstants.HORIZONTAL);
	       requestBuddyButton = makeButton("Request Buddy", "REQUEST_BUDDY", "Request a Buddy",
	    		                           "C:\\Users\\Bill\\Desktop\\request.png", true);
	       signoffButton = makeButton("Sign Off", "SIGN_OFF", "Sign Off Client", 
	    		   "C:\\Users\\Bill\\Desktop\\signoff.png",        true);
	       fInviteButton = makeButton("Invite", "INVITE", "Invite For Chat",
	    		                      "C:\\Users\\Bill\\Desktop\\invite.png", true);
	       fSendFileButton = makeButton("Send File", "SEND_FILE", "Send File To User",
	    		                         "C:\\Users\\Bill\\Desktop\\sendfile.png", false);
	       fSendPhotoButton = makeButton("Send Photo", "SEND_PHOTO", "Send Photo To User", 
	    		                         "C:\\Users\\Bill\\Desktop\\sendphoto.png",  false);
	       fToolBar.add(requestBuddyButton);
	       fToolBar.add(fInviteButton);
	       fToolBar.add(fSendFileButton);
	       fToolBar.add(fSendPhotoButton);
	       fToolBar.add(signoffButton);
	       final JMenuBar menuBar = new JMenuBar();
	       final JMenu fileMenu = new JMenu();
	       fileMenu.setText("File");
	       final JMenuItem exitMenuItem = createMenuItem("Exit", "EXIT");
	       fileMenu.add(exitMenuItem);
	       final JMenu menuSettings = new JMenu();
	       menuSettings.setText("Settings");
	       final JMenuItem disconnectMenuItem = createMenuItem("Disconnect", "DISCONNECT");
	       menuSettings.add(disconnectMenuItem);
	       final JMenu menuHelp = new JMenu();
	       menuHelp.setText("Help");
	       final JMenuItem aboutMenuItem = createMenuItem("About", "ABOUT");
	       menuHelp.add(aboutMenuItem);
	       menuBar.add(fileMenu);
	       menuBar.add(menuSettings);
	       menuBar.add(menuHelp);
	       buddyList = fCts.getBuddyList();
	       fTableModel = new BuddyListTableModel(buddyList);
	       fCts.setTableModel(fTableModel);
	       RowSorter<BuddyListTableModel> sorter = new TableRowSorter<BuddyListTableModel>(fTableModel);
	       fBuddyTable = new JTable();
	       fBuddyTable.setModel(fTableModel);
	       fBuddyTable.setFocusable(false);
	       fBuddyTable.setShowGrid(false);
	       fBuddyTable.setRowSelectionAllowed(true);
	       fBuddyTable.setCellSelectionEnabled(true);
	       fBuddyTable.setColumnSelectionAllowed(false);
	       fBuddyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	       fBuddyTable.setIntercellSpacing(new Dimension(0, 0));
	       fBuddyTable.setRowSorter(sorter);
	       fBuddyTable.setPreferredScrollableViewportSize(fBuddyTable.getPreferredSize());
	       final JPopupMenu popupMenu = new JPopupMenu();
	       final JMenuItem removeItem = createMenuItem("Remove", "REMOVE");
	       final JMenuItem inviteItem = createMenuItem("Invite", "INVITE");
	    	popupMenu.add(removeItem);
	    	popupMenu.add(inviteItem);
	       fBuddyTable.addMouseListener(new MouseAdapter() {	    	   
	    	   @Override
	    	   public final void mouseReleased(MouseEvent me) {
	    		   final JTable source = (JTable)me.getSource();
    		       final int row = source.rowAtPoint(me.getPoint());
	    		   if((buddyList.size() > 0) && (me.isPopupTrigger())) {
	    		       if(!source.isRowSelected(row)) {
	    		    	   source.changeSelection(row, 0, false, false);
	    		       } 	    		       
	    		       final String name = buddyList.get(row);
	    		       final boolean enableInvite = name.startsWith("<html><b>");
	    		       inviteItem.setEnabled(enableInvite);
	    		       popupMenu.show(me.getComponent(), me.getX(), me.getY());
	    		   } else if(row == -1) {
	    			   fBuddyTable.clearSelection();
	    		   }
	    	   }
	       });
	       final JTableHeader header = fBuddyTable.getTableHeader();
	       header.setDefaultRenderer(new HeaderRenderer());
	       final JScrollPane buddyPane = createScrollPane(fBuddyTable);
	       final JViewport vp = buddyPane.getViewport();
	       vp.setBackground(Color.WHITE);
	       final JPanel tablePanel = new JPanel();
	       buddyPane.setPreferredSize(new Dimension(80, 200));
	       tablePanel.setLayout(new BorderLayout());
	       tablePanel.add(buddyPane, BorderLayout.CENTER);
	       fConvoPane = new JTextPane();
	       fHtmlKit = new HTMLEditorKit();
	       fHtmlDoc = new HTMLDocument();
	       fConvoPane.setEditorKit(fHtmlKit);
	       fConvoPane.setDocument(fHtmlDoc);
	       fConvoPane.setPreferredSize(new Dimension(200, 100));
	       final JScrollPane scrollConvoPane = createScrollPane(fConvoPane);
	       scrollConvoPane.setViewportView(fConvoPane);
	       fConvoPane.setEditable(false);
	       final JPanel dialogPanel = new JPanel();
	       dialogPanel.setLayout(new BorderLayout());
	       dialogPanel.add(scrollConvoPane, BorderLayout.CENTER);
	       fMessageEditPanel = new MessageEditPanel(this, fCts);
	       fStatusBarPanel = new StatusBarPanel(fCts, this);
	       final JPanel mainDialogPanel = new JPanel(new BorderLayout());
	       mainDialogPanel.add(dialogPanel, BorderLayout.NORTH);
	       mainDialogPanel.add(fMessageEditPanel, BorderLayout.SOUTH);
	       fFrame = new JFrame();
	       final ImageIcon icon = new ImageIcon("C:\\Users\\Bill\\Desktop\\chatheader.png");
	       fFrame.setIconImage(icon.getImage());
	       fFrame.addWindowListener(new WindowAdapter() {
		    	@Override
		    	public void windowClosing(WindowEvent we) {
		    		fCts.sendMessage("SIGN_OFF");
		    		fFrame.dispose();
		    	}
		    });
	       fFrame.setLayout(new BorderLayout());
	       fFrame.setJMenuBar(menuBar);
	       fFrame.add(fToolBar, BorderLayout.NORTH);
	       fFrame.add(mainDialogPanel, BorderLayout.WEST);
	       fFrame.add(tablePanel, BorderLayout.EAST);
	       fFrame.add(fStatusBarPanel, BorderLayout.SOUTH);
	       fFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	       fFrame.setTitle("Chat Client : " + fUsername);
	       fFrame.pack();
	       final Dimension d = new Dimension(fFrame.getWidth(), 20);
	       fStatusBarPanel.setPreferredSize(d);
	       fFrame.setLocationRelativeTo(null);
	       
	       if(fCts.getRequestingBuddyList().size() != 0) {
	            new RequestingBuddiesDialog(fFrame, fUsername,  fCts, buddyList);
	       }
	       fCts.setChatClient(this);
	       fFrame.setVisible(true);
	   }
    
	        @Override
		    public final void actionPerformed(ActionEvent ae)    {
		        final String command = ae.getActionCommand();
		        switch(command) {
		        case "REQUEST_BUDDY":
		        	new RequestBuddyDialog(fFrame, fUsername, buddyList, fCts);
		        	break;
		        case "INVITE":
		        	inviteUser();
		        	break;
		        case "REMOVE BUDDY":
					final int selectedRow = fBuddyTable.getSelectedRow();
					if(selectedRow == -1) {
					     createErrorMessage("No Buddy Selected, Please Try Again.");
					} else {
						final String buddyNameSelected = buddyList.get(selectedRow);
			            fCts.sendMessage("REMOVE_BUDDY" + buddyNameSelected + " " + "BUDDY_WANTING_TO_BE_REMOVED " + fUsername);	
					}		
					break;
		        case "SEND_FILE":
		        	fCts.sendMessage("VERIFY_SENDING_PERMISSION " + fUsername);
		        	break;
		        case "SEND_PHOTO":
		        	fCts.sendMessage("VERIFY_SENDING_IMAGE_PERMISSION " + fUsername);
		        	break;
		        case "SIGN_OFF":
		           requestBuddyButton.setEnabled(false);
		           signoffButton.setEnabled(false);
		           enableComponents(false);
		           buddyList.clear();
		           break;
		        case "REMOVE":
		        	final Component component = (Component)ae.getSource(); 
		    	    final JPopupMenu popup = (JPopupMenu)component.getParent();
		    	     final JTable table = (JTable)popup.getInvoker();
		    	     final int row = table.getSelectedRow();		    	     
		    	     fTableModel.removeRow(row);
		    	     break;
		        case "EXIT":
		        	fCts.sendMessage("SIGN_OFF");
		        	fCts.sendMessage("BUDDIES");
		        	for(String name : buddyList) {
		        		if(name.startsWith("<html><b>")){
		        			name = fTableModel.parseName(name);
		        		}
		        		fCts.sendMessage(name);
		        	}
		        	fCts.sendMessage("END");
		        	if(((StatusBarPanel)fStatusBarPanel).downloadManagerOpen()) {
		        		((StatusBarPanel)fStatusBarPanel).closeDownloadManager();
		        	}
		        	fFrame.dispose();
		        	break;
		        }
		    }
	        
	        public final void addFileAttributes(String aFileName, String aFileSizeConverted, Long aFileSizeActual){
	        	((StatusBarPanel) fStatusBarPanel).addFileAttributes(aFileName, aFileSizeConverted, aFileSizeActual);
	        }
	        
	        public final void addImageAttributes(List<String> aImageNameList, List<BufferedImage> aImage) {
	        	((StatusBarPanel) fStatusBarPanel).addImageAttributes(aImageNameList, aImage);
	        }
		    
		    final class HeaderRenderer 
                          implements TableCellRenderer {
	            final DefaultTableCellRenderer renderer;
	            public HeaderRenderer() {
	            final JTableHeader tableHeader = fBuddyTable.getTableHeader();
	            renderer = (DefaultTableCellRenderer)tableHeader.getDefaultRenderer();
	            renderer.setHorizontalAlignment(JLabel.CENTER);
            }

				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					                                        boolean hasFocus, int row, int col) {
				return renderer.getTableCellRendererComponent(table, value, isSelected,
						                                       hasFocus, row, col);
				}
			}
		    
		    private final void inviteUser() {
		    	 final int row = fBuddyTable.getSelectedRow();
	    	     String selection = fTableModel.getUsername(row);
	    	     if(row == -1) {
	    	        createErrorMessage("Please Select Buddy.");
	    	     } else {
	    	    	 final int indexHtml = selection.indexOf("</b></html>");					
	         		 final String name = selection.substring(9, indexHtml).trim();
	    	    	 fCts.sendMessage("INVITE " + name); 
	    	     }	    	        
		    }
		    
		    public final void appendTextPane(String aMessage, boolean aUserConvo) {
				 try {				 
					 final Document doc = Jsoup.parseBodyFragment(aMessage);
					 final Elements paragraphs = doc.select("p");
					 final Element p = paragraphs.get(0);
					 final String color = aUserConvo ? "\"red\"" : "\"blue\"";
					 final String username = aUserConvo ? fUsername : fNameChattingWith;
	 				 final String styledName = "<font color = " + color + ">" + username + ": " + "</font>";
					 p.prepend(styledName);
					 fHtmlKit.insertHTML(fHtmlDoc, fHtmlDoc.getLength(), doc.toString(), 0, 0, null);
				 } catch (IOException ioe) {
						ioe.printStackTrace();
					} catch(BadLocationException ble) {
						ble.printStackTrace();
					}
			 }
		    
		    public final void enableComponents(boolean aEnable) {
		    	fSendPhotoButton.setEnabled(aEnable);
		    	fSendFileButton.setEnabled(aEnable);
		    	fMessageEditPanel.enableComponents(aEnable);
		    }
		    
		    private final JMenuItem createMenuItem(String aTitle, String aCommand) {
		    	JMenuItem mi = new JMenuItem();
		    	mi.setText(aTitle);
		    	mi.setActionCommand(aCommand);
		    	mi.addActionListener(this);
		    	return mi;
		    }
		    
		    private final void createErrorMessage(String aMessage) {
		                    JOptionPane.showMessageDialog(fFrame, 
		                                                  aMessage, 
		                                                  "Chat Error",
		                                                  JOptionPane.ERROR_MESSAGE);
		    }
		    
		    public final JButton makeButton(String label, String actionCommand, String toolTipText,
		    		                        final String aPath, boolean aEnable)    {
		        final JButton b = new JButton(label);
		        final Border border = BorderFactory.createRaisedSoftBevelBorder();
		        b.setBorder(border);
		        b.setActionCommand(actionCommand);
		        b.addActionListener(this);
		        b.setToolTipText(toolTipText);
		        final ImageIcon image = new ImageIcon(aPath);
                b.setIcon(image);
                b.setVerticalTextPosition(SwingConstants.BOTTOM);
                b.setHorizontalTextPosition(SwingConstants.CENTER);
		        b.setEnabled(aEnable);
		        return b;
		    }
		    
		    private final JScrollPane createScrollPane(JComponent aComponent) {
		    	   return new JScrollPane(aComponent, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	                                                   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		    }
		    
		    public final void updateStatus(File aFile, int aProgress) {
		    	((StatusBarPanel)fStatusBarPanel).updateDownload(aFile, aProgress);
		    }
		    
		    public final void setDownloadStatus(int aTrackDownload, int aNumDownloads) {
		    	String statusMessage = "";
		    	statusMessage = (aTrackDownload == aNumDownloads) ? "Downloads Complete"	
		    	                                                    : "Downloading " + aTrackDownload + "/" + aNumDownloads;
		    	 ((StatusBarPanel)fStatusBarPanel).setDownloadText(statusMessage);
		    }		    
		    
		    public final String getUserName() {
				return this.fUsername;
			}
		    
		    public final void setChatWithName(String aName) {
		    	this.fNameChattingWith = aName;
		    }
		    
		    public final String getChatWithName() {
		    	return this.fNameChattingWith;
		    }
		    
		    public final List<DownloadFileModel> getFileModelList() {
		    	return ((StatusBarPanel)fStatusBarPanel).getFileModelList();
		    }
}