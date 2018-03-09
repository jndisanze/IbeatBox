package be.umons.ibeatbox.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import be.umons.ibeatbox.R;
import be.umons.ibeatbox.activity.ListPlayActivity.LoopAdapter;
import be.umons.ibeatbox.activity.ListPlayActivity.LoopAdapter.ViewHolder;
import be.umons.ibeatbox.databases.ForumDatabase;
import be.umons.ibeatbox.forum.Message;
import be.umons.ibeatbox.main.Chat;
import be.umons.ibeatbox.main.Sound;
import be.umons.ibeatbox.main.Tools;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity {
	private Tools tools;
	private Chat chat;
	private Button send;
	private EditText mymessage;
	private Context context;
	
	@Override
	public void onBackPressed (){
		Intent intent = new Intent(ChatActivity.this,MenuActivity.class);
		startActivity(intent);
	}
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        tools = (Tools)getApplicationContext();
        context = getBaseContext();
        listview = (ListView) findViewById(R.id.chatlistView);
        mymessage = (EditText) findViewById(R.id.mymessage);
        send = (Button) findViewById(R.id.send);
        initPlaySound();
        View.OnClickListener sendMessage = new OnClickListener() {
            public void onClick(View v) {
            	if(mymessage.length()>1){
            	 //tools.getForumDatabase().setMessage(new Message(tools.getUidMobile(),mymessage.getText().toString()));
            		ForumDatabase data = new ForumDatabase(new Message(tools.getUidMobile(),
            				mymessage.getText().toString()),"0",context);
            		data.execute(tools.getStrurl());
            	Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
               startActivity(intent);	 
            	}
            }
        };
        send.setOnClickListener(sendMessage);
   }
	private ListView listview;
	private void initPlaySound(){
		ForumDatabase data = new ForumDatabase(null,"1",context);
		data.execute(tools.getStrurl());
		while(!data.isFinish()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ArrayList<Message> msgs =data.getChat().getMessage();
		Log.d("g",msgs.size()+"");
		 final ArrayList<String> list = new ArrayList<String>();
		    for (int i = 0; i < msgs.size(); ++i) {
		    	Log.d(msgs.get(i).getMessage(), i+"");  	
		      list.add(msgs.get(i).getId()+"\n"+msgs.get(i).getMessage());
		    }
		final StableArrayAdapter adapter = new StableArrayAdapter(context,
		        android.R.layout.simple_list_item_1,list);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		      @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			public void onItemClick(AdapterView<?> parent, final View view,
		          int position, long id) {
		        final String item = (String) parent.getItemAtPosition(position);
		        view.animate().setDuration(2000).alpha(0)
		            .withEndAction(new Runnable() {
		              public void run() {
		                //list.remove(item);
		                adapter.notifyDataSetChanged();
		                view.setAlpha(1);
		              }
		          });
		    }
		    });
		  }
	}
class StableArrayAdapter extends ArrayAdapter<String> {

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

    public StableArrayAdapter(Context context, int textViewResourceId,
        ArrayList<String> msgs) {
      super(context, textViewResourceId, msgs);
      for (int i = 0; i < msgs.size(); ++i) {
    	Log.d(msgs.get(i), i+"");  
        mIdMap.put(msgs.get(i), i);
      }
    }
    public long getItemId(int position) {
      String item = getItem(position);
      return mIdMap.get(item);
    }
    public boolean hasStableIds() {
      return true;
    }

  }
	
	
	