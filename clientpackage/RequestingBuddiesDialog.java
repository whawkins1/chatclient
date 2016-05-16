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
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public final class RequestingBuddiesDialog  
                                  implements ActionListener  {
    private final List<String> fRequestingBuddyList;
    private final List<String> fBuddyList;
    private final RequestingTableModel fRequestingTableModel;
    private final ConnectionToServer fCts;
    private final JTable fTable;
    private final JDialog fDialog;
    private final JButton fOkButton;
    private final JButton fCloseButton;
    
	public RequestingBuddiesDialog(JFrame aFrame, String aUsername, ConnectionToServer aCts, List<String> aBuddyList) {
		this.fBuddyList = aBuddyList;
		this.fCts = aCts;
		fRequestingBuddyList = fCts.getRequestingBuddyList(); 
		fTable = new JTable();
		fRequestingTableModel = new RequestingTableModel(fRequestingBuddyList);
		fTable.setModel(fRequestingTableModel);
        fTable.setShowGrid(false);
	    fTable.setRowSelectionAllowed(true);
	    fTable.setCellSelectionEnabled(false);
	    fTable.setColumnSelectionAllowed(false);
	    fTable.setShowHorizontalLines(true);
	    fTable.setShowVerticalLines(true);
        final JTableHeader header = fTable.getTableHeader();
	    header.setDefaultRenderer(new HeaderRenderer());
        setColumnRendEditor();
        final JScrollPane scrollPane = new JScrollPane(fTable, 
                                                       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                       JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        final JViewport vp = scrollPane.getViewport(); 
        vp.setBackground(Color.WHITE);
        vp.setPreferredSize(new Dimension(300, 200));
        final JPanel buttonPanel = new JPanel();
        fOkButton = createButton("Ok", "OK");
        fCloseButton = createButton("Cancel", "CANCEL");
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(fOkButton);
        buttonPanel.add(fCloseButton);
        fDialog = new JDialog(aFrame, "Requesting Buddies"); 
		fDialog.setLayout(new BorderLayout());
	    fDialog.add(scrollPane, BorderLayout.NORTH);
	    fDialog.add(buttonPanel, BorderLayout.SOUTH);
	    fDialog.setTitle("Requesting Buddies");
	    fDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    fDialog.pack();
	    fDialog.setLocationRelativeTo(aFrame);
	    fDialog.setVisible(true);
	}
	
	private final void setColumnRendEditor() {
		for(int index = 1; index <= 2; index++) {
			final TableColumnModel tableColumnModel = fTable.getColumnModel();
	        final TableColumn column = tableColumnModel.getColumn(index);
	        column.setCellRenderer(new RadioButtonRenderer());
	        final JCheckBox checkBox = new JCheckBox();
	        checkBox.setHorizontalAlignment(SwingConstants.CENTER);
	        checkBox.setOpaque(false);
	        column.setCellEditor(new ButtonEditor(checkBox));	
		}		
	}
	
	public final void actionPerformed(ActionEvent ae) {
		final String command = ae.getActionCommand();
		if(command.equals("OK")){
			final List<SourceRequestingModel> sourceModelList = fRequestingTableModel.getSourceModelList();
		    for(int index = 0; index < sourceModelList.size(); index++) {
		    	String buddyStatus = "";
		    	final SourceRequestingModel requestingBuddy = sourceModelList.get(index); 
		    	if(requestingBuddy.isAccepted())  {
		    		fBuddyList.add(requestingBuddy.getName());
		    	    buddyStatus = "ACCEPTED_BUDDY ";
		    	} else if(requestingBuddy.isDeclined()) {
		    		buddyStatus = "DECLINED_BUDDY ";
		    	}
		    	if(!buddyStatus.isEmpty()) {
		    		fRequestingBuddyList.remove(index);
		    		fCts.sendMessage(buddyStatus + requestingBuddy.getName());
		    	}
		    }
		 } 
		fDialog.dispose();
	}
	
	final class RadioButtonRenderer  
	                      implements TableCellRenderer {
         private final JRadioButton button;   
		public RadioButtonRenderer() {
			button = new JRadioButton();
		    button.setOpaque(false);
		    button.setHorizontalAlignment(SwingConstants.CENTER);
		}
        
		@Override
		public Component getTableCellRendererComponent(JTable table,  Object value, 
		                              boolean isSelected, boolean hasFocus, 
		                              int row, int column) {
		         button.setSelected((Boolean) value);
		return button;
		}
	}

     final class ButtonEditor extends DefaultCellEditor 
                                         implements ItemListener {
		private static final long serialVersionUID = 1L;
		private final JRadioButton button;
		 		
	  public ButtonEditor(JCheckBox aCheckBox) {
		super(aCheckBox);
		button = new JRadioButton();
		button.addItemListener(this);
		button.setOpaque(false);
		button.setHorizontalAlignment(SwingConstants.CENTER);
	  }
	  
	  public final Component getTableCellEditorComponenet(JTable table, Object value, 
			                                             boolean isSelected, int row, int column) {
		  button.setSelected((Boolean)value);
	      return button;
	  }
	  
	  @Override
	  public final Object getCellEditorValue() {
		  return Boolean.valueOf(button.isSelected());
	  }
	  
	  @Override
	  public final void itemStateChanged(ItemEvent ie) {
		  super.fireEditingStopped();
	  }
	}
     
     final class HeaderRenderer 
                         implements TableCellRenderer {
    	 final DefaultTableCellRenderer renderer;
    	 public HeaderRenderer() {
    		 final JTableHeader tableHeader = fTable.getTableHeader();
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
	
	public final JButton createButton(String label, String actionCommand)    {
        final JButton b = new JButton(label);
        b.setActionCommand(actionCommand);
        b.addActionListener(this);
        return b;
    }
}
