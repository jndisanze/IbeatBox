package be.umons.ibeatbox.databases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;


import android.annotation.TargetApi;
import java.io.ByteArrayOutputStream;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import be.umons.ibeatbox.forum.Message;
import be.umons.ibeatbox.main.Chat;
import be.umons.ibeatbox.main.Tools;

public class ForumDatabase extends AsyncTask<String, Void, String>{
	private String request="SELECT mobile, message FROM  forum  WHERE 1  LIMIT 0 , 40";
	private Tools tools;
	private Context context;
	private Chat chat;
	private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private int NOTIFICATION_ID = 1;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private String content =  null;
    private boolean error = false;
    private boolean finish =false;
    private final HttpClient httpclient = new DefaultHttpClient();
    final HttpParams params = httpclient.getParams();
	private Message message;
	private String action;
	public ForumDatabase (Message message,String action,Context context){
		this.message = message;
		this.context=context;
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		chat = new Chat();
		this.action = action;
	}
	public boolean isFinish(){
		return this.finish;
	}
	public Chat getChat(){
		return this.chat;
	}
	  protected void onPreExecute() {
	        createNotification("Data download is in progress","");
	    }
	  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void createNotification(String contentTitle, String contentText) {
		  
	        //Build the notification using Notification.Builder
		  NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
	        .setSmallIcon(android.R.drawable.stat_sys_download)
	        .setAutoCancel(true)
	        .setContentTitle(contentTitle)
	        .setContentText(contentText);
	 
	        //Get current notification
	        mNotification = builder.build();
	        
	        //Show the notification
	        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
	    }
	private void setNewChat(String url){
		try{
			finish = false;	
		HttpPost httppost = new HttpPost(url);
    	List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);
    	nvp.add(new BasicNameValuePair("operation","1"));
		nvp.add(new BasicNameValuePair("request", request));
    	httppost.setEntity(new UrlEncodedFormEntity(nvp));
    	//HttpClient httpclient = new DefaultHttpClient();
    	HttpResponse response = httpclient.execute(httppost);
    	BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    	StringBuffer sbf = new StringBuffer();
    	String line;
    	while((line = reader.readLine()) != null){
    		sbf.append(line + "\n");
    	}
			JSONArray data = new JSONArray(sbf.toString());
			if(data.length()>0) {
				for(int i=0;i<data.length();++i){
					JSONObject res = data.getJSONObject(i);
					this.chat.setMessage(new Message(res.getString("mobile"),res.getString("message")));
					Log.d(res.getString("mobile"),this.chat.getMessage().size()+"");
				}	
			}
			finish = true;
		}
		catch (ClientProtocolException e) {
			   Log.w("HTTP2:",e );
	           content = e.getMessage();
	           error = true;
	           cancel(true);
		    } catch (IOException e) {
		    	Log.w("HTTP3:",e );
	            content = e.getMessage();
	            error = true;
	            cancel(true);;
		    }
		 catch (Exception e) {
	         Log.w("HTTP4:",e );
	         content = e.getMessage();
	         error = true;
	         cancel(true);
		 }
	}
	private void setMessage(Message message,String url){
		finish = false;
	    HttpPost httppost = new HttpPost(url);
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("operation","2"));
        nameValuePairs.add(new BasicNameValuePair("mobile",message.getId()));
        nameValuePairs.add(new BasicNameValuePair("message",message.getMessage()));
        try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			// Execute HTTP Post Request
	        //System.out.println(line);
	        HttpResponse response = httpclient.execute(httppost);
	        System.out.println(response.getEntity());
	        System.out.println("request message send");
	        StatusLine statusLine = response.getStatusLine();
            //Check the Http Request for success
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                content = out.toString();
                finish = true;
            }
            else{
                //Closes the connection.
                Log.w("HTTP1:",statusLine.getReasonPhrase());
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
		}
	   catch (ClientProtocolException e) {
		   Log.w("HTTP2:",e );
           content = e.getMessage();
           error = true;
           cancel(true);
	    } catch (IOException e) {
	    	Log.w("HTTP3:",e );
            content = e.getMessage();
            error = true;
            cancel(true);;
	    }
	 catch (Exception e) {
         Log.w("HTTP4:",e );
         content = e.getMessage();
         error = true;
         cancel(true);
     }
        
	}
	protected void onCancelled() {
        createNotification("Error occured during data download",content);
    }
    protected void onPostExecute(String content) {
        if (error) {
            createNotification("Data download ended abnormally!",content);
        } else {
            createNotification("Data download is complete!","");
        }
    }
	@Override
	protected String doInBackground(String... urls) {
		  String URL = urls[0];
          HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
          HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
          ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);
          if(action.equals("0")){
        	  setMessage(message,URL);
        	  setNewChat(URL);
          }
          else if(action.equals("1")){
        	  setNewChat(URL);
          }
		return null;
	}
}