package be.umons.ibeatbox.main;

import java.util.UUID;

public class Sound {
	private String name;
	private String id;
	private int bmp = 120;
	private int signatureRythmNum = 4;
	private int signatureRythmDen = 4;
	private GPS gps; 
	private String TAG_SOUND =TagInit.instruments[0];
	private Licence licence;
	private int mesure = 1;
	private int numberScore =0;
	private int time =0;
	public Sound(){
		this.setId(UUID.randomUUID().toString());
		this.name ="No name";
		licence = new CreateLicence().createLicenceCCBY();
	}
	public Sound(String id){
		this.setId(id);
		this.name ="No name";
		licence = new CreateLicence().createLicenceCCBY();
	}
	public Sound(String id,String name){
		this.setId(id);
		this.name =name;
	}
	public void setName(String name){
		this.name=name;
	}
	public String getName() {
		return this.name;
	}
	public void setGps(GPS gps){
		this.gps = gps;
	}
	public double getLat(){
		return gps.getLatitude();
	}
	public double getLong(){
		return gps.getLongitude();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getTempo() {
		return bmp;
	}
	public void setTempo(int bmp) {
		this.bmp = bmp;
	}
	public int getSignatureRythmNum() {
		return signatureRythmNum;
	}
	public void setSignatureRythmNum(int signatureRythmNum) {
		this.signatureRythmNum = signatureRythmNum;
	}
	public int getSignatureRythmDen() {
		return signatureRythmDen;
	}
	public void setSignatureRythmDen(int signatureRythmDen) {
		this.signatureRythmDen = signatureRythmDen;
	}
	public String getTAG_SOUND() {
		return TAG_SOUND;
	}
	public void setTAG_SOUND(String tAG_SOUND) {
		TAG_SOUND = tAG_SOUND;
	}
	public void setMesure(int mesure) {
		this.mesure = mesure;
	}
	public int getMesure() {
		return this.mesure;
	}
	public void setRating(float rating) {
		this.numberScore += 1;
		this.rating +=rating;
	}
	public double getRating() {
		if(this.numberScore>0)
		return this.rating/this.numberScore;
		else
			return 0.;
	}
	public Licence getLicence() {
		return licence;
	}
	public void setLicence(Licence licence) {
		this.licence = licence;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public String toString(){
		return getId()+" "+getName()+" "+getMesure()+" "+getTAG_SOUND()+" "+0+" "+0+" "+
				getSignatureRythmNum()+" "+getSignatureRythmDen()+" "+getTempo()+" ";		
	}
	private double rating=0.0;
}