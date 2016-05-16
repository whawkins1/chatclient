package clientpackage;

import java.io.File;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public final class DownloadTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    private final List<DownloadFileModel> fFileModelList; 
    private final DownloadManagerDialog fManager;
    private final String[] fColumnNames = {" ", "Name", "Size", "Progress"};
	
	 public DownloadTableModel(List<DownloadFileModel> aFileModelList, 
			                            DownloadManagerDialog aManager) {
		 this.fFileModelList = aFileModelList;
		 this.fManager = aManager;
     }
	 
		 @Override
		 public final boolean isCellEditable(int aRow, int aColumn) {
			 return (aColumn == 0);
		 }
		 
		 @Override
		 public final int getColumnCount() {
			 return 4;
		 }
	
	     @Override
	     public final String getColumnName(int aColumn) {
	    	 return fColumnNames[aColumn];
	     }
	     
	     @Override
	     public final Class<?> getColumnClass(int aColumn) {
	    	 return getValueAt(0, aColumn).getClass();
	     }
	     
	     @Override
	     public final int getRowCount() {
	    	 return fFileModelList.size();
	     }     
	     
	     @Override
	     public final void setValueAt(Object value, int aRow, int aColumn) {
	    	 final DownloadFileModel model = fFileModelList.get(aRow);
	    	 if(aColumn == 0 && value instanceof Boolean) {
	    		 final String boolString = value.toString();
	    		 final boolean selected = Boolean.valueOf(boolString);
	    		 model.setSelect(selected);
	    		 fManager.selectHeaderBoxStatus(selected);
	    		 fManager.selectEnableButtons(selected);
	    	 } else if (aColumn == 3) {
	    		 if(value instanceof Float) {
	    			 model.setStatus((Float) value);
	    		 }
	    	 }
	     }
	     
	     @Override
	     public final Object getValueAt(int aRow, int aColumn) {
	    	 final DownloadFileModel model = fFileModelList.get(aRow);
	    	 if(aColumn == 0) {
	    		 return model.isSelected();
	    	 } else if (aColumn == 1) {
	    		 return model.getName();
	    	 } else if (aColumn == 2) {
	    		 return model.getConvertedSize();
	    	 } else if (aColumn == 3) {
	    		 return model.getStatus();
	    	 } else {
	    		 return null;
	    	 }
	     }     
	     
	     public final List<DownloadFileModel> getDownloadModelList() {
	    	 return this.fFileModelList;
	     }
	     
	    /* public final void addFile(String aName, String aSize) {
	    	 DownloadFileModel model = new DownloadFileModel(aName, aSize);
	    	 fFileModelList.add(model);
	    	 final int size = fFileModelList.size();
	    	 fireTableRowsInserted(size - 1, size - 1);
	     }*/
	     
	     public final void updateStatus(File aFile, int aProgress) {
			 final String name = aFile.getName();
	    	 int modelIndex = 0;
	    	 for(DownloadFileModel model : fFileModelList) {
	    		 final String modelFileName = model.getName();
	    		 if(modelFileName.equals(name)) {
	    		     modelIndex = fFileModelList.indexOf(model);
	    			 break;
	    		 }
	    	 }
	    	 final Float currProgress = ((Float) (aProgress / 100f));
	         setValueAt(currProgress, modelIndex, 3);
	         fireTableCellUpdated(modelIndex, 3);
	     }
	     
}