package clientpackage;

public final class SourceRequestingModel {
	private String fName;
	private boolean fAccept;
	private boolean fDecline;
	
	public SourceRequestingModel(String aName, boolean aAccept, boolean aDecline) {
		this.fName = aName;
		this.fAccept = aAccept;
		this.fDecline = aDecline;	    
	}

		public final void setName(String aName) {
			this.fName = aName;
		}
		
		public final String getName() {
		    return this.fName;
		}
		
		public final boolean isAccepted() {
		    return this.fAccept;
		}
	
		public final void setAccept(boolean aAccept) {
		    this.fAccept = aAccept;
		}
	
		public final boolean isDeclined() {
		    return this.fDecline;
		}
	
		public final void setDecline(boolean aDecline) {
		    this.fDecline = aDecline;
		}	
}