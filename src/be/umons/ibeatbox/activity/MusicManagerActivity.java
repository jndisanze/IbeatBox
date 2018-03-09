package be.umons.ibeatbox.activity;

import be.umons.ibeatbox.R;
import be.umons.ibeatbox.main.NativeAudio;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MusicManagerActivity extends Activity {
    protected Button createsound;
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.music_manager);

	    GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(new MusicManagerImageAdapter(this));

	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	String markText = "";
	        	if(position == 0){
	        		markText = getString(R.string.record);
	        		Toast.makeText(MusicManagerActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		Intent intent = new Intent(MusicManagerActivity.this, ListRecordersActivity.class);
					startActivity(intent);
	        	}
	        	else if(position == 1){
	        		markText = getString(R.string.my_musique);
	        		Toast.makeText(MusicManagerActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		Intent intent = new Intent(MusicManagerActivity.this, MusicManagerActivity.class);
					startActivity(intent);
	        	}
	        	else if(position == 2){
	        		markText = getString(R.string.loop);
	        		Toast.makeText(MusicManagerActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		Intent intent = new Intent(MusicManagerActivity.this, MusicManagerActivity.class);
					startActivity(intent);	
	        	}
	        	else{
	        		markText = getString(R.string.my_musique);
	        		Toast.makeText(MusicManagerActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		Intent intent = new Intent(MusicManagerActivity.this, MusicManagerActivity.class);
					startActivity(intent);
	        	}
	        }
	    });
	}
}
