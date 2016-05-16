package clientpackage;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public final class RequestingTableModel extends AbstractTableModel  {
	
	private static final long serialVersionUID = 1L;
	private final List<String> fRequestingList;
	private final int fRowCount;
	private final List<SourceRequestingModel> fSourceList;
	private String[] fColumnNames = {"Name", "Accept", "Decline"};
	
	
	public RequestingTableModel(List<String> aRequestingList) {
		    this.fRequestingList = aRequestingList;
		    this.fRowCount = fRequestingList.size();
		    fSourceList = new ArrayList<SourceRequestingModel>();
	    	//Populate Source List
		    for(int index = 0;  index < fRowCount; index++) {
	    		final String name = fRequestingList.get(index);
	    		final SourceRequestingModel sourceModel = new SourceRequestingModel(name, false, false);
	    	    fSourceList.add(sourceModel);
	    	}
		}
		
	    @Override
	    public final boolean isCellEditable(int aRow, int aColumn) {
	    	return (aColumn == 1 || aColumn == 2);
	    }
	
	    @Override
	    public final int getColumnCount() {
			return 3;
		}
		
	    @Override
	    public final String getColumnName(int aColumn) {
	    	return fColumnNames[aColumn];
	    }
	    
	    @Override
	    public final Class<?> getColumnClass(int aColumn) {
	    	return ((aColumn == 1 || aColumn == 2) ? Boolean.class : String.class);
	    }
	    
	    @Override
		public final int getRowCount() {
			return fRowCount;
		}
		
	    @Override
	    public final void setValueAt(Object aValue, int aRow, int aColumn) {
	    	final SourceRequestingModel model = fSourceList.get(aRow);
	    	if(aColumn == 1) {
	    		syncButtons(model, true, false);
	    	} else if(aColumn == 2) {
	    		syncButtons(model, false, true);
	    	}
	    	fireTableCellUpdated(aRow, aColumn);
	    }
	    
	    @Override
		public final Object getValueAt(int aRow, int aColumn) {
			final SourceRequestingModel model = fSourceList.get(aRow);
			if(aColumn == 0) {
				return model.getName();
			} else if(aColumn == 1) {
				return model.isAccepted();
			} else if(aColumn == 2) {
				return model.isDeclined();
			} else {
				return null;
			}
		}
	    
	    public final List<SourceRequestingModel> getSourceModelList() {
	    	return this.fSourceList;
	    }
	    
	    private final void syncButtons(SourceRequestingModel aModel, boolean aAccept, boolean aDecline) {
	    	aModel.setAccept(aAccept);
	    	aModel.setDecline(aDecline);
	    	fireTableRowsUpdated(0, fRowCount - 1);
	    }
}