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

public final class FileSendWorker extends SwingWorker<Void, Void>{
     private final List<File> fFileQueue;
     private final List<String> fNameList;
     private DataOutputStream fDos;
     private final String fNameChatWith;
     private Socket fSocket;
     private BufferedOutputStream fBos;
     public FileSendWorker(List<String> aNameList, List<File> aFileQueue, String aNameChatWith) {
    	 this.fNameList = aNameList;
    	 this.fNameChatWith = aNameChatWith;
		 this.fFileQueue = aFileQueue;   
		 try {
			 fSocket = new Socket("127.0.0.1", 8082);
			 final OutputStream os = fSocket.getOutputStream();
			 fDos = new DataOutputStream(os);
			 fDos.writeUTF(fNameChatWith);
			 fDos.flush();
			 fBos = new BufferedOutputStream(fDos);			 
		 } catch(IOException ioe) {
			 ioe.printStackTrace();
		 }		 
     }
	 
	 public final Void doInBackground() {
		 try {
			 for(String name : fNameList) {
	 	    		File fileDownload = null;
	      		for(File file : fFileQueue) {
	      			final String fileNameCheck = file.getName();
	      			if(fileNameCheck.equals(name)) {
	      				fileDownload = file;
	      				break;
	      			}
	      		}	      		
	      		final FileInputStream fileStream = new FileInputStream(fileDownload);
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