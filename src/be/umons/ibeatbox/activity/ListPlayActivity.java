package be.umons.ibeatbox.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import be.umons.ibeatbox.R;
import be.umons.ibeatbox.main.Sound;
import be.umons.ibeatbox.main.Tools;

public class ListPlayActivity extends Activity {
	private ListView maListViewPerso;
	private SimpleAdapter mSchedule;
	private final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
	private Tools tools;
	private ProgressDialog progress;
	private ArrayList<String> loopSound = new ArrayList<String>();
	private ArrayList<String> id_loopSound = new ArrayList<String>();
	private ArrayList<Sound> soundsAv = new ArrayList<Sound>();
	private ListPlayActivity mContext;
	@Override
	public void onBackPressed (){
		Intent intent = new Intent(ListPlayActivity.this, MenuActivity.class);
		startActivity(intent);
	}
	@Override
	public void onPause (){
		if(progress != null && progress.isShowing())
			progress.dismiss();
		super.onPause();
	}
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.main_list_record);
        maListViewPerso = (ListView) findViewById(R.id.listviewperso);
        tools = (Tools) getApplicationContext();
        initPlaySound(); 
     }
	private void initPlaySound(){
		ArrayList<Bitmap> arr_bitmaps = new ArrayList<Bitmap>();
		HashMap<String,Sound> hp =tools.getSoundInDatabase().getSounds();
		Iterator i = hp.entrySet().iterator();
		while(i.hasNext()){
		Map.Entry entry = (Map.Entry) i.next();
		Sound sound = (Sound) entry.getValue();
		soundsAv.add(sound);
        arr_bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.ic_music));
        loopSound.add(sound.getName());
		id_loopSound.add(sound.getId());
		}
		LoopAdapter myadapter = new LoopAdapter(ListPlayActivity.this, arr_bitmaps, loopSound);
        this.maListViewPerso.setAdapter(myadapter);
	}
	class LoopAdapter extends BaseAdapter
    {
    
    	public String title[];
    	public String description[];
    	ArrayList<String> arr_calllog_name = new ArrayList<String>();
    	public Activity context;
    	ArrayList<Bitmap> imageId; 

    	public LayoutInflater inflater;
    	private boolean isPlay;
    	public LoopAdapter(Activity context, ArrayList<Bitmap> arr_bitmaps, ArrayList<String> arr_calllog_name) {
    		super();

    		this.imageId = arr_bitmaps;
    		this.context = context;
    		this.arr_calllog_name = arr_calllog_name;
    	
    	    this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	     isPlay = false;
    	}
    	
    	public ArrayList<Bitmap> getImageId() {
    	    return imageId;
    	}
    	public void setImageId(ArrayList<Bitmap> imageId) {
    	    this.imageId = imageId;
    	}
    	public int getCount() {
    		return arr_calllog_name.size();
    	}
    	public Object getItem(int position) {
    		// TODO Auto-generated method stub
    		return null;
    	}

    	public long getItemId(int position) {
    		// TODO Auto-generated method stub
    		return 0;
    	}
    	public class ViewHolder
    	{
    		ImageView image;
    		TextView txtName;
    		TextView txtDescr;
    		CheckBox btnPlayPaus;
    		Button setting;
    	}
    	public View getView(final int position, View convertView, ViewGroup parent) {
    		// TODO Auto-generated method stub
    		final ViewHolder holder;
    		if(convertView==null)
    		{
    			holder = new ViewHolder();
    			convertView = inflater.inflate(R.layout.affichageitem, null);

    			holder.image = (ImageView) convertView.findViewById(R.id.img);
    			holder.txtName = (TextView) convertView.findViewById(R.id.titre);
    			holder.txtDescr = (TextView) convertView.findViewById(R.id.description);
    			holder.btnPlayPaus = (CheckBox) convertView.findViewById(R.id.listPlayPause);
    			holder.setting = (Button) convertView.findViewById(R.id.setting);
    			convertView.setTag(holder);
    		}
    		else{
   
    			holder=(ViewHolder)convertView.getTag();
    		}
    		holder.image.setImageBitmap(getImageId().get(position));
    		holder.txtName.setText(arr_calllog_name.get(position));
    		holder.txtDescr.setText(position+".Tag:"+soundsAv.get(position).getTAG_SOUND());
    		
    		holder. btnPlayPaus.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String id = id_loopSound.get(position);
					if(holder.btnPlayPaus.isChecked()){
						//tools.getNativeAudio().playSound(id);
						tools.getNativeAudio().chargingSound(id);
						tools.getNativeAudio().playback(id);
						Toast.makeText(getApplicationContext(), "Playing "+ position, Toast.LENGTH_SHORT).show();
					}
					else{
						//tools.getNativeAudio().stopMusic();
						Toast.makeText(getApplicationContext(), "Pause "+ position, Toast.LENGTH_SHORT).show();
					
					}
				}
			});
    		holder.setting.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String id = id_loopSound.get(position);
					showDialogPlay(id);
				 	//Toast.makeText(getApplicationContext(), "You have deleted row No. "+ position, Toast.LENGTH_SHORT).show();*/
				}
			});
    		return convertView;
    	}
	}
private void showDialogPlay(String uid){
	final String uid_local = uid;
	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

		// set title
		alertDialogBuilder.setTitle(getString(R.string.setting));

		// set dialog message
		alertDialogBuilder
			.setMessage(getString(R.string.waiting))
			.setCancelable(false)
			.setPositiveButton("Play",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					tools.getSoundInDatabase().setCurrentSoundUseId(uid_local);
					Intent intent = new Intent(ListPlayActivity.this, PlayActivity.class);
					startActivity(intent);
				}
			  })
			.setNegativeButton("Remove",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					//tools.getSoundInDatabase().deleteSound(uid_local);
					//tools.getNativeAudio().detele(uid_local);
					tools.deleteInObservateur(uid_local);
					Intent intent = new Intent(ListPlayActivity.this,ListPlayActivity.class);
					startActivity(intent);
				}
			})
			.setNeutralButton(getString(R.string.cancel_task),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
				}
			}
			);

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		}
		
}
