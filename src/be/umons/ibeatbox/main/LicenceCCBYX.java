package be.umons.ibeatbox.main;

import java.util.ArrayList;

public class LicenceCCBYX extends Licence {
    
	private String myLicence;
	private ArrayList<Licence> combineLicence = new ArrayList<Licence>(); 
	public LicenceCCBYX(String lic,String info) {
		myLicence = lic;
		this.infoLicence = info;
	}
	@Override
	public void setLicence(String myLicence) {
		this.myLicence = myLicence;
	}
	public void setCombineLicence(Licence myLicence) {
		this.combineLicence.add(myLicence);
	}
	@Override
	public String getLicence() {
		String res =this.myLicence; 
		for(Licence d : combineLicence){
			res += d.getLicence();
		}
		return res;
	}

	@Override
	public void setInfoLicence(String info) {
		this.infoLicence = info;
	}
	@Override
	public String getInfoLicence() {
		return this.infoLicence;
	}
}
