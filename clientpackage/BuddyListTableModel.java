package clientpackage;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public final class BuddyListTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private final List<String> fBuddyList;
	
	public BuddyListTableModel(List<String> aBuddyList) {
		this.fBuddyList = aBuddyList;
	}
	
		@Override
		public final Class<?> getColumnClass(int aColumn) {
			return getValueAt(0, aColumn).getClass();		
		}
		
		@Override
		public final Object getValueAt(int aRow , int aColumn) {
			return fBuddyList.get(aRow);
		}
		
		@Override
		public final int getRowCount() {		
			return fBuddyList.size();
		}
		
		@Override
		public final String getColumnName(int column) {
			return "Buddies";
		}
		
		@Override
		public final int getColumnCount() {
			return 1;
		}
		
		public final void setRow(String aName, boolean aOnline) {
			String editedName = "";
			int row = -1;
			if(aOnline) {
				row = fBuddyList.indexOf(aName);
				editedName = "<html><b> " + aName + " </b></html>";	
			} else {
				for(int index = 0; index < fBuddyList.size(); index++) {
         		    String name = fBuddyList.get(index);
         		   if(name.startsWith("<html><b>")) {
                       editedName = parseName(name);        		   
         		   }
         		   if(name.equals(aName)) {
         			   editedName = name;
         			   row = index;
         			   break;
         		   }
				}
			}			
			fBuddyList.set(row, editedName);
			fireTableRowsInserted(row, row);
		}
		
		public final String parseName(String aHtmlName) {
  		       final int indexHtml = aHtmlName.indexOf("</b></html>");					
  		       return  aHtmlName.substring(9, indexHtml).trim();
		}
		
		public final void addRow(String aName) {
			fBuddyList.add(aName);
			fireTableDataChanged();
		}
		
		public final void removeRow(int aRow) {
			fBuddyList.remove(aRow);
			fireTableRowsDeleted(aRow, aRow);
		}
		
		public final String getUsername(int aRow) {
			return fBuddyList.get(aRow);
		}
}
