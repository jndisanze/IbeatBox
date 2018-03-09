package be.umons.ibeatbox.activity;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import be.umons.ibeatbox.R;
import be.umons.ibeatbox.main.NativeAudio;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RecordMusicActivity extends Activity {
    protected Button createsound;
    private NativeAudio myAudio;
    private java.util.Timer timer; 
    private String soundNumber;
    private Thread t;
	private RecordMusicImageAdapter adapter;
    private void startThread(){
    	
    	timer.schedule (new TimerTask() {
            public void run()
            { 
            	  t = new Thread(new MyThread(0));
            	  t.start();
            	   
            }
        },0,64);
    }
    class MyThread implements Runnable{
	private int i;
		public MyThread(int i) {
			this.i=i; 
		}
		public void run() {
			//traitemtement 
		}
    }
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.record_music);
	    //myAudio = new NativeAudio();	
	    timer = new Timer();
	    GridView gridview = (GridView) findViewById(R.id.gridview);
	    adapter = new RecordMusicImageAdapter(this);
	    gridview.setAdapter(adapter);
        
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	String markText = "";
	        	if(position == 0){
	        		markText = getString(R.string.record);
	        		Toast.makeText(RecordMusicActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		Intent intent = new Intent(RecordMusicActivity.this, ListRecordersActivity.class);
					startActivity(intent);
	        		//startThread();
	        		//myAudio.startRecord();
	        	}
	        	else if(position == 1){
	        		markText = getString(R.string.play);
	        		Toast.makeText(RecordMusicActivity.this, "" + soundNumber, Toast.LENGTH_SHORT).show();
	        		Intent intent = new Intent(RecordMusicActivity.this, ListRecordersActivity.class);
					startActivity(intent);
	        		myAudio.playback();
	        		
	        	}
	        	else if(position == 2){
	        		markText = getString(R.string.paus);
	        		Toast.makeText(RecordMusicActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		//Intent intent = new Intent(RecordMusicActivity.this, MusicManagerActivity.class);
					//startActivity(intent);
	        		Toast.makeText(RecordMusicActivity.this,"", Toast.LENGTH_SHORT).show();
	        	}
	        	else if(position == 3){
	        		markText = getString(R.string.Stop);
	        		Toast.makeText(RecordMusicActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		//Intent intent = new Intent(RecordMusicActivity.this, MusicManagerActivity.class);
					//startActivity(intent);
	        		try{
	        		int f =0; //myAudio.setRecordBufferLocal();
	        		Toast.makeText(RecordMusicActivity.this, "taille = "+f, Toast.LENGTH_SHORT).show();
	        		}
	        		catch(Exception e){
	        			Toast.makeText(RecordMusicActivity.this, "fail", Toast.LENGTH_SHORT).show();
	        		}
	        	}
	        	else{
	        		markText = getString(R.string.loop);
	        		Toast.makeText(RecordMusicActivity.this, "" + markText, Toast.LENGTH_SHORT).show();
	        		//Intent intent = new Intent(RecordMusicActivity.this, MusicManagerActivity.class);
					//startActivity(intent);
	        	}
	        }
	    });
	}
    public void onPause(){
        // turn off all audio
        myAudio.onPause();
        super.onPause();
    }
 protected void onDestroy(){
        super.onDestroy();
    }
}
