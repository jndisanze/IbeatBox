package be.umons.ibeatbox.main;

public interface SoundObservable {
	 public void addObservateur(SoundObserveur obs);
	  public void updateObservateur(Sound s);
	  public void updateScoreObservateur(String uid,Double score);
	  public void deleteInObservateur(String uid);
	  public void delObservateur();	
}
