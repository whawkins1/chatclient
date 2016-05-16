package clientpackage;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public final class ConnectionToServer 
                          implements Runnable{
    private BufferedReader br;
    private BufferedWriter bw;
    private BufferedOutputStream bos;
    private List<String> fBuddyList;
    private String replyServer;
    public boolean done;
    private Socket socket;
    final byte[] buffer;
    private ChatSignIn fCsi;
    private ChatClient fClient;
    private String fNameChattingWith;
    private  List<String> fRequestBuddyListNames;
    private BuddyListTableModel fTableModelBuddies;
    private String fPath;
    private final List<File> fFileQueue;
    private final List<Long> fFileSizeList;
    private ImageSendWorker fImageWorker;
    
    public ConnectionToServer(ChatSignIn aCsi)  {
    	this.fCsi = aCsi;
    	this.fFileQueue = new ArrayList<File>();
    	this.fFileSizeList = new ArrayList<Long>();
    	fImageWorker = null;
    	buffer = new byte[8192];
    	fBuddyList = new ArrayList<String>();    	
    	fRequestBuddyListNames = new ArrayList<String>();
    	done = false;
    	final Thread t = new Thread(this);
        t.start();
    }
    
     @Override
     public final void run() {
    	 try {
    		      socket = new Socket("127.0.0.1", 8080);
    		      final InputStream is = socket.getInputStream();
    		      final OutputStream os = socket.getOutputStream();
		          bw = new BufferedWriter(new OutputStreamWriter(os));
    		      br = new BufferedReader(new InputStreamReader(is)); 
		       while(!done) {
		    	     String name = "";
		    	     String fileName = "";
		    	     final String userDir = System.getProperty("user.home");
		    	     JFileChooser fileChooser = null;
		    	     int returnVal = Integer.MAX_VALUE;
		    	     List<String> nameList = null;
		    	     String username = "";
	    		     replyServer = readMessage();

		    		if(replyServer.startsWith("REQUESTS_FOR_BUDDY")) {  
		                	   while(!((name = readMessage()).equals("END"))) {
		                		   fRequestBuddyListNames.add(name);
		                	   }
		    		}  else if(replyServer.startsWith("USER_REQUESTING_BUDDY")) {
		                	   final String userRequestingToBeBuddy = replyServer.substring(22).trim();
		                       int choice = JOptionPane.showConfirmDialog(null,
		                       		                                   userRequestingToBeBuddy + " Has Requested You To Be A Buddy (Y/N)",
		                       		                                   "Buddy Request",
		                       		                                   JOptionPane.YES_NO_OPTION,
		                       		                                   JOptionPane.QUESTION_MESSAGE);
		
		                       if(choice == JOptionPane.YES_OPTION)   {
		                           sendMessage("ACCEPTED_BUDDY " + userRequestingToBeBuddy);
		                       } else {
		                           sendMessage("DECLINED_BUDDY " + userRequestingToBeBuddy);
		                       }
		    		}  else if(replyServer.startsWith("OFFLINE")) {
		                	   username = replyServer.substring(7).trim();
		                	   fTableModelBuddies.setRow(username, false);   
		    		}  else if(replyServer.startsWith("ONLINE")) {
		                	   final String userOnline = replyServer.substring(6).trim();
		                	   fTableModelBuddies.setRow(userOnline, true);
		    		}  else if(replyServer.startsWith("ACCEPTED")) {
		                	   name = replyServer.substring(8).trim();
		                       showMessage(name + " Has Accepted You as a Buddy"); 
		                       final String acceptedNameBold = "<html><b> " + name + " </b></html>";
		                       fTableModelBuddies.addRow(acceptedNameBold);
		    		}  else if(replyServer.startsWith("DECLINED")) {
		                	   name = replyServer.substring(8).trim();
		                       createErrorMessage(name + " Did Not Accept You as a Buddy.");                       	                         
		    		}  else if(replyServer.startsWith("FAIL_NOT_A_USER")) {
		                	   final String buddyNotUserName = replyServer.substring(14).trim();
		                       createErrorMessage(buddyNotUserName + " Is not a User!");
		    		}  else if(replyServer.startsWith("INVITE_REQUEST")) {
		                	   name = replyServer.substring(14).trim();
		                	   String inviteStatus = "INVITE_DECLINED ";
		                	   final int answer = JOptionPane.showConfirmDialog(null, 
		                			                         name + " Has Invited You to Chat.", 
		                			                         "Chat Invite",
		                			                         JOptionPane.YES_NO_OPTION);
		                	   if(answer == JOptionPane.YES_OPTION) {
		                		   inviteStatus = "INVITE_ACCEPTED ";
		                		   fClient.setChatWithName(name);
		                		   fNameChattingWith = name;
		                		   fClient.enableComponents(true);		                	   
		                	   }
		                	   sendMessage(inviteStatus + name);	                	   
		    		}  else if(replyServer.startsWith("START_CHAT")) {
		                	   fNameChattingWith = replyServer.substring(10).trim();
		                	   showMessage(fNameChattingWith + " Has Accepted Your Invitation to Chat");
		                	   fClient.setChatWithName(fNameChattingWith);
		                	   fClient.enableComponents(true);
		    		}  else if(replyServer.startsWith("INVITE_DECLINED")) {
		                	   name = replyServer.substring(15).trim();
		                	   showMessage(name + " Has Declined Your Invitation to Chat");
		    		}  else if(replyServer.startsWith("BUDDIES")) {
		                       name = readMessage();
		                       while(!(name.startsWith("END"))) {
		                    	   if(name.startsWith("ONLINE")) {
		                    		   name = name.substring(6).trim();
		                    		   name = "<html><b>" + name + "</b></html>";
		                    	   } else {
		                    		   name = name.substring(7).trim();
		                    	   }
		                    	  fBuddyList.add(name); 
		                          name = readMessage();
		                       }
		    		}  else if(replyServer.startsWith("MESSAGE")) {
		                       String message = replyServer.substring(7).trim();
		                       fClient.appendTextPane(message, false);	                       
		    		}  else if(replyServer.startsWith("OK_SIGNED_IN")) {
		                    	 fCsi.signInSuccess();
		    		}  else if(replyServer.startsWith("OK_ACCOUNT_CREATED")) {
		                    	 fCsi.createAccountSuccess(true);
		    		}  else if(replyServer.startsWith("FAIL_SIGN_IN")) {
		                    	 fCsi.signInFail("Sign In Incorrect");
		    		}  else if(replyServer.startsWith("FAIL_ALREADY_SIGNED_IN")) {
		                	   fCsi.signInFail("Already Signed In");
		    		}  else if(replyServer.startsWith("IN_CHAT")) {
		                    	 name = replyServer.substring(7);
		                    	 showMessage(name + " Is in Chat, Please Try Again Later.");
		    		}  else if(replyServer.startsWith("NOTIFY_DOWNLOAD")) {
		                	 replyServer = replyServer.substring(15).trim();
		                	 final int indexWS = replyServer.indexOf(" ");
		                	 fileName = replyServer.substring(0, indexWS).trim();	                	 
		                	 replyServer = replyServer.substring(indexWS).trim();
		                	 final int indexWS2 = replyServer.indexOf(" ");
		                	 final String fileSizeConverted = replyServer.substring(0, indexWS2).trim();
		                	 final String fileSizeActualString = replyServer.substring(indexWS2, replyServer.length()).trim();
		                	 final Long fileSizeActual = Long.parseLong(fileSizeActualString);
		                	 fFileSizeList.add(fileSizeActual);
		                	 fClient.addFileAttributes(fileName, fileSizeConverted, fileSizeActual);
		    		}  else if(replyServer.startsWith("PERMISSION_SENDING_FAIL")) {
		                	   createErrorMessage("Error Verifying Username, Please Signoff and try again");
		    		}  else if(replyServer.startsWith("PERMISSION_SENDING_SUCCESS")) {
		   		        	fileChooser = new JFileChooser(userDir);
		   		        	fileChooser.setMultiSelectionEnabled(true);
		   		        	fileChooser.setDialogTitle("Select File");
		   		        	fileChooser.setApproveButtonText("Send");
		   		        	fileChooser.setToolTipText("Send File");
		   		        	returnVal = fileChooser.showOpenDialog(null);
			   		            if(returnVal == JFileChooser.APPROVE_OPTION) {
			   		            	final File[] selectedFiles = fileChooser.getSelectedFiles();
			   		            	for(int index = 0; index < selectedFiles.length; index++) {
			   		            	    final File file = selectedFiles[index];	
			   		            	    final long fileSizeBytes = file.length();
			   		            	    //Convert to Readable
			   		            	    String convertedSize = "0";
			   		            	    final String[] units = new String[] {"B", "KB", "MB", "GB", "TB"};
			   		            	    final int digitGroups = (int)(Math.log10(fileSizeBytes)/Math.log10(1024));
			   		            	    convertedSize = new DecimalFormat("#,##0.#").format(fileSizeBytes/Math.pow(1024, digitGroups)) + units[digitGroups];
			   		            	    sendMessage("NOTIFY_DOWNLOAD " + file.getName() + " " + convertedSize + " " + fileSizeBytes);
			   		            	    fFileQueue.add(file);	
			   		            	}		            	
			   		            }	                	   
		    		}  else if(replyServer.startsWith("DOWNLOAD_FILE_LIST")) {
		                	   nameList = new ArrayList<String>();
		                	   fileName = "";
		                	   while(!(fileName = readMessage()).startsWith("END_FILE_NAME_LIST")){
		                		   nameList.add(fileName);
		                	   }
		                	   FileSendWorker sendWorker = new FileSendWorker(nameList, fFileQueue, fNameChattingWith);
		                	   sendWorker.execute();
		    		}  else if(replyServer.startsWith("PERMISSION_SENDING_IMAGE_SUCCESS")) {
		                	   fileChooser = new JFileChooser(userDir);
			   		        	fileChooser.setAcceptAllFileFilterUsed(false);
			   		        	final FileFilter imageFilter = new FileNameExtensionFilter("Image Files", 
			   		        			                                                    ImageIO.getReaderFileSuffixes());
			   		        	fileChooser.addChoosableFileFilter(imageFilter);
			   		        	fileChooser.setMultiSelectionEnabled(true);
			   		        	fileChooser.setDialogTitle("Select Image(s)");
			   		        	fileChooser.setApproveButtonText("Send");
			   		        	fileChooser.setToolTipText("Send Image(s)");
			   		        	returnVal = fileChooser.showOpenDialog(null);
				   		            if(returnVal == JFileChooser.APPROVE_OPTION) {
				   		            	final File[] selectedFiles = fileChooser.getSelectedFiles();
				   		            	final List<File> fileList = new ArrayList<File>();
				   		            	sendMessage("IMAGE_LIST " + fNameChattingWith);
				   		            	for(int index = 0; index < selectedFiles.length; index++) {
				   		            	    final File file = selectedFiles[index];
				   		            	    fileList.add(file);
				   		            	    final long fileSizeBytes = file.length();
				   		            	    sendMessage(file.getName() + " " + fileSizeBytes);
				   		            	}		            	
				   		            	fImageWorker = new ImageSendWorker(fileList, fNameChattingWith);
				   		            }
		    		}  else if(replyServer.startsWith("IMAGE_EXECUTE_SEND")) {
		                	   if(fImageWorker != null) {
		                		   fImageWorker.execute();   
		                	   }	   
		    		}  else if(replyServer.startsWith("IMAGE_LIST")) {
		                	   String imageStats = "";
		                	   nameList = new ArrayList<>();
		                	   final List<Integer> sizeList = new ArrayList<>();	                	   
		                	   while(!(imageStats = readMessage()).startsWith("END_IMAGE_LIST")) {
		                		   final int wsIndex = imageStats.indexOf(" ");
		                		   name = imageStats.substring(0, wsIndex).trim();
		                		   final String sizeStr = imageStats.substring(wsIndex, imageStats.length()).trim();
		                		   final int sizeConv = Integer.parseInt(sizeStr);
		                		   nameList.add(name);
		                		   sizeList.add(sizeConv);
		                	   }
		                	   final ImageDownloadWorker worker = new ImageDownloadWorker(fClient, nameList, sizeList);
		                	   worker.execute();
		                	   sendMessage("IMAGE_SEND_EXECUTE " + fNameChattingWith);
		    		}  else if(replyServer.startsWith("PERMISSION_DOWNLOAD_SUCCESS")) {
		                	   final List<DownloadFileModel> modelList = fClient.getFileModelList();	
		                	   final List<String> fileNameDownloadList = new ArrayList<String>();
		                	   final List<Long> fileSizeDownloadList = new ArrayList<Long>();
		                	   sendMessage("DOWNLOAD_FILE_LIST");
		                	   for(DownloadFileModel model : modelList) {
									if(model.isSelected()) {
										name = model.getName();
										fileNameDownloadList.add(name);
										final Long size = model.getActualSize();
										fileSizeDownloadList.add(size);
										sendMessage(name);
									}
								}
		                	   final FileDownloadWorker downloadWorker = new FileDownloadWorker(fPath, fClient,  
		                			                                                             fileNameDownloadList,
		                			                                                             fileSizeDownloadList);
		                	   downloadWorker.execute();
								sendMessage("END_FILE_NAME_LIST");
		    		}  else if(replyServer.startsWith("PERMISSION_DOWNLOAD_FAIL")) {
		                	   JOptionPane.showMessageDialog(null, 
	                                                         "Error Verifying User Please Signout and Try Again.", 
	                                                         "Download Error",
	                                                         JOptionPane.ERROR_MESSAGE);
		    		}  else if(replyServer.startsWith("IMAGE_SEND_EXECUTE")) {
		                	   if(fImageWorker != null) {
		                		   fImageWorker.execute();
		                	   }
		    		}  else if(replyServer.startsWith("CHANGE_PASSWORD")) {
		                	   username = replyServer.substring(15).trim();
		                	   new ChangePassDialog(username, this);
	               } else {
	            	   done = true;
	               }
		       }
    	 } catch(IOException ioe) {
    		 createErrorMessage("Network I/O Error!");
     	}
    }

	    public final String readMessage()  {
                String readString = "";                		
	    	try {
					readString = br.readLine();		
				} catch (IOException ioe) {
					createErrorMessage("Error Reading Client Message!");
				}
	    	   System.out.println("Client Rece>>" + readString + "<<");
	    	 return readString;
	    }
	    
	    public final void sendMessage(String aMessage) {
	    		try {
	    			bw.write(aMessage + '\n');
	    			bw.flush();
	    		} catch (IOException e) {
					createErrorMessage("Error Sending Message!");
				}
	            System.out.println("Client Send>>" + aMessage + "<<");
	    }
	    
	    public final void sendFileChunk(int aRead, byte[] aBuffer) {
	    	try {
	    	   bos.write(aBuffer, 0, aRead);
	    	   bos.flush();
	    	} catch(IOException ioe) {
	    		ioe.printStackTrace();
	    	}
	    }
	    
	    public final void setDownloadPath(String aPath) {
	    	this.fPath = aPath;
	    }
	    
	    public final void setChatClient(ChatClient aClient) {
	    	this.fClient = aClient;
	    }
	    
	    public final List<String> getRequestingBuddyList() {
	      return this.fRequestBuddyListNames;
	    }	
	    
	    public final List<String> getBuddyList() {
	    	return this.fBuddyList;
	    }
	    
	    public final void setTableModel(BuddyListTableModel aTableModel) {
	    	this.fTableModelBuddies = aTableModel;
	    }
	    
	    private final void showMessage(String aMessage) {
        	JOptionPane.showMessageDialog(null, 
                                          aMessage, 
                                          "JChat",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
	    
	    private final void createErrorMessage(String aMessage) {
	    		JOptionPane.showMessageDialog(null, 
	                                          aMessage, 
	                                          "Chat Error",
	                                          JOptionPane.ERROR_MESSAGE);
	    	}	    
}