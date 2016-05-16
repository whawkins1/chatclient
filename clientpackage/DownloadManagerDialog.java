package clientpackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public final class DownloadManagerDialog extends JDialog 
                                              implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final List<String> fFileNameList;
	private final List<String> fFileSizeConvertedList;
	private final List<Long> fFileSizeActualList;
	private final JTable fTable;
	private final JButton fDownloadButton;
	private final JButton fRemoveButton;
	private final JButton fCloseButton;
	private final DownloadTableModel fTableModel;
	private final ConnectionToServer fCts;
	private final List<DownloadFileModel> fFileModelList; 
	private final CheckBoxHeader fCbh;
	private final ChatClient fClient;
	
	public DownloadManagerDialog(List<String> aFileNameList, List<String> aFileSizeConvertedList,
                                 List<Long> aFileSizeActualList, ConnectionToServer aCts, 
                                 ChatClient aClient) {
		this.fFileNameList = aFileNameList;
		this.fFileSizeConvertedList = aFileSizeConvertedList;
		this.fFileSizeActualList = aFileSizeActualList;
		this.fCts = aCts;
		this.fClient = aClient;
		
		final int size = fFileNameList.size();
		 fFileModelList = new ArrayList<DownloadFileModel>(size);
  	    for(int index = 0; index < size; index++) {
  		   final String name = fFileNameList.get(index);
  		   final String fileSizeConverted = fFileSizeConvertedList.get(index);
  		   final Long fileSizeActual = fFileSizeActualList.get(index);
  		   final DownloadFileModel fileModel = new DownloadFileModel(name, fileSizeConverted, 
  				                                                     fileSizeActual);
  	       fFileModelList.add(fileModel);
  	    }
		fTableModel = new DownloadTableModel(fFileModelList, this);
		fTable = new JTable();
		fTable.setModel(fTableModel);
		
		//Set Column Adjust Data Size
		for(int column = 0; column < 2; column++) {
			final DefaultTableColumnModel colModel = (DefaultTableColumnModel) fTable.getColumnModel();
			final TableColumn col = colModel.getColumn(column);
			
			TableCellRenderer renderer = col.getHeaderRenderer();
			if(renderer == null) {
				renderer = fTable.getTableHeader().getDefaultRenderer();
			}
			Component comp = renderer.getTableCellRendererComponent(fTable, col.getHeaderValue(), false, false, 0, 0);
			int width = comp.getPreferredSize().width;
			
			for(int row = 0; row < fTable.getRowCount(); row++) {
				renderer = fTable.getCellRenderer(row, column);
				comp = renderer.getTableCellRendererComponent(fTable, fTable.getValueAt(row, column), false, false, row, column);
			    final int currentWidth = comp.getPreferredSize().width;
			    width = Math.max(width, currentWidth);
			}			
			width += (2 * 2);
			col.setPreferredWidth(width);			
		}
		final TableColumnModel columnModel = fTable.getColumnModel();
		final TableColumn columnStatus = columnModel.getColumn(3);
		columnStatus.setCellRenderer(new ProgressCellRenderer());
		final TableColumn tableColumn = columnModel.getColumn(0);
		final CheckBoxListener cbl = new CheckBoxListener(fFileModelList);
		fCbh = new CheckBoxHeader(cbl);
		tableColumn.setHeaderRenderer(fCbh);
		fTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		fTable.setFocusable(false);
		fTable.setShowGrid(false);
		fTable.setRowSelectionAllowed(true);
		fTable.setCellSelectionEnabled(true);
		fTable.setColumnSelectionAllowed(false);
		fTable.setRowSelectionAllowed(true);
		fTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		fTable.setIntercellSpacing(new Dimension(0, 0));
		fTable.setPreferredScrollableViewportSize(fTable.getPreferredSize());
		final JScrollPane scrollPane = new JScrollPane(fTable, 
				                                       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                       JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		final JViewport vp = scrollPane.getViewport();
		final Dimension d = vp.getPreferredSize();
		final int width = (int)d.getWidth();
		final Dimension vpDim = new Dimension(width, 200);
		vp.setPreferredSize(vpDim);
	    vp.setBackground(Color.WHITE);
	    final JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BorderLayout());
		tablePanel.add(scrollPane);
	    fDownloadButton = createButton("DownLoad", "DOWNLOAD", false);
		fRemoveButton = createButton("Remove", "REMOVE", false);
		fCloseButton = createButton("Close", "CLOSE", true);
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));		
		buttonPanel.add(fDownloadButton);
		buttonPanel.add(fRemoveButton);
		buttonPanel.add(fCloseButton);
		setLayout(new BorderLayout());
		add(tablePanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Download Manager");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);		
	}
	
		public final void actionPerformed(ActionEvent ae) {
			final String command = ae.getActionCommand();
			if(command.equals("DOWNLOAD")) {
				final String userDir = System.getProperty("users.home");
				final JFileChooser saveChooser = new JFileChooser(userDir);
				saveChooser.setMultiSelectionEnabled(false);
				saveChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				saveChooser.setDialogTitle("Select Directory");
				saveChooser.setApproveButtonText("Save");
				saveChooser.setToolTipText("Save Files");
	        	final int returnVal = saveChooser.showOpenDialog(this);
	        	if(returnVal == JFileChooser.APPROVE_OPTION) {
	        		final File selectedDir = saveChooser.getSelectedFile();
	        		final String path = selectedDir.getAbsolutePath();	        		
	        		fCts.setDownloadPath(path);
	        		final String userName = fClient.getUserName();
					fCts.sendMessage("VERIFY_DOWNLOAD_PERMISSION " + userName);
				}
			} else if(command.equals("REMOVE")) {
				final List<DownloadFileModel> modelList = fTableModel.getDownloadModelList();
				for(DownloadFileModel model : modelList) {
					if(model.isSelected()) {
						modelList.remove(model);
					}
				}
			} else if(command.equals("CLOSE")) {
				setVisible(false);
			}
		}
		
		public final void selectHeaderBoxStatus(boolean aSelected) {
			final boolean colHeaderSelected = fCbh.isSelected();
			if(!aSelected && colHeaderSelected) {
			     fCbh.setSelected(false);
			} else if(aSelected && !colHeaderSelected) {
				//Check If All Selected
				boolean allSelected = true;
				for(DownloadFileModel model : fFileModelList) {
					if(!(model.isSelected())) {
						allSelected = false;
					    break;
					}
				}							
				if(allSelected) {
					fCbh.setSelected(true);
				}
			}
			final JTableHeader tableHeader = fTable.getTableHeader();
			tableHeader.repaint();
		}
		
		public final void updateStatus(File aFile, int aProgress) {
			fTableModel.updateStatus(aFile, aProgress);
		}
		
		public final void selectEnableButtons(boolean aSelected) {
			if(!aSelected) {
				boolean foundSelected = false;
				for(DownloadFileModel model : fFileModelList) {
					if(model.isSelected()) {
						foundSelected = true;
						break;
					}
				}				
				aSelected = foundSelected;				
			} 
				fDownloadButton.setEnabled(aSelected);
				fRemoveButton.setEnabled(aSelected);			
		}
		
		public final List<DownloadFileModel> getFileModelList() {
			return this.fFileModelList;
		}
		
		private final JButton createButton(String aTitle, String aCommand, Boolean aEnable) {
			final JButton b = new JButton();
			b.setText(aTitle);
			b.addActionListener(this);
			b.setActionCommand(aCommand);
			b.setEnabled(aEnable);
			return b;
		}
		
   }
		
		final class CheckBoxListener implements ItemListener {
			final private List<DownloadFileModel> fModelList;
			
			public CheckBoxListener(List<DownloadFileModel> aFileModelList) {
				this.fModelList = aFileModelList;
			}
			
			@Override
			public final void itemStateChanged(ItemEvent ie) {
				final boolean checked = (ie.getStateChange() == ItemEvent.SELECTED);
				    for(DownloadFileModel file : fModelList) {
						file.setSelect(checked);
					}				
			}			
		}
		
		final class CheckBoxHeader extends JCheckBox
		                        implements TableCellRenderer, MouseListener {
			private static final long serialVersionUID = 1L;
			private CheckBoxHeader rendererComponent;
			private int column;
			private boolean mousePressed = false;
			
			public CheckBoxHeader(ItemListener itemListener) {
				rendererComponent = this;
				rendererComponent.addItemListener(itemListener);
			}
			
			final public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
				
				if(table.isEnabled()) {
					final JTableHeader header = table.getTableHeader();
					if(header.isEnabled()) {
						header.addMouseListener(rendererComponent);
					}
				}
				this.column = column;
				return rendererComponent;
			}
			
			private final void handleClickEvent(MouseEvent me) {
				if(mousePressed) {
					mousePressed = false;
					final JTableHeader header = (JTableHeader)me.getSource();
					final JTable table = header.getTable();
					final TableColumnModel model = table.getColumnModel();
					final int viewColumn = model.getColumnIndexAtX(me.getX());
					final int column = table.convertColumnIndexToModel(viewColumn);
					
					if((viewColumn == this.column) && (me.getClickCount() == 1) 
						 && (column != -1)) {
						doClick();
					}
				}
			}
			
			@Override
			   final public void mouseClicked(MouseEvent e)  {
				   handleClickEvent(e);
				   ((JTableHeader)e.getSource()).repaint();
			   }
			   
			   @Override
			   final public void mousePressed(MouseEvent e)  {
				   mousePressed = true;
			   }
			   
			   @Override
			   final public void mouseReleased(MouseEvent e) {
				   
			   }
			   
			   @Override
			   final public void mouseEntered(MouseEvent e)  {
				   
			   }
			   
			   @Override
			   final public void mouseExited(MouseEvent e)  {
				   
			   }
		}
		
		final class ProgressCellRenderer extends JProgressBar
		                                      implements TableCellRenderer {
			private static final long serialVersionUID = 1L;

			@Override
			public final Component getTableCellRendererComponent(JTable table, 
					                                             Object value,
					                                             boolean isSelected,
					                                             boolean hasfocus,
					                                             int row, 
					                                             int column){
				final Dimension d = new Dimension(200, this.getHeight()); 
				setPreferredSize(d);
				setStringPainted(true);
				int progress = 0;
				if(value instanceof Float) {
					progress = Math.round(((Float) value) * 100f);
				} else if(value instanceof Integer) {
					progress = (int) value;
				}
				setValue(progress);
				final String progressString = Integer.toString(progress);
				setString(progressString + "%");
				return this;
	    		}
		}