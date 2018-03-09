package be.umons.ibeatbox.main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class CreateLicence {
	public Licence createLicenceCCBY(){
		return new LicenceCCBYX("CC-BY","Creativecommons.org");
		
	}
	public Licence createLicenceCCBYSA(){
		return new LicenceCCBYX("CC-BY-SA","Creativecommons.org");
		
	}
	public Licence createLicenceCCBYNC(){
		return  new LicenceCCBYX("CC-BY-NC","Creativecommons.org");
		
	}
	public Licence createLicenceCCBYND(){
		return  new LicenceCCBYX("CC-BY-ND","Creativecommons.org");
		
	}
	public Licence createLicenceCCBYNCSA(){
		return  new LicenceCCBYX("CC-BY-NC-SA","Creativecommons.org");
		
	}
	public Licence createLicenceCCBYNCND(){
		return  new LicenceCCBYX("CC-BY-NC-ND","Creativecommons.org");
		
	}
}
