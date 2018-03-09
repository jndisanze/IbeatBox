package be.umons.ibeatbox.activity;

import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import be.umons.ibeatbox.R;
import be.umons.ibeatbox.main.CreateLicence;
import be.umons.ibeatbox.main.Licence;
import be.umons.ibeatbox.main.NativeAudio;
import be.umons.ibeatbox.main.Sound;
import be.umons.ibeatbox.main.TagInit;
import be.umons.ibeatbox.main.Tools;

public class SoundRecordActivity extends Activity implements OnClickListener{
	private Button showDialogButton;
	private Context mContext;
	private Timer timer;
	private Chronometer mChronometer;
	private NativeAudio myAudio;
	private EditText  mySoundName;
	private CharSequence affichageBase= 0+":"+0+":"+"0"+":"+0+":"+0+"4/4";
	private CharSequence beatBar = affichageBase;
	private TextView counter;
	private Sound sound;
	private Tools tools;
	private long myElapsedMillis =0;
	 private int selected = 0; 
	 private int buffKey = 0;
	@Override
	public void onBackPressed (){
		tools.deleteSound(sound.getId());
		Intent intent = new Intent(SoundRecordActivity.this, MenuActivity.class);
		startActivity(intent);
	}
	@Override
	public void onPause (){
		if(progress != null && progress.isShowing())
			progress.dismiss();
		super.onPause();
	}
	private ProgressDialog progress;// = ProgressDialog.show(SoundRecordActivity.this, getString(R.string.waiting), 
    	//	getString(R.string.create_music), true, false);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sound_record);
		mContext = this; // to use all around this class
		timer = new Timer();
		tools = (Tools) getApplicationContext();
		tools.setSaveInInternetDatabase(false);
		//sound = tools.getSoundInDatabase().getCurrentSoundUseId();//tools.getSoundCurrentUse();
		sound = tools.currentSoundTemp;
		System.out.println(sound==null);
		System.out.println(sound.getId());
		initView();
		initViewAction();
		myAudio = tools.getNativeAudio();
		bpm=60;
		sNum=myAudio.getTimeSignatureNum();
		sDen = myAudio.getTimeSignatureDen();
		// creating new sound
	}
	private void showDialogSetTags() {
	    AlertDialog.Builder builder = 
	        new AlertDialog.Builder(mContext);
	    builder.setTitle("Instruments");;
	  final CharSequence[] choiceList =  new CharSequence[TagInit.instruments.length];
	  int i=0;
	    while(i<TagInit.instruments.length){
	    	if(i>=0 && i<10){
	    	choiceList[i]=TagInit.instruments[i];
	    	}
	    	else if(i>=25 && i<=27){
	    	choiceList[i]=TagInit.instruments[i];
	    	}
	    	else
	    		choiceList[i]=TagInit.instruments[i]+"\t None";
	    	++i;
	    	
	    }
	    builder.setSingleChoiceItems(
	            choiceList, 
	            selected, 
	            new DialogInterface.OnClickListener() {

	        public void onClick(
	                DialogInterface dialog, 
	                int which) {
	            buffKey = which;
	        }
	    })
	    .setCancelable(false)
	    .setPositiveButton("OK", 
	        new DialogInterface.OnClickListener() 
	        {
	            public void onClick(DialogInterface dialog, 
	                    int which) {
	            	//Sound s =  tools.getSoundCurrentRecord();
	            	System.out.println(choiceList[buffKey]);
	            	sound.setTAG_SOUND((String)choiceList[buffKey]);
	            	myAudio.setInstrument(TagInit.getNumberInstrument((String)choiceList[buffKey]));
	            	//tools.saveInCurrentRecordSound(s);
	                Toast.makeText(
	                        mContext, 
	                        "Select "+TagInit.getNumberInstrument((String) choiceList[buffKey])+" "+choiceList[buffKey], 
	                        Toast.LENGTH_SHORT
	                        )
	                        .show();
	                selected = buffKey;
	            }
	        }
	    )
	    .setNegativeButton("Cancel", 
	        new DialogInterface.OnClickListener() 
	        {
	            public void onClick(DialogInterface dialog, 
	                    int which) {
	                Toast.makeText(
	                        mContext, 
	                        "Cancel click", 
	                        Toast.LENGTH_SHORT
	                        )
	                        .show();
	            }
	        }
	    );
	     
	    AlertDialog alert = builder.create();
	    alert.show();
	}
	private int b=0,sNum=0,sDen=0,bpm=0;
	private void initCounterRythm(){
		beatBar = myAudio.getBarvalue()+":"+myAudio.getBeatvalue()+
				":"+0+":"+0+"ms"+"-"+myAudio.getTimeSignatureNum()+"/"+myAudio.getTimeSignatureDen();
		counter.setText(beatBar);
	}
	private void initView() {
		showDialogButton =
	             (Button) findViewById(R.id.rythm);
		showDialogButton.setBackgroundColor(Color.WHITE);
		showDialogButton.setTextColor(Color.rgb(65,105,255));
		mySoundName
		=(EditText) findViewById(R.id.nameSound);
		mySoundName.setText(sound.getName());
		String text =getString(R.string.setting);
		showDialogButton.setText(text);
		showDialogButton.setOnClickListener(configuration);
		Button button;
		mChronometer = (Chronometer) findViewById(R.id.chronometer1);
		this.mChronometer.setFormat("%m");;
		counter =(TextView)findViewById(R.id.counter);
		Button button1 = (Button) findViewById(R.id.imageLicence);
		button1.setOnClickListener(mLicence);
		button1.setBackgroundColor(Color.WHITE);
		button1.setTextColor(Color.rgb(65,105,255));
		// Watch for button clicks.
		button = (Button) findViewById(R.id.record);
		button.setOnClickListener(mStartListener);
		
		button = (Button) findViewById(R.id.play);
		button.setOnClickListener(mPlayback);
		
		/**button = (Button) findViewById(R.id.paus);
		button.setOnClickListener(mResetListener);
		button = (Button) findViewById(R.id.stop);
		button.setOnClickListener(mStopListener);
		*/
		
		button = (Button) findViewById(R.id.tag);
		button.setOnClickListener(mChoosTage);
		button.setBackgroundColor(Color.WHITE);
		button.setTextColor(Color.rgb(65,105,255));
		
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(mSave);
		button.setBackgroundColor(Color.WHITE);
		button.setTextColor(Color.rgb(65,105,255));
		
		button = (Button) findViewById(R.id.button2);
		button.setOnClickListener(mMove);
		button.setBackgroundColor(Color.WHITE);
		button.setTextColor(Color.rgb(65,105,255));
		////////////////////////////////////// //////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////
		mChronometer.setOnChronometerTickListener(
		  new Chronometer.OnChronometerTickListener(){
		  public void onChronometerTick(Chronometer chronometer) {
		      // TODO Auto-generated method stub
			  if(sound.getTime()*60*1000>=myElapsedMillis){
			  myElapsedMillis= SystemClock.elapsedRealtime() - mChronometer.getBase();
			 beatBar = myAudio.getBarvalue()+":"+myAudio.getBeatvalue()+
						":"+0+":"+((myElapsedMillis)/1000)+"ms"+":"+myAudio.getTimeSignatureNum()+"/"+myAudio.getTimeSignatureDen();
				counter.setText(beatBar);
				int mytest = myAudio.SoundTraitemnt();
				String markText = "";
				if(mytest==1){
					markText = "Born exceed " + mytest;
				}
        		Toast.makeText(SoundRecordActivity.this,markText, Toast.LENGTH_SHORT).show();
			  }
			  else{
				   
				  myElapsedMillis=0;
				  mChronometer.stop();
		          timer.cancel();
		          initCounterRythm();
			  }
		     }}
		       );
		 
	    }
	
	View.OnClickListener mChoosTage = new OnClickListener() {
        public void onClick(View v) {
        	showDialogSetTags();
        }
    };
    View.OnClickListener mLicence = new OnClickListener() {
        public void onClick(View v) {
        	showDialogSetLicence();
        }
    };
    View.OnClickListener mSave = new OnClickListener() {
        public void onClick(View v) {
        	ShowDialogLoadingSound();
        }
    };	
    View.OnClickListener mMove = new OnClickListener() {
        public void onClick(View v) {
        	tools.deleteSound(sound.getId());
        	Intent intent = new Intent(SoundRecordActivity.this,MenuActivity.class);
        	startActivity(intent);
        }
    };
    
	View.OnClickListener mStartListener = new OnClickListener() {
        public void onClick(View v) {
        	if(!paus){
            	mChronometer.stop();
            	timer.cancel();
            	initCounterRythm();
            }
        	mChronometer.setBase(SystemClock.elapsedRealtime());
        	  //Debug.startMethodTracing("stk");
        	  myAudio.startRecord();
        	  //Debug.stopMethodTracing();
        	mChronometer.start();
        }
    };
    View.OnClickListener mPlayback = new OnClickListener() {
        public void onClick(View v) {
        	if(!paus){
        	mChronometer.stop();
        	timer.cancel();
        	initCounterRythm();
        	}
        	myAudio.selectClip(3,1);
        	Toast.makeText(SoundRecordActivity.this,"Playback", Toast.LENGTH_SHORT).show();
        }
    };
    View.OnClickListener mStopListener = new OnClickListener() {
        public void onClick(View v) {
            mChronometer.stop();
            timer.cancel();
        	initCounterRythm();
            //mChronometer.start();
        	tools.getNativeAudio().stopRecord();
        }
    };
	protected boolean paus = false;
 
    View.OnClickListener mResetListener = new OnClickListener() {
        public void onClick(View v) {
        	paus  = true;
        	myAudio.onPause();
        	Toast.makeText(SoundRecordActivity.this,"on test", Toast.LENGTH_SHORT).show();  
        }
    };
	private void initViewAction() {
		//showDialogButton.setOnClickListener(this);
	}
	 
