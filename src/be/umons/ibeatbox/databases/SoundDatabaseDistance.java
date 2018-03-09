package be.umons.ibeatbox.databases;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import be.umons.ibeatbox.main.LicenceCCBYX;
import be.umons.ibeatbox.main.Sound;
import be.umons.ibeatbox.main.SoundObserveur;
import be.umons.ibeatbox.main.Tools;

public class SoundDatabaseDistance implements SoundObserveur{
	private Context context;
	private ArrayList<String> id_sound = new ArrayList<String>();
	private ArrayList<String> name = new ArrayList<String>();
	private Thread thread;
	public SoundDatabaseDistance(Context context,String url){
		this.context = context;
		///((Tools)getApplicationContext()).getStrurl();
		strUrl = url;
	}
	private  String strUrl;// ="http://10.2.35.195/ibeatbox/soundDatabase.php";
	private boolean isFinish;
	//private static final String strUrl ="http://asbrussels.funpic.org/asb_web/soundDatabase.php";
	private void setSound(Sound newSound){
		 // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(this.strUrl);	
	    String filename = (context.getFilesDir().toString())+"/"+newSound.getId();
        System.out.println(newSound);
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(13);
	        nameValuePairs.add(new BasicNameValuePair("operation","0"));
	        nameValuePairs.add(new BasicNameValuePair("id",newSound.getId()));
	        nameValuePairs.add(new BasicNameValuePair("name",newSound.getName()));
	        nameValuePairs.add(new BasicNameValuePair("tag",newSound.getTAG_SOUND()));
	        nameValuePairs.add(new BasicNameValuePair("long","0."));
	        nameValuePairs.add(new BasicNameValuePair("lat","0."));
	        nameValuePairs.add(new BasicNameValuePair("score",newSound.getRating()+""));
	        nameValuePairs.add(new BasicNameValuePair("time",newSound.getTime()+""));
	        nameValuePairs.add(new BasicNameValuePair("signature",
	        		newSound.getSignatureRythmNum()+"/"+newSound.getSignatureRythmDen()));
	        nameValuePairs.add(new BasicNameValuePair("tempo",newSound.getTempo()+""));
	        nameValuePairs.add(new BasicNameValuePair("mesure",newSound.getMesure()+""));
	        nameValuePairs.add(new BasicNameValuePair("licence",newSound.getLicence().getLicence()));
	        BufferedReader read=new BufferedReader(new FileReader(filename));
	        String line=read.readLine();
	        nameValuePairs.add(new BasicNameValuePair("data",line));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        // Execute HTTP Post Request
	        //System.out.println(line);
	        HttpResponse response = httpclient.execute(httppost);
	        System.out.println(response.getEntity());
	        System.out.println("request send");
	        
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	AlertDatabases.alertMessage(context, "internet desable.pleas enable your internet");
	    }
	    catch(Exception ex)
	    {
	    System.out.println(filename+" file error" + ex);
	    }
	}
	public JSONArray doRequest(String request) throws ClientProtocolException, IOException, JSONException{
		HttpPost httppost = new HttpPost(this.strUrl);
    	List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);
    	nvp.add(new BasicNameValuePair("operation","1"));
		nvp.add(new BasicNameValuePair("request", request));
    	httppost.setEntity(new UrlEncodedFormEntity(nvp));
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpResponse response = httpclient.execute(httppost);
    	BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    	StringBuffer sbf = new StringBuffer();
    	String line;
    	while((line = reader.readLine()) != null){
    		sbf.append(line + "\n");
    	}
    	if(thread != null)
    		thread.interrupted();
    	return new JSONArray(sbf.toString());
	}
	public Sound getSound(String id) throws NullPointerException{
		Sound newSound = null;
		try {
			this.setFinish(false);
			  name = new ArrayList<String>();
			  id_sound = new ArrayList<String>();
			String request = "SELECT * FROM id_sound where id = "+id+" ;";
			String request1 = "SELECT * FROM sound_configuration where id = "+id+" ;";
			String request2 = "SELECT * FROM soundData where id = "+id+" ;";
			///String request3 = "SELECT * FROM soundData where id = "+id+" ;";
			JSONArray response = doRequest(request);
			JSONArray response1 = doRequest(request1);
			JSONArray response2 = doRequest(request2);
			if(response.length()>0) {
				JSONObject line = response.getJSONObject(0);
				newSound = new Sound(line.getString("id"),line.getString("name"));
				if(response1.length()>0) {
					line = response1.getJSONObject(0);
					String sign = line.getString("signature");
					String [] tmp = sign.split("/");
					if(tmp.length>=2){
					newSound.setSignatureRythmNum(Integer.parseInt(tmp[0]));
					newSound.setSignatureRythmDen(Integer.parseInt(tmp[1]));
					}
					newSound.setTempo(Integer.parseInt(line.getString("tempo")));
					newSound.setMesure(Integer.parseInt(line.getString("mesure")));
					newSound.setLicence(new LicenceCCBYX(line.getString("licence"),"http://creativecommons.org/"));
				}
				if(response2.length()>0){
					line = response2.getJSONObject(0);
					String dataSound = line.getString("data");
					String filename = (context.getFilesDir())+"/"+newSound.getId();
					BufferedWriter wt=new BufferedWriter(new FileWriter(filename));
					wt.write(dataSound);
				}
				
			}
			this.setFinish(true);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			AlertDatabases.alertMessage(context, "internet desable.pleas enable your internet");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			AlertDatabases.alertMessage(context, "internet desable.pleas enable your internet");
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			AlertDatabases.alertMessage(context, "Error data");
			e.printStackTrace();
		}
		return newSound;
	}
	public ArrayList<String> getInfoSound(){
		try {
			  name = new ArrayList<String>();
			  id_sound = new ArrayList<String>();
			String request = "SELECT * FROM id_sound;";
			JSONArray response = doRequest(request);
			
			for(int i = 0; i < response.length(); i++) {
				JSONObject line = response.getJSONObject(i);
				id_sound.add(line.getString("id"));
				id_sound.add(line.getString("name"));
			}
			return name;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			AlertDatabases.alertMessage(context, "internet desable.pleas enable your internet");
			System.out.println("1" +e);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("2" +e);
			AlertDatabases.alertMessage(context, "internet desable.pleas enable your internet");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("3" +e);
			// TODO Auto-generated catch block
			AlertDatabases.alertMessage(context, "error data");
			e.printStackTrace();
		}
		return this.name;
	}
	public void update(Sound sound) {
		final Sound newSound = sound;
		new Thread(new Runnable() {

			public void run() {
				setSound(newSound);
				
			}}).start();
		
	}
	public void detele(String uid) {
		// TODO Auto-generated method stub
		
	}
	public ArrayList<String> getSoundsBykeyTag(String keyTag){
		final String req ="Select * from id_sound where tag = '"+keyTag+"' or name = '"+keyTag+"';";
		System.out.println(req);
		final ArrayList<String> res = new ArrayList<String>();
		thread = new Thread(new Runnable(){

			public void run() {
				try {
					JSONArray response =doRequest(req);
					System.out.println("request send");
					for(int i = 0; i < response.length(); i++) {
						System.out.println("request receive");
						hasResultatMysql=true;
						JSONObject line = response.getJSONObject(i);
						id_sound.add(line.getString("id"));
						res.add(line.getString("name"));
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					AlertDatabases.alertMessage(context, "internet desable.pleas enable your internet");
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					AlertDatabases.alertMessage(context, "internet desable.pleas enable your internet");
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}});
		 startRequest(thread);
		return res;
	}
	private void startRequest(Thread t){
		thread.start();
		int i = 0;
		while(!this.hasResultatMysql || i<5){
			try {
				Thread.sleep(400);
				++i;
			} catch (InterruptedException e) {
				i = 5;
				e.printStackTrace();
			}
		}
		thread.interrupt();
		
	}
	private boolean hasResultatMysql = false;
	public void updateScore(String uid, Double score) {
		// TODO Auto-generated method stub	
	}
	public boolean isFinish() {
		return isFinish;
	}
	private void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}
}