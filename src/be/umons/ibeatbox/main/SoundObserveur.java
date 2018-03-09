package be.umons.ibeatbox.main;

public interface SoundObserveur {
	public void update(Sound sound);
	public void detele(String uid);
	public void updateScore(String uid,Double score);
}