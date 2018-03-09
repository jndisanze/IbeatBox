package be.umons.ibeatbox.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import be.umons.ibeatbox.R;
import be.umons.ibeatbox.activity.LoopActivity.LoopAdapter.ViewHolder;
import be.umons.ibeatbox.main.NativeAudio;
import be.umons.ibeatbox.main.Sound;
import be.umons.ibeatbox.main.Tools;

public class LoopActivity extends Activity{
	ListView listView;
	private String link;
	LoopAdapter myadapter;
	ArrayList<String> arr_cars = new ArrayList<String>();
	AlertDialog.Builder alertDialogBuilder;
	private  Tools tools;
	private NativeAudio myAudio;
	private Context mContext;
	/**
	private ArrayList<String> loopSound = new ArrayList<String>();
	private ArrayList<String> id_loopSound = new ArrayList<String>();*/
	private ArrayList<Bitmap> imageId = new ArrayList<Bitmap>(); 
	@Override
	public void onBackPressed (){
		Intent intent = new Intent(LoopActivity.this, MenuActivity.class);
		startActivity(intent);
	}
	@Override
	public void onPause (){
		if(progress != null && progress.isShowing())
			progress.dismiss();
		super.onPause();
	}
	private ProgressDialog progress;// = ProgressDialog.show(LoopActivity.this, getString(R.string.waiting), 
	private int selected;
	protected boolean loopstart = false;;
    		//getString(R.string.create_music), true, false);
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loop_list);
        mContext = this;
        listView = (ListView)findViewById(R.id.listloop);
        tools = (Tools) getApplicationContext();
        myAudio = tools.getNativeAudio();
        link=mContext.getFilesDir().toString();
        for(int i=0;i<tools.loop.size();i++){
        imageId.add(BitmapFactory.decodeResource(getResources(), R.drawable.ic_music));
        }
    	View.OnClickListener addTrack = new OnClickListener() {
            public void onClick(View v) {
            	Sound sound = tools.getDefauldSound();
            	tools.loop.setVoice(sound); 
            	imageId.add(BitmapFactory.decodeResource(getResources(), R.drawable.ic_music));
        		myAudio.setVoiceLoopUIDSound(link+"/"+sound.getId());
        		LoopAdapter myadapter = new LoopAdapter(LoopActivity.this);
                listView.setAdapter(myadapter);
            	
            	//Intent intent = new Intent(LoopActivity.this,LoopCheckboxChooseActivity.class);
                //startActivity(intent);
            }
        };
        View.OnClickListener save = new OnClickListener() {
            public void onClick(View v) {
            Sound s = tools.getDefauldSound();
            	File f = mContext.getFilesDir();
            	myAudio.saveLoopFile(f.toString()+"/"+s.getId());
            	tools.getSoundInDatabase().addSound(s);
            	Intent intent = new Intent(LoopActivity.this,MenuActivity.class);
            	startActivity(intent);	
            
            }
        };
        View.OnClickListener startLoop = new OnClickListener() {
            public void onClick(View v) {
            	myAudio.startLoop();
            	loopstart = true;
            }
        };
        View.OnClickListener stopLoop = new OnClickListener() {
            public void onClick(View v) {
            	myAudio.stopLoop();
            }
        };
        View.OnClickListener setting = new OnClickListener() {
            public void onClick(View v) {
            	//myAudio.stopLoop();
            }
        };
        Button btn = (Button)findViewById(R.id.ajouter);
        btn.setOnClickListener(addTrack);
        btn.setBackgroundColor(Color.WHITE);
        btn.setTextColor(Color.rgb(65,105,255));
        Button btn1 = (Button)findViewById(R.id.valider);
        btn1.setOnClickListener(save);
        btn1.setBackgroundColor(Color.WHITE);
        btn1.setTextColor(Color.rgb(65,105,255));
        Button conf = (Button)findViewById(R.id.loopConf);
        conf.setBackgroundColor(Color.WHITE);
        conf.setTextColor(Color.rgb(65,105,255));
        Button btn2 = (Button)findViewById(R.id.loopPlay);
        btn2.setOnClickListener(startLoop);
        btn2.setBackgroundColor(Color.WHITE);
        btn2.setTextColor(Color.rgb(65,105,255));
        Button btn3 = (Button)findViewById(R.id.loopStop);
        btn3.setBackgroundColor(Color.WHITE);
        btn3.setTextColor(Color.rgb(65,105,255));
        btn3.setOnClickListener(stopLoop);
        Button btn4 = (Button)findViewById(R.id.loopRecord);
        btn4.setBackgroundColor(Color.WHITE);
        btn4.setTextColor(Color.rgb(65,105,255));
        initLoopSound();
        listView.setOnItemClickListener(new OnItemClickListener() {

        	    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg)   {
        		 Object listItem = listView.getItemAtPosition(position);
        		Toast.makeText(getApplicationContext(), "Selected Item is "+ position + ": " + listItem, Toast.LENGTH_SHORT).show();
        	}

			
        	});
        
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
       	 public boolean onItemLongClick(AdapterView<?> av, View v, final int pos, long id) 
            { 
       		 Object listItem = listView.getItemAtPosition(pos);
                Toast.makeText(LoopActivity .this, "The long clicked item is " + pos, Toast.LENGTH_LONG).show();
               
                alertDialogBuilder = new AlertDialog.Builder(LoopActivity.this);
        	 	alertDialogBuilder.setTitle("Delete item");
        	 	alertDialogBuilder.setMessage("Are you sure?");
        	 	alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog,int id) {
        				 arr_cars.remove(pos);
        	             myadapter.notifyDataSetChanged();
       				}
       			  });
       			alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
       				public void onClick(DialogInterface dialog,int id) {
       						dialog.cancel();
       				}
       			});
       	 
       			AlertDialog alertDialog = alertDialogBuilder.create();
       			alertDialog.show();
               
				return true; //false will also trigger OnItemClick!
            } 
       });
	}
	private void initLoopSound(){
		ArrayList<Sound> soundsLoop = tools.loop.getSound();
		for(int i=0;i<soundsLoop.size();i++){
				myAudio.setSoundStateActive(getBaseContext().getFilesDir().toString()
						+"/"+soundsLoop.get(i).getId(),true);
				tools.loop.setPosVoiceActive(i);
		}
		LoopAdapter myadapter = new LoopAdapter(LoopActivity.this);
        listView.setAdapter(myadapter);
	}
	class LoopAdapter extends BaseAdapter{
    	public String title[];
    	public String description[];
    	public Activity context;
    	public LayoutInflater inflater;
    	private ArrayList<ViewHolder> viewHolder = new ArrayList<ViewHolder>();
    	public LoopAdapter(Activity context) {
    		super();
    		//this.imageId = arr_bitmaps;
    		this.context = context;
    		//this.arr_calllog_name = arr_calllog_name;
    	
    	    this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
    	
    	public ArrayList<Bitmap> getImageId() {
    	    return imageId;
    	}
    	public void setImageId(ArrayList<Bitmap> imageId1) {
    	    imageId = imageId1;
    	}
    	public int getCount() {
    		return tools.loop.size();
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
    		TextView txtTitle;
    		TextView txtDescr;
    		CheckBox actif;
    		CheckBox rec;
    		CheckBox loop;
    		Button btn;
    	}
    	public View getView(final int position, View convertView, ViewGroup parent) {
    		final ViewHolder holder;
    		final String id = getBaseContext().getFilesDir().toString()
					+"/"+tools.loop.getSound(position).getId();
    		if(convertView==null)
    		{
    			holder = new ViewHolder();
    			convertView = inflater.inflate(R.layout.loop_pist_empty, null);
    			holder.image = (ImageView) convertView.findViewById(R.id.loopimg);
    			holder.txtTitle = (TextView) convertView.findViewById(R.id.LoopTitre);
    			holder.txtDescr = (TextView) convertView.findViewById(R.id.loopDescription);
    			holder.btn = (Button) convertView.findViewById(R.id.loopUpload);
    			holder.actif = (CheckBox) convertView.findViewById(R.id.LooplistPlayPause1);
    			holder.rec = (CheckBox) convertView.findViewById(R.id.loopRec);
    			holder.loop = (CheckBox) convertView.findViewById(R.id.loopLoop);
    			this.viewHolder.add(holder);
    			convertView.setTag(holder);
    		}
    		else
    			holder=(ViewHolder)convertView.getTag();

    		holder.image.setImageBitmap(getImageId().get(position));
    		holder.txtTitle.setText(position +"."+tools.loop.getSound().get(position).getName());
    		holder.btn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					showDialogLOOP(id,position);
					/**
					tools.currentpostionloopChoose = position;
					Intent intent = new Intent(LoopActivity.this,LoopCheckboxChooseActivity.class);
					System.out.println(tools.loop.size());
	                startActivity(intent);*/
				 	//Toast.makeText(getApplicationContext(), "You have deleted row No. "+ position, Toast.LENGTH_SHORT).show();*/
				}
			});
    		System.out.println(tools.loop.isVoice(position) +"is voice" + position);
    		if(!tools.loop.isVoice(position)){
    			holder.loop.setChecked(true);
    			holder.actif.setChecked(true);
    			myAudio.setSoundStateLoop(id,true);
    			myAudio.setSoundStateActive(id,true);
    			holder.rec.setVisibility(View.INVISIBLE);;
    		}
    		else{
    			holder.actif.setVisibility(View.INVISIBLE);
    		}
    		holder.loop.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					if(holder.loop.isChecked())		
						myAudio.setSoundStateLoop(id, true);
					else
						myAudio.setSoundStateLoop(id,false);
				}});
    		holder.actif.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(holder.actif.isChecked())
							myAudio.setSoundStateActive(id, true);
					else
						myAudio.setSoundStateActive(id,false);
				}});
    		holder.rec.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(holder.rec.isChecked()){
						System.out.println(tools.loop.isVoice(position)+"  is voice");
						// decheck les autres piste audio 
						if(tools.loop.isVoice(position)){
								// desactiver les autres piste audio voice		
								for(int i=0;i<viewHolder.size();i++){
									  if(i !=position && tools.loop.isVoice(i)){
										  myAudio.setSoundStateRec(id,false);
										  viewHolder.get(i).rec.setChecked(false);
										  System.out.println((i !=position && tools.loop.isVoice(i))
												  +"  "+i +"  is no reco");
									  }
								}
								tools.loop.setPosVoiceActive(position);
								Toast.makeText(getApplicationContext(), "Pist voice recoder: "+ position, Toast.LENGTH_SHORT).show();
								myAudio.setSoundStateRec(id,true);
								 System.out.println(id +"  is  reco");
							}
						myAudio.setSoundStateRec(id,true);
						//si le looper is started 
						if(loopstart)
						   myAudio.startLoopVoice(id);	
					}
					else{
						if(tools.loop.isVoice(position)){
							tools.loop.stopPosVoiceRec(position);
							Toast.makeText(getApplicationContext(), "END OF Pist voice recoder : "+ position, Toast.LENGTH_SHORT).show();
							myAudio.setSoundStateRec(id,false);
						}
						else
							myAudio.setSoundStateRec(id,false);
					}
				}
			});
    		return convertView;
    	}
	}
	private void ShowDialogSound(int position,boolean localDl)
	{
		final int position1 = position;
		AlertDialog.Builder builder = 
		        new AlertDialog.Builder(mContext);
		    builder.setTitle("Tracks");
		    CharSequence[] choiceList =null;
		    final ArrayList<Sound> tmp = new ArrayList<Sound>();
		    if(localDl==true){	
		    HashMap<String,Sound> hp =tools.getSoundInDatabase().getSounds();
			Iterator i = hp.entrySet().iterator();
			int k =0;
			while(i.hasNext()){
				Map.Entry entry = (Map.Entry) i.next();
				Sound sound = (Sound) entry.getValue();
				tmp.add(sound);
				k++;
			}
			
			 choiceList = new CharSequence[k]; 
			for(int j=0;j<k;j++){
				 choiceList[j]=(j+1)+"."+tmp.get(j).getName();
			}
		    }
		    else{}
		    
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
		            	if(tmp != null){
		            	Sound f = tmp.get(selected);
		            	//set loop sound 
		            	myAudio.eraseSoundVoiceLoope(link+"/"+f.getId());
		        		myAudio.setLoopUIDSound(link+"/"+f.getId());
		            	tools.loop.addSound(position1,f); 
		            	//ArrayList<Bitmap> arr_bitmaps = new ArrayList<Bitmap>();
		            	imageId.add(BitmapFactory.decodeResource(getResources(), R.drawable.ic_music));
		            	//tools.soundsAviable.remove(position1);
		            	//tools.soundsAviable.add(position1,f);
		            	//tools.id_loopSound.remove(position1);
		            	//tools.id_loopSound.add(position1,f.getId());
		        		LoopAdapter myadapter = new LoopAdapter(LoopActivity.this);
		                listView.setAdapter(myadapter);
		            	}
		                
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
	private void ShowDialogLoadingSound(int position)
	{
		final int position1 = position;
		AlertDialog.Builder builder = 
		        new AlertDialog.Builder(mContext);
		    builder.setTitle(getString(R.string.download));
		   
			
			final CharSequence[] choiceList = {getString(R.string.local),getString(R.string.distance)}; 
		
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
		            	//if(which == 0)
		            	ShowDialogSound(position1,true);
		            	//else
		            		//ShowDialogSound(position1,false);
		                
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
		            	Sound s = tools.getDefauldSound();
		            	File f = mContext.getFilesDir();
		            	myAudio.saveLoopFile(f.toString()+"/"+s.getId());
		            	tools.getSoundInDatabase().addSound(s);
		            	Intent intent = new Intent(LoopActivity.this,MenuActivity.class);
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
	private void showDialogLOOP(String uid,final int position){
		final String uid_local = uid;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

			// set title
			alertDialogBuilder.setTitle(getString(R.string.setting));

			// set dialog message
			alertDialogBuilder
				.setMessage(getString(R.string.waiting))
				.setCancelable(false)
				.setPositiveButton("DownLoad music",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						tools.currentpostionloopChoose = position;
						Intent intent = new Intent(LoopActivity.this,LoopCheckboxChooseActivity.class);
						System.out.println(tools.loop.size());
		                startActivity(intent);
					}
				  })
				.setNegativeButton("Remove",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						//tools.getSoundInDatabase().deleteSound(uid_local);
						//tools.getNativeAudio().detele(uid_local);
						
						tools.deleteInObservateur(uid_local);
						tools.loop.removeSound(position);
						Intent intent = new Intent(LoopActivity.this,LoopActivity.class);
						startActivity(intent);
					}
				})
				.setNeutralButton(getString(R.string.cancel_task),new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				}
				);

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
			}	
}