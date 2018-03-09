package be.umons.ibeatbox.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
* Cette classe définit l'utilisateur de l'application.
*
*/
public class User {
	public ProjectInfo _unnamed_ProjectInfo_;
	public Profile _unnamed_Profile_;
	public Tools _unnamed_Tools_;
	private String id;
	private HashMap<String,Sound> userSounds;
	private String name;
	private String password;
	private double longitude;
	private double latitude;
	private GPS gps;
	/**
	 * Constructeur.
	 * @param .
	 */
	public User() {
		this.name ="None";
	}
	/**
	 * Constructeur.
	 * @param username Le nom de l'utilisateur.
	 */
	public User(String username,String password,String id_mobile) {
		this.name = username;
		this.id= id_mobile;
		this.password = password;
	}
	/**
	 * Permet d'obtenir l'identifiant numérique de l'utilisateur.
	 * @return L'identifiant numérique de l'utilisateur.
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * Permet d'attribuer un identifiant numérique à l'utilisateur.
	 * @param id Un identifiant numérique.
	 */
	public void setId(String id){
		this.id = id;
	}
	
	/**
	 * Permet d'obtenir le nom de l'uilisateur.
	 * @return Le nom de l'utilisateur.
	 */
	public String getName() {
		return name;
	}
	/**
	 * Permet d'obtenir les chansons portant le nom soundname
	 * @param Sound_name Le nom de l'itinéraire.
	 * @return Un objet Sound.
	 * @see be.umons.ibeatbox.main.Sound
	 */
	public Sound getSound(String soundName) {
		return (Sound) userSounds.get(soundName);
	}
	
	/**
	 * Permet d'obtenir la liste de tous les chansons de l'utilisateur.
	 * @return La liste des chansons.
	 */
	public ArrayList<Sound> getAllSounds() {
		ArrayList<Sound> result = new ArrayList<Sound>();
		Iterator<String> set = userSounds.keySet().iterator();
		
		while(set.hasNext()) {
			Sound tmp = userSounds.get(set.next());
			result.add(tmp);
		}
		return result;
	}
	
	/**
	 * Permet d'ajouter un itinéraire à l'ensemble des itinéraires créés par l'utilisateur.
	 * @param Sound Une chanson.
	 * @see be.umons.ibeatbox.main.Sound
	 */
	public void addSound(Sound sound) {
		String key = sound.getName();
		userSounds.put(key, sound);
	}
	
	/**
	 * Permet de supprimer l'itinéraire Sound_name de l'ensemble des itinéraires créés par l'utilisateur.
	 * @param Sound_name Le nom de l'itinéraire à supprimer.
	 */
	public void removeSound(String soundName) {
		userSounds.remove(soundName);
	}
	
	/**
	 * Permet d'attribuer l'ensemble des itinéraires créés par l'utilisateur.
	 * @param Sounds Un ensemble d'itinéraires.
	 * @see java.util.HashMap
	 * @see MCGProject.umons.main.Sound
	 */
	public void setSounds(HashMap<String,Sound> sounds) {
		this.userSounds = sounds;
	}
	
	/**
	 * Permet de savoir si l'utilisateur possède un itinéraire ayant pour nom Sound_name.
	 * @param Sound_name Un nom d'itinéraire.
	 * @return True si cet itinéraire existe, False sinon.
	 */
	public boolean hasSound(String soundName) {
		return userSounds.containsKey(soundName);
	}
	/**
	 * Permet d'obtenir la note que l'utilisateur a attribué au POI poi_name.
	 * @param poi_name Le nom du POI.
	 * @return La note attribuée au POI.
	 */
	public int getScore(int soundId) {
		return 0; //userSounds.get(soundId)getScore(soundId);
	}
	
	/**
	 * Permet d'attribuer une note au POI poi_name. Si le POI a déjà été noté, l'ancienne note
	 * est remplacée par la nouvelle.
	 * @param poi_id L'id du POI noté.
	 * @param score La note attribuée au POI.
	 */
	public void addScore(int poi_id, int score) {
		//user_score.put(poi_id, score);
	}
	/**
	 * Permet d'attribuer une note au POI poi_name. Si le POI a déjà été noté, l'ancienne note
	 * est remplacée par la nouvelle.
	 * @param poi_id L'id du POI noté.
	 * @param score La note attribuée au POI.
	 */
	public void updateScore(int poi_id, int score) {
		//user_score.put(poi_id, score);
		//TODO Supprimer car redondante ?
	}
	public void setGPS(GPS gps) {
		this.gps=gps;
	}
	public double getLong() {
		return this.gps.getLongitude();
	}
	public double getLat() {
		return this.gps.getLatitude();
	}
	public String getPassword() {
		return this.password;
	}
	
}
