package be.umons.ibeatbox.activity;

import java.io.File;
import java.util.ArrayList;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import be.umons.ibeatbox.R;
import be.umons.ibeatbox.databases.DownloadDatabase;
import be.umons.ibeatbox.main.CreateLicence;
import be.umons.ibeatbox.main.Licence;
import be.umons.ibeatbox.main.NativeAudio;
import be.umons.ibeatbox.main.Sound;
import be.umons.ibeatbox.main.Tools;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

public class LoopCheckboxChooseActivity extends  Activity implements OnQueryTextListener {
	private LoopCheckboxChooseActivity mContext;
	private ListView listView;
	private Tools tools;
	private NativeAudio myAudio;
	private ArrayList<String> loopSound= new ArrayList<String>();
	private ArrayList<String> id_loopSound= new ArrayList<String>();
	private HashMap<String,Sound> hp;
	private boolean isOnline = false;
    private String request ="SELECT id from id_sound";
	private final static String TAG = "Search online by key";
    private SearchView mSearchView;
    private ListView mListViewSearch;
    private ArrayAdapter<String> mAdapterSearch;
    private ArrayList<String> strings = new ArrayList<String>();
	@Override
	public void onBackPressed (){
		Intent intent = new Intent(LoopCheckboxChooseActivity.this, LoopActivity.class);
		startActivity(intent);
	}
	@Override
	public void onPause (){
		if(progress != null && progress.isShowing())
			progress.dismiss();
		super.onPause();
	}
	private ProgressDialog progress ;//= ProgressDialog.show(LoopCheckboxChooseActivity.this, getString(R.string.waiting), 
	private String[] mStrings;
	private Button advance;
	private Button search;
    		//getString(R.string.create_music), true, false);
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_loop_checkbox_list);
        mContext = this;
        listView = (ListView)findViewById(R.id.loopChoose);
        View.OnClickListener mSave = new OnClickListener() {
            public void onClick(View v) {
            	final int selected = -1;
            	 AlertDialog.Builder builder = 
            		        new AlertDialog.Builder(mContext);
            		    builder.setTitle("Option");
            		    
            		    final CharSequence[] choiceList = 
            		    {getString(R.string.all,R.string.byscore)};
            		    
            		    builder.setSingleChoiceItems(
            		            choiceList, 
            		            selected, 
            		            new DialogInterface.OnClickListener() {

            		        public void onClick(
            		                DialogInterface dialog, 
            		                int which) {
            		            //set to buffKey instead of selected 
            		            //(when cancel not save to selected)
            		            //buffKey = which;
            		            if(selected ==0)
            		            	request="SELECT a.id FROM id_sound a, sound_score b WHERE a.id = b.id ORDER BY b.score DESC ";
            		            else
            		            	request ="SELECT id from id_sound";
            		            	
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
            		                        " ", 
            		                        Toast.LENGTH_SHORT
            		                        )
            		                        .show();
            		                
            		                //selected = buffKey;
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
        };
        View.OnClickListener mSearch = new OnClickListener() {
            public void onClick(View v) {
            	System.out.println(request+"----------------->");
        		ArrayList<String> n = tools.soundDatabaseDistance.getSoundsBykeyTag(request);
        		System.out.println(n.size() +"----------------->");
        		if(n.size()>0){
        			ArrayList<Bitmap> arr_bitmaps = new ArrayList<Bitmap>();
        			for(int i =0;i<n.size();i++){
        				arr_bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.ic_music));
        				loopSound.add(n.get(i));
        				System.out.println(n.get(i));
        			}
        			LoopAdapter myadapter = new LoopAdapter(LoopCheckboxChooseActivity.this, arr_bitmaps,n);
        			listView.setAdapter(myadapter);
        			}	
            //Intent intent = new Intent(LoopCheckboxChooseActivity.this,MenuActivity.class);
            //startActivity(intent);
            }
        };
        advance = (Button)findViewById(R.id.advance);
        advance.setBackgroundColor(Color.WHITE);
        advance.setTextColor(Color.rgb(65,105,255));
        advance.setOnClickListener(mSave);
        search = (Button)findViewById(R.id.search);
        search.setOnClickListener(mSearch);
        search.setBackgroundColor(Color.WHITE);
        search.setTextColor(Color.rgb(65,105,255));
        tools = (Tools) getApplicationContext();
        hp =tools.getSoundInDatabase().getSounds(); 
        myAudio = tools.getNativeAudio();
        mSearchView =(SearchView) findViewById(R.id.searchView1);
        mSearchView.setQueryHint(TAG);
        mListViewSearch = (ListView) findViewById(R.id.loopChoose);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni1 = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo ni2 = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(ni1.isConnected() || ni2.isConnected()) {
			System.out.println("connect");
			new Thread(new Runnable() {

				public void run() {
					//strings = tools.soundDatabaseDistance.getInfoSound();
					strings =new ArrayList<String>();
		
				}}).start();
			mStrings = new String[strings.size()];
			for(int i =0;i<strings.size();i++){
				System.out.println(strings.get(i));
				mStrings[i]=strings.get(i);
				System.out.println(strings.get(i));
			}
		}
		else{
			mStrings = new String[strings.size()];
			AlertDialog dialog = new AlertDialog.Builder(LoopCheckboxChooseActivity.this).create();
    		dialog.setTitle(getString(R.string.attention));
    		dialog.setMessage(getString(R.string.error));
    		dialog.show();
			
		}
    		
        mListViewSearch.setAdapter(mAdapterSearch = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                mStrings));
        mListViewSearch.setTextFilterEnabled(true);
        setupSearchView();
        initLoopSound();
	}
	private void setupSearchView() {
		this.mSearchView.setIconifiedByDefault(false);
		this.mSearchView.setOnQueryTextListener(this);
		this.mSearchView.setSubmitButtonEnabled(false);
	}
	private void initLoopSound(){
		ArrayList<Bitmap> arr_bitmaps = new ArrayList<Bitmap>();
		//HashMap<String,Sound> hp =tools.getSoundInDatabase().getSounds();
		Iterator i = hp.entrySet().iterator();
		while(i.hasNext()){
		Map.Entry entry = (Map.Entry) i.next();
		String uid = (String) entry.getKey();
		Sound sound = (Sound) entry.getValue();	
        arr_bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.ic_music));
        loopSound.add(sound.getName());
        id_loopSound.add(sound.getId());
		}
		LoopAdapter myadapter = new LoopAdapter(LoopCheckboxChooseActivity.this, arr_bitmaps, loopSound);
        listView.setAdapter(myadapter);
	}
	class LoopAdapter extends BaseAdapter{
    	public String title[];
    	public String description[];
    	ArrayList<String> arr_calllog_name = new ArrayList<String>();
    	public Activity context;
    	ArrayList<Bitmap> imageId; 
    	public LayoutInflater inflater;
    	public LoopAdapter(Activity context, ArrayList<Bitmap> arr_bitmaps, ArrayList<String> arr_calllog_name) {
    		super();

    		this.imageId = arr_bitmaps;
    		this.context = context;
    		this.arr_calllog_name = arr_calllog_name;
    	
    	    this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    		Button  cbx;
    	}
    	public View getView(final int position, View convertView, ViewGroup parent) {
    		// TODO Auto-generated method stub

    		final ViewHolder holder;
    		if(convertView==null)
    		{
    			holder = new ViewHolder();
    			convertView = inflater.inflate(R.layout.loop_checkbox_list, null);

    			holder.image = (ImageView) convertView.findViewById(R.id.imageViewLoopChoose);
    			holder.txtName = (TextView) convertView.findViewById(R.id.textViewLoopChoose);
    			holder.cbx = (Button) convertView.findViewById(R.id.loopChooseButton);
    			holder.cbx.setBackgroundColor(Color.WHITE);
    			holder.cbx.setTextColor(Color.rgb(65,105,255));
    			convertView.setTag(holder);
    		}
    		else
    			holder=(ViewHolder)convertView.getTag();

    		holder.image.setImageBitmap(getImageId().get(position));
    		holder.txtName.setText(arr_calllog_name.get(position));
    		
    		holder.cbx.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Sound s = null;
					String id;
					if(isOnline){
						id = id_loopSound.get(position);
						DownloadDatabase data = new DownloadDatabase(id,context);
						data.execute(tools.getStrurl());
						while(!data.isFinish()){
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						s = data.getSound();
					}
					else{
					id = id_loopSound.get(position);
					s = tools.getSoundInDatabase().getSounds().get(id);
					}
						//myAudio.chargingSound(id);
						File f = context.getFilesDir();
						String uid_erase = tools.loop.getSound(tools.currentpostionloopChoose).getId();
						if(tools.loop.isVoice(tools.currentpostionloopChoose)){
						    myAudio.eraseSoundVoiceLoope(f.toString()+"/"+uid_erase);
						}
						else
						{
						    myAudio.eraseSoundLoope(f.toString()+"/"+uid_erase);
						    	
						}
						myAudio.setLoopUIDSound(f.toString()+"/"+id);
						tools.loop.addSound(tools.currentpostionloopChoose, s);
						System.out.println(tools.loop.size()+"  value to replace" + tools.currentpostionloopChoose);
						Toast.makeText(getApplicationContext(),s.getName() +" Select", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(LoopCheckboxChooseActivity.this,LoopActivity.class);
			            startActivity(intent);
				}
			});
    		
    		return convertView;
    	}
	}
	public boolean onQueryTextChange(String newText) {
		if(TextUtils.isEmpty(newText)){
			this.mListViewSearch.clearTextFilter();
		}
		else
			this.mListViewSearch.setFilterText(newText.toString());
		return false;
	}
	public boolean onQueryTextSubmit(String query) {
		System.out.println(query+"----------------->");
		ArrayList<String> n = tools.soundDatabaseDistance.getSoundsBykeyTag(query);
		System.out.println(n.size() +"----------------->");
		if(n.size()>0){
			ArrayList<Bitmap> arr_bitmaps = new ArrayList<Bitmap>();
			for(int i =0;i<n.size();i++){
				arr_bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.ic_music));
				this.loopSound.add(n.get(i));
				System.out.println("sound find"+n.get(i));
			}
			LoopAdapter myadapter = new LoopAdapter(LoopCheckboxChooseActivity.this, arr_bitmaps,n);
			isOnline = true;
			listView.setAdapter(myadapter);
			}
		return false;
	}
}
