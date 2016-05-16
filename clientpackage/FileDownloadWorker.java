package clientpackage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import javax.swing.SwingWorker;

public final class FileDownloadWorker 
                        extends SwingWorker<Void, Void> {
    private Socket fSocket;
    private BufferedInputStream fBis;
    private final String fPath;
    private final ChatClient fClient;
    private final int fNumDownloads;
    private final List<String> fNameList;
    private final List<Long> fSizeList;
    
	public FileDownloadWorker(String aPath, ChatClient aClient, 
			                  List<String> aNameList, List<Long> aSizeList) {
		this.fPath = aPath;
		this.fClient = aClient;
		this.fNameList = aNameList;
		this.fSizeList = aSizeList;
		this.fNumDownloads = fNameList.size();
		try {
			fSocket = new Socket("127.0.0.1", 8081);
			final OutputStream os = fSocket.getOutputStream();
			final DataOutputStream dos = new DataOutputStream(os);
			dos.writeUTF(fClient.getUserName());
			final InputStream is = fSocket.getInputStream();
			final DataInputStream dis = new DataInputStream(is);
			fBis = new BufferedInputStream(dis);			
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}		
	}
	
	public Void doInBackground() {
		try {
			for(int index = 0; index < fNumDownloads; index++) {
				final String fileName = fNameList.get(index);
				final Long fileSize = fSizeList.get(index);
				final File file = new File(fPath + File.separator + fileName);		                   
		        final FileOutputStream fos = new FileOutputStream(file);
		        final DataOutputStream dos = new DataOutputStream(fos);
		        final BufferedOutputStream bos = new BufferedOutputStream(dos);
		        fClient.setDownloadStatus(index + 1, fNumDownloads);
		        long totalBytesLeft = fileSize;
		        long totalBytesRead = 0;
		        byte[] buffer = new byte[8192];
		        int read = 0;
		        while(totalBytesLeft > 0 &&  (read = fBis.read(buffer, 0, (int)Math.min(buffer.length, totalBytesLeft))) != -1) {
		           bos.write(buffer, 0, read);	
		           totalBytesLeft -= read;
		           totalBytesRead += read;
		           int progress = ((int) Math.round(((double) totalBytesRead / (double) fileSize) * 100d));
		           fClient.updateStatus(file, progress);
		        }
		        bos.close();
			}   
	        fSocket.close();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}		
		return null;
	}	
}