package clientpackage;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

	public final class ImageDownloadWorker extends SwingWorker<Void, Void> {
	private Socket fSocket;
	private BufferedInputStream fBis;
	private final ChatClient fClient;
	private final List<String> fNameList;
	private final List<Integer> fSizeList;

    public ImageDownloadWorker(ChatClient aClient, List<String> aNameList, List<Integer> aSizeList) {
		this.fClient = aClient;
		this.fNameList = aNameList;
		this.fSizeList = aSizeList;
		try {
			fSocket = new Socket("127.0.0.1", 8084);
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
    	final List<BufferedImage> imageList = new ArrayList<BufferedImage>();
		for(int index = 0; index < fNameList.size(); index++)   {			
			final int fileSize = fSizeList.get(index);
			long totalBytesLeft = fileSize;
			byte[] buffer = new byte[8192];
			final ByteArrayOutputStream byteOut = new ByteArrayOutputStream(fileSize);
			int read = 0;
				while(totalBytesLeft > 0 &&  (read = fBis.read(buffer, 0, (int)Math.min(buffer.length, totalBytesLeft))) != -1) {
					byteOut.write(buffer, 0, read);
					totalBytesLeft -= read;
				}				
				final byte[] imageData = byteOut.toByteArray();
				final ByteArrayInputStream byteIn = new ByteArrayInputStream(imageData); 
			    final BufferedImage image = ImageIO.read(byteIn);
			    imageList.add(image);			  
		}
		fClient.addImageAttributes(fNameList, imageList);
		fSocket.close();
		} catch(IOException ioe) {
		ioe.printStackTrace();
		}		
		return null;
		}	
	}