public void onClick(View view) {
	if (view.equals(showDialogButton)) {
		//showDialogButtonClick();
	}
}

View.OnClickListener configuration = new OnClickListener() {
        public void onClick(View v) {
        	LayoutInflater li = LayoutInflater.from(mContext);
			View promptsView = li.inflate(R.layout.conf_sound_record, null);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
			alertDialogBuilder.setView(promptsView);
			 final EditText bns = (EditText) promptsView.findViewById(R.id.BNS);
			 bns.setText("1");
			 final EditText tempo = (EditText) promptsView.findViewById(R.id.editBPM);
			 tempo.setText(myAudio.getBMP()+"");
			 final EditText signature = (EditText) promptsView.findViewById(R.id.signature);
			 signature.setText(myAudio.getTimeSignatureNum()+"/"+myAudio.getTimeSignatureDen());
			 final EditText time = (EditText) promptsView.findViewById(R.id.time);
			 time.setText("10");
				alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								AlertDialog dialog1 = new AlertDialog.Builder(SoundRecordActivity.this).create();
								dialog1.setTitle("Error");
								String t = tempo.getText().toString();
								String s = signature.getText().toString();
								//Sound sound = tools.getSoundCurrentRecord();
								if(t.matches("[0-9]+")){
							    int n = Integer.parseInt(t);
							    if(n>= 60 && n<=180){
							    	int tempo = Integer.parseInt(t);
							    myAudio.setBMP(tempo);
								sound.setTempo(tempo);
							    }
							    else{
							    	dialog1.setMessage("Bad Format: "+t);
									dialog1.show();
							    	}
							    }
							    if(s.matches("[0-9]+/[0-9]+")){
							    	String[] m = s.split("/");
							    	int r = Integer.parseInt(m[0]);
							    	if(m[1].matches("2|4|8|16") && r>=1 && r<= 16){
							    	myAudio.setTimeSignatureNum(Integer.parseInt(m[0]));
							    	myAudio.setTimeSignatureDen(Integer.parseInt(m[1]));
							    	sound.setSignatureRythmNum(Integer.parseInt(m[0]));
							    	sound.setSignatureRythmDen(Integer.parseInt(m[1]));
							    	}
							    else{
							    	dialog1.setMessage("Bad Format configuration: "+s);
									dialog1.show();
							    	}
							   //missing  	bns and munit
							    String time_str = time.getText().toString();
							    if(time_str.matches("[0-9]+")){
							    myAudio.setMinute(Integer.parseInt(time_str));
							    sound.setTime(Integer.parseInt(time_str));
							    }
							    String bns_str = bns.getText().toString();
							    if(bns_str.matches("[0-9]+")){
							    // do something
							    //tools.setSoundCurrentRecord(sound);
							    }
							    }
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
        }
        
    };	
	
	private void showDialogSetLicence() {
	    AlertDialog.Builder builder = 
	        new AlertDialog.Builder(mContext);
	    builder.setTitle("Set licence Creative Commons");
	    CreateLicence r = new CreateLicence(); 
	    final Licence l1 = r.createLicenceCCBY();
	    final Licence l2=r.createLicenceCCBYNC(); 
	    final Licence l3=r.createLicenceCCBYNCND();
	    final Licence l4=r.createLicenceCCBYNCSA();
	    final Licence l5=r.createLicenceCCBYND();
	    final Licence l6=r.createLicenceCCBYSA();
	    final CharSequence[] choiceList = 
	    {		l1.getLicence(),
	    		l2.getLicence(), 
	    		l3.getLicence(),
	    		l4.getLicence(),
	    		l5.getLicence(),
	    		l6.getLicence()};
	    
	    builder.setSingleChoiceItems(
	            choiceList, 
	            selected, 
	            new DialogInterface.OnClickListener() {

	        public void onClick(
	                DialogInterface dialog, 
	                int which) {
	            //set to buffKey instead of selected 
	            //(when cancel not save to selected)
	            buffKey = which;
	            if(selected ==0)
	            	sound.setLicence(l1);
	            else if(selected ==1)
	            	sound.setLicence(l2);
	            else if(selected ==2)
	            	sound.setLicence(l3);
	            else if(selected ==3)
	            	sound.setLicence(l4);
	            else if(selected ==4)
	            	sound.setLicence(l5);
	            else if(selected ==5)
	            	sound.setLicence(l6);
	        }
	    })
	    .setCancelable(false)
	    .setPositiveButton("OK", 
	        new DialogInterface.OnClickListener() 
	        {
	            public void onClick(DialogInterface dialog, 
	                    int which) {
	            	// save licences
	                Toast.makeText(
	                        mContext, 
	                        "Select "+choiceList[buffKey], 
	                        Toast.LENGTH_SHORT
	                        )
	                        .show();
	                
	                selected = buffKey;
	            }
	        }
	    )
	    .setNegativeButton("Cancel", 
	        new DialogInterface.OnClickListener() 
	        {
	            public void onClick(DialogInterface dialog, 
	                    int which) {
	                Toast.makeText(
	                        mContext, 
	                        "Cancel click", 
	                        Toast.LENGTH_SHORT
	                        )
	                        .show();
	            }
	        }
	    );
	     
	    AlertDialog alert = builder.create();
	    alert.show();
	}
	private void ShowDialogLoadingSound()
	{
		AlertDialog.Builder builder = 
		        new AlertDialog.Builder(mContext);
		    builder.setTitle(getString(R.string.download));
		   
			
			final CharSequence[] choiceList = {getString(R.string.local),
					getString(R.string.local)+"+"+getString(R.string.distance)}; 
		
		    builder.setSingleChoiceItems(
		            choiceList, 
		            selected, 
		            new DialogInterface.OnClickListener() {

		        public void onClick(
		                DialogInterface dialog, 
		                int which) {
		            
		        	 selected=which;
		        }
		    })
		    .setCancelable(false)
		    .setPositiveButton("OK", 
		        new DialogInterface.OnClickListener() 
		        {
		            public void onClick(DialogInterface dialog, 
		                    int which) {
		            	// set sound for list view sound in loop 
		            	System.out.println("here "+selected);
		            	if(selected == 1){
		            	tools.setSaveInInternetDatabase(true);
		            	}
		            	sound.setName(mySoundName.getText().toString());
		            	tools.updateObservateur(sound);
		            	Intent intent = new Intent(SoundRecordActivity.this,MenuActivity.class);
		            	startActivity(intent);
		            }
		        }
		    )
		    .setNegativeButton("Cancel", 
		        new DialogInterface.OnClickListener() 
		        {
		            public void onClick(DialogInterface dialog, 
		                    int which) {
		                Toast.makeText(
		                        mContext, 
		                        "Cancel click", 
		                        Toast.LENGTH_SHORT 
		                        )
		                        .show();
		            }
		        }
		    );
		     
		    AlertDialog alert = builder.create();
		    alert.show();
	}
}