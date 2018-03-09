package be.umons.ibeatbox.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
import be.umons.ibeatbox.R;
import be.umons.ibeatbox.main.CreateLicence;
import be.umons.ibeatbox.main.NativeAudio;
import be.umons.ibeatbox.main.Sound;
import be.umons.ibeatbox.main.Tools;

public class MenuActivity extends Activity{
	private NativeAudio myAudio;
	private Tools tools;
	@Override
	public void onBackPressed (){
		
	}
	@Override
	public void onPause (){
		if(progress != null && progress.isShowing())
			progress.dismiss();
		super.onPause();
	}
	private ProgressDialog progress;
	/**@Override
	public void onDestroy(){
		myAudio.exitApp();
	}*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    
	    tools = (Tools) getApplicationContext();
	    myAudio = tools.getNativeAudio();
        //myAudio.startRecord();
        //CreateLicence.copyPasteFile(source, "/data/app-lib/be.umons.ibeatbox-1");
	    GridView gridview = (GridView) findViewById(R.id.gridview); 
	    gridview.setAdapter(new MenuImageAdapter(this));
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	String markText = "";
	        	if(position == 0){
	        		markText = getString(R.string.music);
	        		Toast.makeText(MenuActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		//Intent intent = new Intent(MenuActivity.this, ListRecordersActivity.class);
	        		// recommand by stephane dupont
			        
			        //tools.updateObservateur(tools.getDefauldSound());
	        		Sound s = tools.getDefauldSound();
	        		tools.currentSoundTemp = s;
	        		Intent intent = new Intent(MenuActivity.this, SoundRecordActivity.class);
					startActivity(intent);
	        	}
	        	else if(position == 1){
	        		//loop
	        		markText = getString(R.string.Project);
	        		Toast.makeText(MenuActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		Intent intent = new Intent(MenuActivity.this, LoopActivity.class);
					startActivity(intent);
	        	}
	        	else if(position == 2){
	        		//projet
	        		markText = getString(R.string.IB);
	        		Toast.makeText(MenuActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		Intent intent = new Intent(MenuActivity.this, ListPlayActivity.class);
					startActivity(intent);	
	        	}
	        	else if(position == 3){
	        		//play music
	        		markText = getString(R.string.IB);
	        		Toast.makeText(MenuActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		Intent intent = new Intent(MenuActivity.this, ChatActivity.class);
					startActivity(intent);	
	        	}
	        	else if(position == 4){
	        		//IBB
	        		markText = getString(R.string.IB);
	        		Toast.makeText(MenuActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		Intent intent = new Intent(MenuActivity.this, SettingActivity.class);
					startActivity(intent);	
	        	}
	        	/**
	        	else if(position == 5){
	        		//Facebook
	        		markText = getString(R.string.IB);
	        		Toast.makeText(MenuActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		Intent intent = new Intent(MenuActivity.this, ForumActivity.class);
					startActivity(intent);	
	        	}
	        	else if(position == 6){
	        		//Facebook
	        		markText = getString(R.string.setting);
	        		Toast.makeText(MenuActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		Intent intent = new Intent(MenuActivity.this, SettingActivity.class);
					startActivity(intent);	
	        	}*/
	        	else{
	        		//exit
	        		markText = getString(R.string.RS);
	        		Toast.makeText(MenuActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		//myAudio.exitApp();
	        		finish();
	        	}
	        }
	    });
	}
}
		