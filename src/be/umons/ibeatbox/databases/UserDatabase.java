package be.umons.ibeatbox.databases;

import java.io.BufferedReader;
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

import be.umons.ibeatbox.main.GPS;
import be.umons.ibeatbox.main.Tools;
import be.umons.ibeatbox.main.User;
import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;

public class UserDatabase {
	private static final String strUrl ="http://localhost/ibeatbox/personne.php";
	private String android_id; 
	private User user = null;
	private Context context;
	public UserDatabase(Context context) {
		this.android_id = Secure.getString(context.getContentResolver(),
                        Secure.ANDROID_ID);
		this.context = context;
	}
	public String getUniqueID(){
		return this.android_id;
	}
	/**
	 * Permet de savoir si un nom d'utilisateur et un mot de passe sont bien formatés.
	 * @param username Le nom d'utilisateur à tester.
	 * @param password Le mot de passe à tester.
	 * @return Vrai si bien formatés, faux sinon. 
	 */
	public boolean checkValidity(String username, String password){
		return isCorrectUsername(username) && isCorrectPassword(password);
	}
	/**
	 * Permet de savoir si un nom d'utilisateur est correctement formaté.
	 * @param username Le nom d'utilisateur à tester.
	 * @return Vrai si bien formaté, faux sinon.
	 */
	private boolean isCorrectUsername(String username){
		if(username == null) return false;
		else{
			int length = username.length();
			return length > 3 && length < 21 && username.matches("[a-zA-Z0-9]{" + length + "}");
		}
	}
	
	/**
	 * Permet de savoir si un mot de passe est correctement formaté.
	 * @param password Le mot de passe à tester.
	 * @return Vrai si bien formaté, faux sinon.
	 */
	public boolean isCorrectPassword(String password){
		if(password == null) return false;
		else{
			int length = password.length();
			return length > 5 && length < 21 && password.matches("[a-zA-Z0-9]{" + length + "}"); 
		}
	}
	public void setUser(User newUser){
		 // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(this.strUrl);
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
	        nameValuePairs.add(new BasicNameValuePair("id",this.android_id));
	        nameValuePairs.add(new BasicNameValuePair("name",newUser.getName()));
	        nameValuePairs.add(new BasicNameValuePair("long",newUser.getLong()+""));
	        nameValuePairs.add(new BasicNameValuePair("lat",newUser.getLat()+""));
	        nameValuePairs.add(new BasicNameValuePair("pass",newUser.getPassword()));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }

		user = newUser;
	}
	public User getUser(){
		return user;
	}
	public User getServerData() {
	 InputStream is = null;
	 String result = "";
	 ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	 nameValuePairs.add(new BasicNameValuePair("id","1225699874"));
			 
	 // Envoie de la commande http
	 try{
		 HttpClient httpclient = new DefaultHttpClient();
		 HttpPost httppost = new HttpPost(strUrl);
		 httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		 HttpResponse response = httpclient.execute(httppost);
		 HttpEntity entity = response.getEntity();
		 is = entity.getContent();
		 
	 	}catch(Exception e){
	 		Log.e("log_tag", "Error in http connection " + e.toString());
	 	}
	 
	 // Convertion de la requête en string
	 try{
		 BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		 StringBuilder sb = new StringBuilder();
		 String line = null;
		 while ((line = reader.readLine()) != null) {
			 sb.append(line + "\n");
		 }
		 is.close();
		 result=sb.toString();
	 }catch(Exception e){
		 Log.e("log_tag", "Error converting result " + e.toString());
	 }
	 // Parse les données JSON
	 try{
		 JSONArray jArray = new JSONArray(result);
		 for(int i=0;i<jArray.length();i++){
			 JSONObject json_data = jArray.getJSONObject(i);
			 // Affichage ID_ville et Nom_ville dans le LogCat
			// Log.i("log_tag","ID_ville: "+json_data.getInt("ID_ville")+
				//	 ", Nom_ville: "+json_data.getString("Nom_ville")
					// );
			 // Résultats de la requête
			 user = new User(json_data.getString("name"),json_data.getString("pass"),this.android_id);
			 //returnString += "\n\t" + jArray.getJSONObject(i);
		 }
	 }catch(JSONException e){
		 Log.e("log_tag", "Error parsing data " + e.toString());
	 }
	 return user;
	}
}	