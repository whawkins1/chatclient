package clientpackage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class ImageViewerDialog  implements ActionListener {
    private final List<ImageIcon> fImageIconList;
    private final List<BufferedImage> fImageBufList;
    private final List<String> fImageNameList;
    private int imageIndex;
    
    private final JButton fSaveButton;
    private final JButton fSaveAllButton;
    private final JButton fCloseButton;
    private final JButton fPreButton;
    private final JButton fNextButton;
    private final JLabel fImageLabel;
    private final JPanel fButtonPanel;
    private final JDialog fDialog;
	
	public ImageViewerDialog(List<String> aNameList, List<BufferedImage> aImageList) {
         this.fImageBufList = aImageList;
         this.fImageNameList = aNameList;
         final FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
         fButtonPanel = new JPanel(layout);
    	 fSaveButton = createButton("Save", "SAVE");
    	 fSaveAllButton = createButton("Save All", "SAVE_ALL");
    	 fCloseButton = createButton("Close", "CLOSE");
    	 fPreButton = createButton("<", "PREV_IMAGE");
    	 fNextButton = createButton(">", "NEXT_IMAGE");
    	 final int imageBufListSize = aImageList.size();
    	 if(imageBufListSize == 1) {
    		 fNextButton.setEnabled(false);
    		 fPreButton.setEnabled(false);
    	 }
    	 fImageIconList = new ArrayList<ImageIcon>(imageBufListSize);
    	 for(int index = 0; index < imageBufListSize; index++) {
    		 final BufferedImage imageBuf = aImageList.get(index);
    		 final ImageIcon icon = new ImageIcon(imageBuf);
    		 fImageIconList.add(icon);
    	 }
    	 imageIndex = 0;
         final ImageIcon image = fImageIconList.get(imageIndex); 
         fImageLabel = new JLabel();
         fImageLabel.setIcon(image);         
         final JPanel imagePanel = new JPanel();
    	 imagePanel.setLayout(new BorderLayout());
         imagePanel.add(fImageLabel);
     	 fDialog = new JDialog();
     	 setTitle(image);
     	 fDialog.setLayout(new BorderLayout());
     	 fDialog.add(imagePanel, BorderLayout.CENTER);
     	 fDialog.add(fPreButton, BorderLayout.WEST);
     	 fDialog.add(fNextButton, BorderLayout.EAST);
     	 fDialog.add(fButtonPanel, BorderLayout.SOUTH);
     	 fDialog.pack();
     	 fDialog.setModal(false);
    	 fDialog.setLocationRelativeTo(null);
     	 fDialog.setVisible(true);
	}
	
	private final void setTitle(ImageIcon aImage) {
		final String imageName = fImageNameList.get(imageIndex);
		 final String imageHeight = Integer.toString(aImage.getIconHeight());
    	 final String imageWidth = Integer.toString(aImage.getIconWidth());
    	fDialog.setTitle(imageName + " Dimensions: " + imageWidth + " x " + imageHeight);
	}
	
	private final String openSaveDialog() {
		final String userDir = System.getProperty("user.home");
     	final JFileChooser imageChooser = new JFileChooser(userDir);
     	imageChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
     	imageChooser.setDialogTitle("Select Location");
     	imageChooser.setApproveButtonText("OK");
     	final int returnVal = imageChooser.showOpenDialog(null);
     	String path = "";
     	if(returnVal == JFileChooser.APPROVE_OPTION) {
     		final File directory = imageChooser.getSelectedFile();
     		path = directory.getAbsolutePath();
     	}
     	return path;     	
	}
	
	private final void saveImage(String aName, BufferedImage aImageBuf, String aPath) {
		String extension = "";
		int lastIndex = aName.lastIndexOf('.');
		if(lastIndex > 0) {
			extension = aName.substring(lastIndex++);
		}
		final File imageFile = new File(aName);		
		try {
		    ImageIO.write(aImageBuf, extension, imageFile);
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
		public final void actionPerformed(ActionEvent ae) {
			final String command = ae.getActionCommand();
			if(command.equals("SAVE")) {
				final String path = openSaveDialog();
		         final BufferedImage imageBuf = fImageBufList.get(imageIndex);
		         final String imageName = fImageNameList.get(imageIndex);
		         path.concat(imageName);
		         saveImage(imageName, imageBuf, path);
			} else if(command.equals("SAVE_ALL")) {
				final String path = openSaveDialog();
				for(int index = 0; index < fImageBufList.size(); index++) {
					final String name = fImageNameList.get(index);	
					final BufferedImage imageBuf = fImageBufList.get(index);
					path.concat(name);
					saveImage(name, imageBuf, path);
				}
			} else if (command.equals("CLOSE")) {
				fDialog.dispose();				
			} else if(command.equals("PREV_IMAGE")) {
				imageIndex--;
				if(imageIndex == 0) {
					fPreButton.setEnabled(false);
				} else if(imageIndex == 1) {
					fPreButton.setEnabled(true);
				}
				final ImageIcon image = fImageIconList.get(imageIndex);
				fImageLabel.setIcon(image);
				setTitle(image);
			} else if(command.equals("NEXT_IMAGE")){
				imageIndex--;
				int size = fImageIconList.size();
				if(imageIndex == size) {
					fNextButton.setEnabled(false);
				} else if(imageIndex == size - 1) {
					fNextButton.setEnabled(true);
				}				
				final ImageIcon image = fImageIconList.get(imageIndex);
				fImageLabel.setIcon(image);
				setTitle(image);
			}
		}
		
		private final JButton createButton(String aTitle, String aCommand) {
			final JButton b = new JButton();
			b.setText(aTitle);
			b.addActionListener(this);
			b.setActionCommand(aCommand);
			fButtonPanel.add(b);
			return b;
		}
}
