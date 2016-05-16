package clientpackage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import javax.swing.SwingWorker;

public final class ImageSendWorker extends SwingWorker<Void, Void>{	
	     private final List<File> fFileList;
	     private DataOutputStream fDos;
	     private final String fNameChatWith;
	     private Socket fSocket;
	     private BufferedOutputStream fBos;
	     public ImageSendWorker(List<File> aFileList, String aNameChatWith) {
	    	 this.fNameChatWith = aNameChatWith;
			 this.fFileList = aFileList;   
	     }
		 
		 public final Void doInBackground() {
			 try {
				 fSocket = new Socket("127.0.0.1", 8083);
				 final OutputStream os = fSocket.getOutputStream();
				 fDos = new DataOutputStream(os);
				 fDos.writeUTF(fNameChatWith);
				 //fDos.flush();
				 fBos = new BufferedOutputStream(fDos);
				 for(File file : fFileList) {
		      		final FileInputStream fileStream = new FileInputStream(file);
		      		final BufferedInputStream bis = new BufferedInputStream(fileStream);
		      		final byte[] buffer  = new byte[8192];
		      		int read = 0;
		      		
		      		while((read = bis.read(buffer)) != -1) {
		      			fBos.write(buffer, 0, read);
		      		}
		      		fBos.flush();
		      		fileStream.close();
		      	}
				 fSocket.close();
			 } catch(IOException ioe) {
				 ioe.printStackTrace();
			 }
		   return null;
		 }
	}