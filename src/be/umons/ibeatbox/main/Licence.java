package be.umons.ibeatbox.main;

public abstract class Licence {
		
	protected String infoLicence;
	public  String getLicenceCCBY(){
		return "CC-BY";
	}
	public abstract String getLicence();
	public abstract void setLicence(String myLicence);
	public String toString(){
		return "Licences Creative Commons :\n"+
				getLicenceCCBY()+"-"+this.getLicence()+this.getInfoLicence();
	}
	public abstract void setInfoLicence(String info);
	public abstract String getInfoLicence();
}
