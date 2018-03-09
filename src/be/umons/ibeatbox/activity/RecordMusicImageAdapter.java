package be.umons.ibeatbox.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import be.umons.ibeatbox.R;

public class RecordMusicImageAdapter extends BaseAdapter {
	 private Context mContext;
     private TextView r;
	private Integer numb;
	    public RecordMusicImageAdapter(Context c) {
	        mContext = c;
	        numb = R.drawable.ic_play;
	    }

	    public int getCount() {
	        return mThumbIds.length;
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            imageView.setPadding(1, 1, 1, 1);
	        } else {
	            imageView = (ImageView) convertView;
	        }

	        imageView.setImageResource(mThumbIds[position]);
	        return imageView;
	    }
	    public void changeTrue(boolean a){
	    	if(a)
	    		numb = R.drawable.ic_turnoff;
	    	else
	    		numb = R.drawable.ic_play;
	    		
	    }
	    // references to our images
	    private Integer[] mThumbIds = {
	    	    R.drawable.ic_record,R.drawable.ic_play_1,
	    	    R.drawable.ic_pause,R.drawable.ic_stop,R.drawable.ic_loop
	    };

}

