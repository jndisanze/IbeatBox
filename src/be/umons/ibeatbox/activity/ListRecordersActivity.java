package be.umons.ibeatbox.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import be.umons.ibeatbox.R;
import be.umons.ibeatbox.main.Sound;
import be.umons.ibeatbox.main.Tools;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
 
public class ListRecordersActivity extends Activity {
	private ListView maListViewPerso;
	private SimpleAdapter mSchedule;
	private final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
	private Tools tools;
	private ProgressDialog progress;
	@Override
	public void onBackPressed (){
		Intent intent = new Intent(ListRecordersActivity.this, MenuActivity.class);
		startActivity(intent);
	}
	@Override
	public void onPause (){
		if(progress != null && progress.isShowing())
			progress.dismiss();
		super.onPause();
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list_record);
        // variable application
        //Récupération de la listview créée dans le fichier main.xml
        maListViewPerso = (ListView) findViewById(R.id.listviewperso);
        tools = (Tools) getApplicationContext();
        //progress = ProgressDialog.show(ListRecordersActivity.this, getString(R.string.waiting), 
        	//	getString(R.string.create_music), true, false);
        //On déclare la HashMap qui contiendra les informations pour un item
        final HashMap<String, String> map;
 
        //Création d'une HashMap pour insérer les informations du premier item de notre listView
        map = new HashMap<String, String>();
        //on insère un élément titre que l'on récupérera dans le textView titre créé dans le fichier affichageitem.xml
        map.put("titre",getString(R.string.addtrack));
        //on insère un élément description que l'on récupérera dans le textView description créé dans le fichier affichageitem.xml
        map.put("description", "");
        map.put("UUID", "");
        //on insère la référence à l'image (convertit en String car normalement c'est un int) que l'on récupérera dans l'imageView créé dans le fichier affichageitem.xml
        map.put("img", String.valueOf(R.drawable.plus));
        //enfin on ajoute cette hashMap dans la arrayList
        listItem.add(map);
       
        //Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
         mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.affichageitem,
               new String[] {"img", "titre", "description"}, new int[] {R.id.img, R.id.titre, R.id.description});
         initCurrentRecordSound(); 
        //On attribut à notre listView l'adapter que l'on vient de créer
        maListViewPerso.setAdapter(mSchedule);
 
        //Enfin on met un écouteur d'évènement sur notre listView
        maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
         	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				if(position == 0){
						//creation nouveau piste
					     HashMap<String, String> map1 = new HashMap<String, String>();
				        map1.put("titre", "Track :"+"no name");
				        map1.put("description", "Click to edit ");
				        map1.put("img", String.valueOf(R.drawable.ic_music));
				        Sound s = new Sound();
				        tools.updateObservateur(s);
				        //tools.saveInCurrentRecordSound(s);
				        map1.put("UUID","");
				        listItem.add(map1);
				        mSchedule.notifyDataSetChanged();
				}
			else{       
				    
				HashMap<String, String> map = (HashMap<String, String>) maListViewPerso.getItemAtPosition(position);
				//on créer une boite de dialogue
				AlertDialog.Builder adb = new AlertDialog.Builder(ListRecordersActivity.this);
				//on attribut un titre à notre boite de dialogue
				adb.setTitle("Edit Pist");
				//on insère un message à notre boite de dialogue, et ici on affiche le titre de l'item cliqué
				adb.setMessage("Votre choix : "+map.get("titre"));
				//on indique que l'on veut le bouton ok à notre boite de dialogue
				
				adb.setPositiveButton("Record", new RecordOnClickListener(map.get("UUID")));
				adb.setNegativeButton("Remove",new RemoveOnClickListener(position));
				adb.setNeutralButton("Cancel",null);
				//on affiche la boite de dialogue
				adb.show();
				}
        	}
         });
        	
    }
private void initCurrentRecordSound() {
	HashMap<String,Sound> s = tools.getCurrentRecordSound();
	Iterator i = s.entrySet().iterator();
	System.out.println(i.hasNext());
		while(i.hasNext()){
			Map.Entry entry = (Map.Entry) i.next();
			String uid = (String) entry.getKey();
			Sound sound = (Sound) entry.getValue();
			final HashMap<String, String> item = new HashMap<String, String>();
			item.put("titre", "Track :"+sound.getName());
	        item.put("description", "Click to edit :"+uid);
	        item.put("img", String.valueOf(R.drawable.ic_music));
	        item.put("UUID",uid);
	        listItem.add(item);
	        mSchedule.notifyDataSetChanged();
		}
	}
//enegistement son
private final class RecordOnClickListener implements
  DialogInterface.OnClickListener {
  private String uid; 
public void onClick(DialogInterface dialog, int which) {
	 tools.setSoundCurrentUse(uid);
	Intent intent = new Intent(ListRecordersActivity.this, SoundRecordActivity.class);
	startActivity(intent);
  }
  public RecordOnClickListener(String uid){
	  this.uid=uid;
  }
}
// effacer une piste
private final class RemoveOnClickListener implements
    DialogInterface.OnClickListener {
	private int pos;
	public void onClick(DialogInterface dialog, int which) {
	   HashMap<String,String> h =listItem.get(pos);
	   tools.deleteInObservateur(h.get("UUID"));
	   listItem.remove(pos);
		mSchedule.notifyDataSetChanged();
		}
	public RemoveOnClickListener(int pos){
	  this.pos = pos;
	}
}
}
