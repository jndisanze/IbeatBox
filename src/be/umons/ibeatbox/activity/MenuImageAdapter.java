package be.umons.ibeatbox.activity;


import be.umons.ibeatbox.R;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.view.View;

public class MenuImageAdapter extends BaseAdapter {
    private Context mContext;

    public MenuImageAdapter(Context c) {
        mContext = c;
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

    // references to our images
    private Integer[] mThumbIds = {
    	    R.drawable.ic_micro,R.drawable.ic_loop,
    	    //R.drawable.ic_projet,
    	    R.drawable.ic_play,
    	    R.drawable.ic_ibb,
    	    //R.drawable.ic_fb,
    	    R.drawable.ic_setting_global,
    	    R.drawable.ic_turnoff
    };

}