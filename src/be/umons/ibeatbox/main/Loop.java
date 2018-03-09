package be.umons.ibeatbox.main;

import java.util.ArrayList;

import android.view.View;

public class Loop {
	private String IS_VOICE ="voice";
	private String IS_SOUND ="Sound";
	private ArrayList<String> state;
	private ArrayList<Sound> sounds_list;
	private int voiceActive;

	public Loop(){
		state = new ArrayList<String>();
		sounds_list = new ArrayList<Sound>();
	}
	public int size(){
		return sounds_list.size();
	}
	public void setVoice(Sound voice) {
		this.state.add(IS_VOICE);
		this.sounds_list.add(voice);
	}
	public int posVoiceActive(){
		return voiceActive;
	}
	public void setPosVoiceActive(int pos){
		if(pos < size() && state.get(pos).equals(IS_VOICE))
			voiceActive = pos;
	}
	public boolean isVoice(int pos){
		if(pos < size())
			return state.get(pos).equals(IS_VOICE);
		else
			throw new RuntimeException("This positon  no exist");
	}
	public ArrayList<Sound> getSound() {
		return sounds_list;
	}

	public void setSound(Sound sound) {
		this.state.add(IS_SOUND);
		this.sounds_list.add(sound);
	}
	public Sound getSound(int position) {
		if(position < size())
		return this.sounds_list.get(position);
		else
			throw new RuntimeException("This positon  no exist");
	}
	public void removeSound(int position1) {
		this.sounds_list.remove(position1);
		this.state.remove(position1);
	}
	public void addSound(int position1,Sound sound) {
		this.sounds_list.remove(position1);
		this.sounds_list.add(position1, sound);
		this.state.remove(position1);
		this.state.add(position1, IS_SOUND);
	}
	public void stopPosVoiceRec(int pos) {
		if(pos < size() && state.get(pos).equals(IS_VOICE))
			voiceActive = -1;
		else
			throw new RuntimeException("This positon is no voice track or no exist");
	}
		
}