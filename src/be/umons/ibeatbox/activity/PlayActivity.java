package be.umons.ibeatbox.activity;

import org.urbanstew.soundcloudapi.SoundCloudAPI;

import be.umons.ibeatbox.R;
import be.umons.ibeatbox.main.Sound;
import be.umons.ibeatbox.main.Tools;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

public class PlayActivity extends Activity {
	
	private Tools tools;
	private TextView soundName;
	private ImageButton play,stop,fb,soundCloud;
	private Button share;
	private Sound currentSound;
	private RatingBar rating;
	private boolean scoreChange = false;
	private ProgressBarPlay progressBarPlay;
	@Override
	public void onBackPressed (){
		//mise a jour de score
		 if(this.scoreChange){
			 tools.updateScoreObservateur(currentSound.getId(),(Double)currentSound.getRating());
		 }
		Intent intent = new Intent(PlayActivity.this, ListPlayActivity.class);
		startActivity(intent);
	}
	@Override
	public void onPause (){
		if(progress != null && progress.isShowing())
			progress.dismiss();
		super.onPause();
	}
	private ProgressDialog progress;// = ProgressDialog.show(PlayActivity.this, getString(R.string.waiting), 
	private ProgressBar progressBar;
    		//getString(R.string.create_music), true, false);
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.play_sound);
	        tools = (Tools)getApplicationContext();
	        this.currentSound = tools.getSoundInDatabase().getCurrentSoundUseId();
	        soundName = (TextView) findViewById(R.id.soundLabel);
	        soundName.setText("Track :"+currentSound.getName()+"\t"+currentSound.getTAG_SOUND() +"\n"
	        +"Rat:"+currentSound.getRating()+"\t Licence:"+currentSound.getLicence().getLicence());
	        progressBar=(ProgressBar) findViewById(R.id.progressBarPlay);
	        progressBar.setProgress(0);
	        progressBarPlay = new ProgressBarPlay();
	        View.OnClickListener playSound = new OnClickListener() {
	            public void onClick(View v) {
	            	tools.getNativeAudio().playback(currentSound.getId());
	            	progressBarPlay.execute();	
	            }
	        };
	        View.OnClickListener shareSound = new OnClickListener() {
	            public void onClick(View v) {
	            //send sound on our serveur
		          //tools.setSaveInInternetDatabase(true);
		          //tools.updateObservateur(currentSound);
	            	tools.soundDatabaseDistance.update(currentSound);
	            }
	        };
	        View.OnClickListener stopSound = new OnClickListener() {
	            public void onClick(View v) {
	            	tools.getNativeAudio().stopMusic();
	            	progressBarPlay.cancel(true);
	            	progressBar.setProgress(0);
	            }
	        };
	        View.OnClickListener postFacebookSound = new OnClickListener() {
	            public void onClick(View v) {
	            	
	            }
	        };
	        View.OnClickListener postSoundCloudSound = new OnClickListener() {
	            public void onClick(View v) {
	            
	            	
	            	
	            }
	        };
	        play = (ImageButton) findViewById(R.id.play);
	        play.setOnClickListener(playSound);
	       
	        share = (Button) findViewById(R.id.share);
	        share.setBackgroundColor(Color.WHITE);
			share.setTextColor(Color.rgb(65,105,255));
	        share.setOnClickListener(shareSound);
	        stop = (ImageButton) findViewById(R.id.stop);
	        stop.setOnClickListener(stopSound);
	        
	        play.setOnClickListener(playSound);
	        fb = (ImageButton) findViewById(R.id.facebook);
	        
	        fb.setOnClickListener(postFacebookSound);
	        soundCloud = (ImageButton) findViewById(R.id.soundcloud);
	        soundCloud.setOnClickListener(postSoundCloudSound);
	         
	        rating = (RatingBar) findViewById(R.id.rating);
	        rating.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){

				public void onRatingChanged(RatingBar arg0, float arg1,
						boolean arg2) {
					currentSound.setRating(arg1);
					Toast.makeText(PlayActivity.this,String.valueOf(arg1), Toast.LENGTH_SHORT).show();
				}
	        	
	        });
	 }
public class ProgressBarPlay extends AsyncTask<Void, Integer, Void> {
  
  int myProgress;
  @Override
  protected void onPostExecute(Void result) {
   // TODO Auto-generated method stub
   Toast.makeText( PlayActivity.this,
         "onPostExecute", Toast.LENGTH_LONG).show();
         //buttonStartProgress.setClickable(true);
  }

  @Override
  protected void onPreExecute() {
   // TODO Auto-generated method stub
   Toast.makeText( PlayActivity.this,
         "onPreExecute", Toast.LENGTH_LONG).show();
   myProgress = 0;
  }

  @Override
  protected Void doInBackground(Void... params) {
   // TODO Auto-generated method stub
   while(myProgress<(currentSound.getTime()*10)){ //100 min of music
    myProgress++;
    publishProgress(myProgress);
       SystemClock.sleep(currentSound.getTime()*10);
   }
   return null;
  }

  @Override
  protected void onProgressUpdate(Integer... values) {
   // TODO Auto-generated method stub
   progressBar.setProgress(values[0]);
  }

 }
}