package clientpackage;

public final class DownloadFileModel {
    private boolean fIsChecked;
    private String fName;
    private String fSizeConverted;
    private Long fSizeActual;
    private Float fStatus;
    
    public DownloadFileModel(String aName, String aSizeConverted, Long aSizeActual) {
    	this.fIsChecked = false;
    	this.fName = aName;
    	this.fSizeConverted = aSizeConverted;
    	this.fSizeActual = aSizeActual;
    	this.fStatus = 0f;
    }
    
	    public final void setSelect(boolean aSelected) {
	    	this.fIsChecked = aSelected;
	    }
	    
	    public final boolean isSelected() {
	    	return this.fIsChecked;
	    }
	    
	    public final String getName() {
	        return this.fName;	
	    }
	    
	    public final String getConvertedSize() {
	    	return this.fSizeConverted;
	    }
	    
	    public final Long getActualSize() {
	    	return this.fSizeActual;
	    }
	    
	    public final void setStatus(float aStatus) {
	    	this.fStatus = aStatus;
	    }
	    
	    public final float getStatus() {
	    	return this.fStatus;
	    }
}