package clientpackage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

public final class StatusBarPanel extends JPanel
                                        implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JButton fDownloadFileButton;
    private JButton fViewImageButton;
    private final List<String> fFileNameList;
    private final List<String> fFileSizeConvertedList;
    private final List<Long> fFileSizeActualList;
    private List<BufferedImage> fImageList;
    private List<String> fImageNameList;
    private final ConnectionToServer fCts;
    private final ChatClient fClient;
    private DownloadManagerDialog fManager;
    
	public StatusBarPanel(ConnectionToServer aCts, ChatClient aClient) {
		this.fCts = aCts;
		this.fClient = aClient;
		fManager = null;
		fFileNameList = new ArrayList<String>();
		fFileSizeConvertedList = new ArrayList<String>();
		fFileSizeActualList = new ArrayList<Long>();
		final Border loweredBorder = BorderFactory.createLoweredBevelBorder();
    	setBorder(loweredBorder);
    	setLayout(new BorderLayout());
	}
	
		public final void actionPerformed(ActionEvent ae){
            final String command = ae.getActionCommand();
			if(command.equals("OPEN_MANAGER")) {
				fManager = new DownloadManagerDialog(fFileNameList, fFileSizeConvertedList, 
						                             fFileSizeActualList, fCts, fClient);				
			} else if(command.equals("OPEN_IMAGE_VIEWER")) {
				new ImageViewerDialog(fImageNameList, fImageList);
			}
		}
		
		public final void addFileAttributes(String aFileName, String aFileSizeConverted, 
				                            Long aFileSizeActual) {
			fDownloadFileButton = createButton("OPEN_MANAGER", "View Downloads");
			fFileNameList.add(aFileName);
			fFileSizeConvertedList.add(aFileSizeConverted);
			fFileSizeActualList.add(aFileSizeActual);
			fDownloadFileButton.setVisible(true);
			fDownloadFileButton.setEnabled(true);
			String aTitle = fFileNameList.size() > 1 ? " Downloads" : " Download";
			aTitle = (fFileNameList.size() + aTitle);
			fDownloadFileButton.setText(aTitle);		
			add(fDownloadFileButton, BorderLayout.WEST);
		    validate();
		    repaint();
		}
		
		public final void addImageAttributes(List<String> aNameList, List<BufferedImage> aImageList) {
			fViewImageButton = createButton("OPEN_IMAGE_VIEWER", "View SlideShow");
			this.fImageNameList = aNameList;
			this.fImageList = aImageList;
			fViewImageButton.setVisible(true);
			fViewImageButton.setEnabled(true);
			final String imageTitle = ((aNameList.size() == 1) ? "Image Available" : 
				                                               aNameList.size() +"Images Sent");
			fViewImageButton.setText(imageTitle);		
			add(fViewImageButton, BorderLayout.EAST);
			validate();
			repaint();
		}
	    
		private final JButton createButton(String aCommand, String aToolTipText) {
			final JButton b = new JButton();
			b.setToolTipText(aToolTipText);
			b.setContentAreaFilled(false);
			b.setFocusPainted(false);
			b.setBorder(BorderFactory.createEmptyBorder());
			b.setVisible(false);
			b.setEnabled(false);
			b.addActionListener(this);
			b.setActionCommand(aCommand);
			return b;
		}	
		
		public final List<DownloadFileModel> getFileModelList() {
			return fManager.getFileModelList();
		}
		
		public final void setDownloadText(String aStatus) {
			this.fDownloadFileButton.setText(aStatus);
		}
		
		public final void updateDownload(File aFile, int aProgress) {
			fManager.updateStatus(aFile, aProgress);
		}
		
		public final boolean downloadManagerOpen() {
			return (fManager == null ? false : true);
		}
		
		public final void closeDownloadManager() {
			fManager.dispose();
		}
}