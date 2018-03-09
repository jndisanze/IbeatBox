package be.umons.ibeatbox.activity;

import java.io.File;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import be.umons.ibeatbox.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ConfigSoundRecorderActivity extends Activity {
	/**private WheelView signNum, signDen,tempo,bns;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conf_sound_record);
        LayoutInflater inf = LayoutInflater.from(getBaseContext());
        bns = (WheelView)findViewById(R.id.bns);
        bns.setViewAdapter(new NumericWheelAdapter(getBaseContext(), 1,8));
        bns.setCyclic(true);
        tempo = (WheelView)findViewById(R.id.tempo);
        tempo.setViewAdapter(new NumericWheelAdapter(getBaseContext(), 60,180));
        tempo.setCyclic(true);
        signNum = (WheelView)findViewById(R.id.signNum);
        signNum.setViewAdapter(new NumericWheelAdapter(getBaseContext(), 1,16));
        signNum.setCyclic(true);
        signDen = (WheelView)findViewById(R.id.signDen);
        Integer[] t = new Integer[]{2,4,8,16};
        signDen.setViewAdapter(new ArrayWheelAdapter<Integer>(getBaseContext(),t));
        signDen.setCyclic(true);
        final Button validate = (Button)findViewById(R.id.validate);
        validate.setOnClickListener(validlestener);
	}
	View.OnClickListener validlestener = new OnClickListener() {
        public void onClick(View v) {
         int b = bns.getCurrentItem();
         int t = tempo.getCurrentItem();
         //int s1= signNum
        }
    };*/
}