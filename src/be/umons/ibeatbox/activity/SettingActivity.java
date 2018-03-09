package be.umons.ibeatbox.activity;

import be.umons.ibeatbox.R;
import be.umons.ibeatbox.main.CreateLicence;
import be.umons.ibeatbox.main.Licence;
import be.umons.ibeatbox.main.NativeAudio;
import be.umons.ibeatbox.main.Tools;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;


public class SettingActivity extends Activity{
	private Tools tools;
	private int tempoValue,mesureValue,signNumValue,signDenValue;
	private NativeAudio myAudio;
	private Context mContext;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_setting);
        mContext = getBaseContext();
        tools = (Tools)getApplicationContext();
        String[] value;
        NumberPicker mesure = (NumberPicker) findViewById(R.id.mesurePicker1);
        mesure.setMaxValue(20);
        mesure.setMinValue(1);
        value= new String[1000];
        for(int i =0;i<value.length;i++){
        	value[i]=i+1+"";
        }
        mesure.setWrapSelectorWheel(false);
        mesure.setDisplayedValues(value);
        NumberPicker tempo = (NumberPicker) findViewById(R.id.tempoPicker1);
        tempo.setMaxValue(500);
        tempo.setMinValue(50);
        tempo.setWrapSelectorWheel(false);
        tempo.setDisplayedValues(value);
        
        NumberPicker signNum = (NumberPicker) findViewById(R.id.signNumPicker1);
        signNum.setMaxValue(16);
        signNum.setMinValue(1);
        value= new String[16];
        for(int i =0;i<value.length;i++){
        	value[i]=i+1+"";
        }
        signNum.setWrapSelectorWheel(false);
        signNum.setDisplayedValues(value);
        NumberPicker signDen = (NumberPicker) findViewById(R.id.signDenPicker1);
        signDen.setMaxValue(4);
        signDen.setMinValue(1);
        signDen.setWrapSelectorWheel(false);
        String[] value1 = new String[]{"2","4","8","16"};
        signDen.setWrapSelectorWheel(false);
        signDen.setDisplayedValues(value1);
        
        Button btn = (Button)findViewById(R.id.licence);
        btn.setBackgroundColor(Color.WHITE);
        btn.setTextColor(Color.rgb(65,105,255));
        btn.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				showDialogSetLicence();
				
			}});
        mesure.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){

			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
			        mesureValue=newVal; 
			}
        	
        });
        tempo.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){

			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				tempoValue=newVal;
			}
        	
        });
        signDen.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){

			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				signDenValue = newVal;
			}
        	
        });
        signNum.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){

			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				signNumValue=newVal;
			}});
        Button button;
        button =(Button) findViewById(R.id.ok);
        View.OnClickListener ok= new OnClickListener() {
            public void onClick(View v) {
            	tools.getNativeAudio().setTimeSignatureNum(signNumValue);
            	tools.getNativeAudio().setTimeSignatureDen(signDenValue);
            	tools.getNativeAudio().setMesure(mesureValue);
            	tools.getNativeAudio().setBMP(tempoValue);
            	tools.signatureRythmNum=signNumValue;
            	tools.signatureRythmDen = signDenValue;
            	tools.bpm_tempo = tempoValue;
            	tools.mesure=mesureValue;
            	Intent intent = new Intent(SettingActivity.this, MenuActivity.class);
				startActivity(intent);
            }
        };
        button.setOnClickListener(ok);
        button =(Button) findViewById(R.id.cancel);
        View.OnClickListener cancel = new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(SettingActivity.this, MenuActivity.class);
				startActivity(intent);
            }
        };
        button.setOnClickListener(cancel);
  }
	private void showDialogSetLicence() {
	    AlertDialog.Builder builder = 
	        new AlertDialog.Builder(SettingActivity.this);
	    builder.setTitle("licence Creative Commons");
	    CreateLicence r = new CreateLicence(); 
	    final Licence l1 = r.createLicenceCCBY();
	    final Licence l2=r.createLicenceCCBYNC(); 
	    final Licence l3=r.createLicenceCCBYNCND();
	    final Licence l4=r.createLicenceCCBYNCSA();
	    final Licence l5=r.createLicenceCCBYND();
	    final Licence l6=r.createLicenceCCBYSA();
	    final CharSequence[] choiceList = 
	    {
	    		l1.getLicence(),
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
	            if(which ==0)
	            	tools.setLicence(l1);
	            else if(which ==1)
	            	tools.setLicence(l2);
	            else if(which ==2)
	            	tools.setLicence(l3);
	            else if(which ==3)
	            	tools.setLicence(l4);
	            else if(which ==4)
	            	tools.setLicence(l5);
	            else if(which ==5)
	            	tools.setLicence(l6);
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
	private int selected = 0; 
	private int buffKey = 0;
}	
