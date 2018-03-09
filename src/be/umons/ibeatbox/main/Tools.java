package be.umons.ibeatbox.main;
import java.util.ArrayList;
import java.util.HashMap;

import be.umons.ibeatbox.databases.ForumDatabase;
import be.umons.ibeatbox.databases.SoundDatabase;
import be.umons.ibeatbox.databases.SoundDatabaseDistance;
import be.umons.ibeatbox.databases.UserDatabase;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.app.Application;
public class Tools extends Application implements SoundObservable{
	private UserDatabase userDatabase;
	private SoundDatabase soundInDatabase;
	public Sound soundCurrentUse;
	private String linkDatabase;
	private GPS gps;
	private NativeAudio nativeAudio;
	////////////////Loop part ////////////////////
	//public ArrayList<String> loopSound = new ArrayList<String>();
	//public  ArrayList<String> id_loopSound = new ArrayList<String>();
	//public ArrayList<Sound> soundsAviable = new ArrayList<Sound>();
	//////////////////////////////////////////////////
	public HashMap<String,Sound> currentSound;
	private static final String strUrl ="http://10.104.36.61/ibeatbox/soundDatabase.php";
	public HashMap<String,Sound> currentLoopSound;
	private ArrayList<SoundObserveur> listObservateur = new ArrayList<SoundObserveur>();
	private Licence defaultLicence;
	public Loop loop;
	private boolean saveInInternetDatabase=false;
	public SoundDatabaseDistance soundDatabaseDistance;
	public int currentpostionloopChoose;
	public int mesure=1;
	public int signatureRythmDen=4;
	public int bpm_tempo=120;
	public int signatureRythmNum=4;
	public Sound currentSoundTemp;
	private String uidMobile;
	private ForumDatabase forumDatabase;
	public void onCreate(){
		
		this.initialize(getApplicationContext());
	}
	public void initialize(Context context) {
		//userDatabase = new UserDatabase(context);
		setSoundDatabase(new SoundDatabase(context));
		this.listObservateur.add(getSoundInDatabase());
		nativeAudio = new NativeAudio(context);
		this.listObservateur.add(nativeAudio);
		currentSound = new HashMap<String,Sound>();
		currentLoopSound = new HashMap<String,Sound>();
		defaultLicence = new CreateLicence().createLicenceCCBY();
		soundDatabaseDistance = new SoundDatabaseDistance(context,strUrl);
		loop = new Loop();
		//setForumDatabase(new ForumDatabase());
		TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		setUidMobile(tManager.getDeviceId());
	}
	public void setGPS(GPS gps) {
		this.setGps(gps);
		
	}
	public UserDatabase getUserDatabase() {
		return userDatabase;
	}
	public GPS getGps() {
		return gps;
	}
	public void setGps(GPS gps) {
		this.gps = gps;
	}
	public SoundDatabase getSoundInDatabase() {
		return soundInDatabase;
	}
	public void setSoundDatabase(SoundDatabase soundDatabase) {
		this.soundInDatabase = soundDatabase;
	}
	public HashMap<String,Sound> getCurrentRecordSound() {
		return currentSound;
	}
	public void saveDatabaseSoundCurrentUse(){
		if(this.soundCurrentUse != null)
			this.saveSound(soundCurrentUse);
	}
	public void saveSound(Sound sound){
		this.currentSound.put(sound.getId(),sound);
		this.soundInDatabase.addSound(sound);
	}
	public  void deleteSound(String uid_sound){
		this.soundInDatabase.deleteSound(uid_sound);
		this.currentSound.remove(uid_sound);
	}
	public Sound getSoundCurrentUse() {
		return soundCurrentUse;
	}
	public void setSoundCurrentUse(String uid) {
		this.soundCurrentUse = this.currentSound.get(uid);
	}
	public NativeAudio getNativeAudio() {
		return nativeAudio;
	}
	public void setNativeAudio(NativeAudio nativeAudio) {
		this.nativeAudio = nativeAudio;
	}
	public String getLinkDatabase() {
		return linkDatabase;
	}
	public void setLinkDatabase(String linkDatabase) {
		this.linkDatabase = linkDatabase;
	}
	public void addObservateur(SoundObserveur obs) {
		this.listObservateur.add(obs);
	}
	public void updateObservateur(Sound s) {
		for(SoundObserveur obs : this.listObservateur){
		      obs.update(s);
		}
		if(this.isSaveInInternetDatabase()){
			System.out.println("save on line");
			this.soundDatabaseDistance.update(s);
			this.setSaveInInternetDatabase(false);
		}
		this.currentSound.put(s.getId(),s);
	}
	public void delObservateur() {
		 this.listObservateur = new ArrayList<SoundObserveur>();
		
	}
	public void deleteInObservateur(String uid) {
		for(SoundObserveur obs : this.listObservateur)
		      obs.detele(uid);
		this.currentSound.remove(uid);
	}
	public Licence getLicence() {
		return defaultLicence;
	}
	public void setLicence(Licence defaulLicence) {
		this.defaultLicence = defaulLicence;
	}
	public boolean isSaveInInternetDatabase() {
		return saveInInternetDatabase;
	}
	public void setSaveInInternetDatabase(boolean saveInInternetDatabase) {
		this.saveInInternetDatabase = saveInInternetDatabase;
	}
	public Sound getDefauldSound(){
		Sound s = new Sound();
		s.setLicence(defaultLicence);
		s.setMesure(mesure);
		s.setSignatureRythmDen(signatureRythmDen);
		s.setSignatureRythmNum(signatureRythmNum);
		s.setTempo(bpm_tempo);
		this.nativeAudio.setInstrument(0);	
		return s;
	}
	public void updateScoreObservateur(String uid,Double score) {
		for(SoundObserveur obs : this.listObservateur)
		      obs.updateScore(uid,score);
		
	}
	public String getUidMobile() {
		return uidMobile;
	}
	public void setUidMobile(String uidMobile) {
		this.uidMobile = uidMobile;
	}
	public ForumDatabase getForumDatabase() {
		return forumDatabase;
	}
	public void setForumDatabase(ForumDatabase forumDatabase) {
		this.forumDatabase = forumDatabase;
	}
	public static String getStrurl() {
		return strUrl;
	}
}