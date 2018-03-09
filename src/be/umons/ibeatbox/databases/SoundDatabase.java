package be.umons.ibeatbox.databases;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import be.umons.ibeatbox.main.Licence;
import be.umons.ibeatbox.main.LicenceCCBYX;
import be.umons.ibeatbox.main.Sound;
import be.umons.ibeatbox.main.SoundObserveur;

public class SoundDatabase implements SoundObserveur{
	
	//Database and helper
private SoundDatabaseHelper dbHelper;
private SQLiteDatabase db;
private String linkDatabase;
private HashMap<String,Sound> sounds = new HashMap<String,Sound>();
//Database file and tables
private static final String DBNAME = "sound_ibeatbox.db";
private static final String SOUND_TABLE = "sound_table";
private static final String SOUND_DATA_TABLE = "sound_data";
private static final String SOUND_CONF_SIGN_TABLE = "conf_sound_table";
private static final String SOUND_LICENCE_TABLE="licence_table";
private static final String SOUND_SCORE_TABLE = "score_table";
private static final String SOUND_TIME_TABLE = "time_table";
//Tables and columns
private static final String COL_ID = "ID";
private static final String COL_SOUND = "SOUND";
private static final String COL_TAGS = "TAGS";
private static final String COL_LONG ="LONGI";
private static final String COL_LAT ="LAT";
private static final String COL_TEMPO ="TEMPO";
private static final String COL_SIGNNUM ="SIGNNUM";
private static final String COL_SIGNDEN ="SIGNDEN";
private static final String COL_BNS = "BNS";
private static final String COL_DATA = "DATA";
private static final String COL_LICENCE = "LICENCE";
private static final String COL_SCORE ="SCORE";
private static final String COL_TIME ="TIME";
//Create commands
private static final String CREATE_SOUND = "CREATE TABLE " + SOUND_TABLE+ " ("
		+ COL_ID + " VARCHAR(20) NOT NULL, " + COL_SOUND + " VARCHAR(20) NOT NULL, " 
		 +COL_LONG + " FLOAT, "+COL_LAT + " FLOAT, "+ COL_TAGS + " TEXT NOT NULL);";

private static final String CREATE_CONF_SOUND = "CREATE TABLE " + SOUND_CONF_SIGN_TABLE+ " ("
		+ COL_ID + " VARCHAR(20) NOT NULL, "+COL_TEMPO + " INTEGER ,"+
		COL_BNS + " INTEGER ," +COL_SIGNNUM + " INTEGER ,"+COL_SIGNDEN + " INTEGER); "; 

private static final String CREATE_SOUND_DATA = "CREATE TABLE " + SOUND_DATA_TABLE+ " ("
		+ COL_ID + " VARCHAR(20) NOT NULL, "+COL_DATA + " BLOB);"; 

private static final String CREATE_SOUND_TIME = "CREATE TABLE " + SOUND_TIME_TABLE+ " ("
		+ COL_ID + " VARCHAR(20) NOT NULL, "+COL_TIME + " INTEGER);"; 

private static final String CREATE_SOUND_LICENCE = "CREATE TABLE " + SOUND_LICENCE_TABLE+ " ("
		+ COL_ID + " VARCHAR(20) NOT NULL, "+COL_LICENCE + " VARCHAR(20) NOT NULL);"; 

private static final String CREATE_SOUND_SCORE = "CREATE TABLE " + SOUND_SCORE_TABLE+ " ("
		+ COL_ID + " VARCHAR(20) NOT NULL, "+COL_SCORE + " VARCHAR(20) NOT NULL);"; 

private String currentSoundUseId; 
public SoundDatabase(Context context){
	dbHelper = new SoundDatabaseHelper(context, DBNAME, null,2);
	open();
	this.setALLSounds();
	this.linkDatabase =context.getFilesDir().toString()+"/"+DBNAME;
	}
/**
 * Permet d'ouvrir la lecture/écriture de la base de données.
 */
public void open(){
	db = dbHelper.getWritableDatabase();
}		
/**
 * Permet de fermer la base de données.
 */
public void close(){
	db.close();
}
/**
 * Permet de vérifier l'existence d'un son dans la base de données.
 * @param id du son dont on veut connaître l'existence.
 * @return Vrai si le sont existe, faux sinon. 
 */
public boolean hasSound(String idsound){
	String format = "SELECT %s FROM %s WHERE %s = \"%s\";";
	String query = String.format(format, COL_SOUND, SOUND_TABLE, COL_ID, idsound);
	Cursor c = db.rawQuery(query, null);
	int count = c.getCount();
	c.close();
	return count == 1;
}
private void setALLSounds(){
	//this.deleteSound("78cffa0a-78a4-46c7-8855-49bacfb2e60d");
	String format1,query1,query2,query3;
	String format = "SELECT %s, %s, %s FROM %s;";
	String query = String.format(format,COL_ID,COL_SOUND,COL_TAGS, SOUND_TABLE);
	Cursor c = db.rawQuery(query, null);
	if(c.moveToFirst()){
	while(!c.isAfterLast()){
		String uid = c.getString(0);
		String name = c.getString(1);
		System.out.println("DB LOADING: "+uid+" "+name);
		format1 = "SELECT %s, %s, %s, %s FROM %s WHERE %s = \"%s\";";
		query1 = String.format(format1, COL_TEMPO,COL_BNS,COL_SIGNNUM,
				COL_SIGNDEN,SOUND_CONF_SIGN_TABLE, COL_ID,uid);
		Cursor c1 = db.rawQuery(query1, null);
		c1.moveToFirst();
		Sound s = new Sound(uid,name);
		s.setTempo(c1.getInt(1));
		System.out.println("1");
		s.setMesure(c1.getInt(0));
		System.out.println("0");
		s.setSignatureRythmNum(c1.getInt(2));
		System.out.println("2");
		s.setSignatureRythmDen(c1.getInt(3));
		System.out.println("3");
		c1.close();
		String format2= "SELECT %s FROM %s  WHERE %s = \"%s\";";
		query2=String.format(format2,COL_TIME,SOUND_TIME_TABLE,COL_ID,uid);
		Cursor c2 = db.rawQuery(query2, null);
		System.out.println("time  "+c2.getColumnCount());
		c2.moveToFirst();
		s.setTime(c2.getInt(0));
		System.out.println("time");
		c2.close();
		System.out.println("avt licence");
		String format3= "SELECT %s FROM %s  WHERE %s = \"%s\";";
		query2=String.format(format3,COL_LICENCE,SOUND_LICENCE_TABLE,COL_ID,uid);
		Cursor c3 = db.rawQuery(query2, null);
		c3.moveToFirst();
		s.setLicence(new LicenceCCBYX(c3.getString(0),"Creativecommons.org"));
		System.out.println("licence");
		c3.close();
		String format4= "SELECT %s FROM %s  WHERE %s = \"%s\";";
		query1=String.format(format4,COL_SCORE,SOUND_SCORE_TABLE,COL_ID,uid);
		Cursor c4 = db.rawQuery(query1, null);
		c4.moveToFirst();
		s.setRating(c4.getFloat(0));
		System.out.println("score");
		c4.close();
		sounds.put(uid,s);
		c.moveToNext();
	} 
	c.close();
	}
}
public void deleteSound(String uid){
	String format =  "%s = \"%s\";";
	String whereClause = String.format(format, COL_ID, uid);
	db.delete(SOUND_TABLE, whereClause, null);
	db.delete(SOUND_CONF_SIGN_TABLE, whereClause, null);
	db.delete(SOUND_DATA_TABLE, whereClause, null);
	db.delete(SOUND_LICENCE_TABLE, whereClause, null);
	db.delete(SOUND_SCORE_TABLE, whereClause, null);
	db.delete(SOUND_TIME_TABLE, whereClause, null);
	this.sounds.remove(uid);
}
public void updateScore(String id,Double score) {
	String format =  "%s = \"%s\";";
	String whereClause = String.format(format, COL_ID,id);
	ContentValues values = new ContentValues();
	values.put(COL_SCORE, score);
	db.update(SOUND_SCORE_TABLE, values,whereClause, null);
}
public void addSound(Sound s){
	deleteSound(s.getId());
	ContentValues values = new ContentValues();
	values.put(COL_ID,s.getId());
	values.put(COL_SOUND,s.getName());
	values.put(COL_LONG,0.0);
	values.put(COL_LAT,0.0);
	values.put(COL_TAGS,s.getTAG_SOUND());
	db.insert(SOUND_TABLE,null, values);
	ContentValues values1 = new ContentValues();
	values1.put(COL_ID,s.getId());
	values1.put(COL_TEMPO,s.getTempo());
	values1.put(COL_BNS,s.getMesure());
	values1.put(COL_SIGNNUM,s.getSignatureRythmNum());
	values.put(COL_SIGNDEN,s.getSignatureRythmDen());
	db.insert(SOUND_CONF_SIGN_TABLE,null, values1);
	ContentValues values2 = new ContentValues();
	values2.put(COL_ID,s.getId());
	values2.put(COL_LICENCE,s.getLicence().getLicence());
	db.insert(SOUND_LICENCE_TABLE,null, values2);
	ContentValues values3 = new ContentValues();
	values3.put(COL_ID,s.getId());
	values3.put(COL_SCORE,s.getRating());
	db.insert(SOUND_SCORE_TABLE,null, values3);
	ContentValues values4 = new ContentValues();
	System.out.println("avt time"+" "+values.toString());
	values4.put(COL_ID,s.getId());
	values4.put(COL_TIME,s.getTime());
	db.insert(SOUND_TIME_TABLE,null, values4);
	System.out.println("time"+" "+values.toString());
	this.sounds.put(s.getId(),s);
}
public HashMap<String,Sound> getSounds(){
	return this.sounds;
}
public String getLinkDatabase() {
	return linkDatabase;
}
public void setLinkDatabase(String linkDatabase) {
	this.linkDatabase = linkDatabase;
}
public Sound getCurrentSoundUseId() {
	return this.sounds.get(currentSoundUseId);
}
public void setCurrentSoundUseId(String currentSoundUseId) {
	this.currentSoundUseId = currentSoundUseId;
}
private class SoundDatabaseHelper extends SQLiteOpenHelper {

	/**
	 * Constructeur.
	 * @see android.database.sqlite.SQLiteOpenHelper#SQLiteOpenHelper(Context, String, CursorFactory, int)
	 */
	public SoundDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	/**
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(SQLiteDatabase)
	 */
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SOUND);
		db.execSQL(CREATE_CONF_SOUND);
		db.execSQL(CREATE_SOUND_DATA);
		db.execSQL(CREATE_SOUND_LICENCE);
		db.execSQL(CREATE_SOUND_SCORE);
		db.execSQL(CREATE_SOUND_TIME);
	}
	/**
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(SQLiteDatabase, int, int)
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("DB", "UPDATED");
		db.execSQL("DROP TABLE IF EXISTS " + SOUND_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SOUND_DATA_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SOUND_CONF_SIGN_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SOUND_LICENCE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SOUND_SCORE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SOUND_TIME_TABLE);
	    onCreate(db);
		}
	}
public void update(Sound sound) {
	this.addSound(sound);
	this.setCurrentSoundUseId(sound.getId());
}
public void detele(String uid) {
	this.deleteSound(uid);	
}	
}
