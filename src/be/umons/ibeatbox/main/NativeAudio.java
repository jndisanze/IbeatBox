package be.umons.ibeatbox.main;



import java.io.File;

import android.content.Context;
import android.content.res.AssetManager;


public class NativeAudio implements SoundObserveur{
static final int CLIP_NONE = 0;
static final int CLIP_HELLO = 1;
static final int CLIP_ANDROID = 2;
static final int CLIP_SAWTOOTH = 3;
static final int CLIP_PLAYBACK = 4;
static String URI;
static AssetManager assetManager;
static boolean isPlayingAsset = false;
static boolean isPlayingUri = false;
static int numChannelsUri = 0;
private Tools tools;
private Context context;

	public NativeAudio(Context context){
		this.context = context;
		createEngine();
        createBufferQueueAudioPlayer();
        createAudioRecorder();
        System.out.println("create audio");
	}
	 public void onPause(){
	        // turn off all audio
	        selectClip(CLIP_NONE, 0);
	        isPlayingAsset = false;
	        setPlayingAssetAudioPlayer(false);
	        isPlayingUri = false;
	        setPlayingUriAudioPlayer(false);
	    }
	 protected void onDestroy(){
	        shutdown();
	    }
	 public void startRecord(){
		 startRecording();
	 }
	 public void playback(){
		 selectClip(4,1);
	 }
	 public void playback(String uid){ 
		 //selectClip(4,1);
		 playSound(context.getFilesDir().toString()+"/"+uid);
	 }
    /** Native methods, implemented in jni folder */
    public static native void createEngine();
    public static native short[] myRecordBuffer();
    public static native int SoundTraitemnt();
    public static native void createBufferQueueAudioPlayer();
    public static native boolean createAssetAudioPlayer(AssetManager assetManager, String filename);
    // true == PLAYING, false == PAUSED
    public static native void setPlayingAssetAudioPlayer(boolean isPlaying);
    public static native boolean createUriAudioPlayer(String uri);
    public static native void setPlayingUriAudioPlayer(boolean isPlaying);
    public static native void setLoopingUriAudioPlayer(boolean isLooping);
    public static native void setChannelMuteUriAudioPlayer(int chan, boolean mute);
    public static native void setChannelSoloUriAudioPlayer(int chan, boolean solo);
    public static native int getNumChannelsUriAudioPlayer();
    public static native void setVolumeUriAudioPlayer(int millibel);
    public static native void setMuteUriAudioPlayer(boolean mute);
    public static native void enableStereoPositionUriAudioPlayer(boolean enable);
    public static native void setStereoPositionUriAudioPlayer(int permille);
    public static native boolean selectClip(int which, int count);
    public static native boolean enableReverb(boolean enabled);
    public static native boolean createAudioRecorder();
    public static native void startRecording();
    public static native void shutdown();
    public static native int saveRecordsFile(String uid);
    public static native int saveLoopFile(String uid);
    public static native int getRecordsFile(String uid);
    public static native int deleteSound(String uid);
    public static native void setBMP(int bmp);
    public static native int getBMP();
    public static native int getBarvalue();
    public static native int getBeatvalue();
    public static native void setTimeSignatureNum(int sign);
    public static native void setTimeSignatureDen(int sign);
    public static native int getTimeSignatureNum();
    public static native int getTimeSignatureDen();
    public static native void setMinute(int min);
    public static native int getMinute();
    public static native int getMesure();
    public static native void setMesure(int mesure);
    public static native int saveCurrentSound(String dbName,String soundName);
    public static native int setCurrentSound(String dbName,String soundName);
    public static native void startLoop(); 
    public static native void stopLoop();
    public static native int setSoundStateActive(String uid,boolean isActive);
    public static native int setSoundStateLoop(String uid,boolean isLoop);
    public static native int setSoundStateRec(String uid,boolean isRec);
    public static native void setLoopUIDSound(String uid);
    public static native void setVoiceLoopUIDSound(String uid);
    public static native void eraseSoundLoope(String uid);
    public static native void eraseSoundVoiceLoope(String uid);
    public static native void exitApp();
    public static native void playSound(String uid);
    public static native void stopRecord();
    public static native void setInstrument(int number);
    public static native void startLoopVoice(String uid);
    //public static native int saveLoopFile(String name);
    /** Load jni .so on initialization */
    static {
    	//System.loadLibrary("stlport_shared");
     //System.loadLibrary("stk");
     System.loadLibrary("native-audio-jni");
    }
	public void update(Sound sound) {
		File f = context.getFilesDir();
		this.saveRecordsFile(f.toString()+"/"+sound.getId());
	}
	public void detele(String uid) { 
		File f = context.getFilesDir();
		 // delete via cpp had problem
    	//deleteSound(f.toString()+"/"+uid);
    	try{
    		File file = new File(f.toString()+"/"+uid);
    		if(file.delete()){ 
    			System.out.println(file.getName() + " is deleted!");
    		}else{
    			System.out.println("Delete operation is failed.");
    		}
    	}catch(Exception e){
    		e.printStackTrace();}
	}
	public void chargingSound(String uid){
		File f = context.getFilesDir();
		this.getRecordsFile(f.toString()+"/"+uid);
	}
	public static native void stopMusic();
	public void updateScore(String uid, Double score) {
		// TODO Auto-generated method stub
		
	}
}
