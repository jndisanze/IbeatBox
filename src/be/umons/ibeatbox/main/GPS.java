package be.umons.ibeatbox.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

/**
 * Cette classe décrit le GPS, utile pour localiser l'utilisateur.
 */
public class GPS implements LocationListener {
	private LocationManager manager;
	private String provider;
	private boolean enable;
	private Activity activity;
	private ProgressDialog progress;
	private double latitude;
	private double longitude;
	
	/**
	 * Constructeur.
	 * @param activity Une activité alertée à chaque top GPS.
	 * @see MCGProject.umons.activity.Notifiable
	 */
	public GPS(Activity activity) {
		this.activity = activity;
		this.manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		this.provider = LocationManager.GPS_PROVIDER;
		this.enable = false;
	}
	
	/**
	 * @see android.location.LocationListener#onLocationChanged(Location)
	 */
	public void onLocationChanged(Location location) {
		latitude =(Double) location.getLatitude();
		longitude =(Double)location.getLongitude();
		//last_position = new GeoPoint(latitude, longitude);
		//activity.update(last_position);
	}
	public double getLongitude(){
		return this.longitude;
	}
	public double getLatitude(){
		return this.latitude;
	}
	/**
	 * @see android.location.LocationListener#onProviderDisabled(String)
	 */
	public void onProviderDisabled(String provider) {
		if(this.provider.equals(provider)) {
			AlertDialog dialog = new AlertDialog.Builder(activity).create();
			dialog.setTitle("Erreur");
			dialog.setMessage("Le GPS n'est pas activé, cette activité ne peut-être accédée.");
			dialog.setOnCancelListener(new CancelAction());
			dialog.show();
		}
	}
	
	/**
	 * @see android.location.LocationListener#onProviderEnabled(String)
	 */
	public void onProviderEnabled(String provider) {
		//Rien à faire ici.
	}
	
	/**
	 * @see android.location.LocationListener#onStatusChanged(String, int, Bundle)
	 */
	public void onStatusChanged(String provider, int status, Bundle extra) {
		if(!this.provider.equals(provider) && status != LocationProvider.AVAILABLE)
			progress = ProgressDialog.show(activity, "Please wait", "Searching for GPS signal", true, true, new CancelAction());
		else if(this.provider.equals(provider) && status == LocationProvider.AVAILABLE && progress != null)
			progress.cancel();
	}
	
	/**
	 * Permet de savoir si l'accès au GPS est permis.
	 * @return True si l'accès est possible, False sinon.
	 */
	public boolean isEnable() {
		return enable;
	}
	
	/**
	 * Permet d'obtenir la dernière position enregistrée.
	 * @return La dernière position enregistrée.
	 
	public GeoPoint getPosition() {
		return last_position;
	}*/
	
	/**
	 * Permet de définir l'activité à alerter à chaque top GPS.
	 * @param activity Une activité Notifiable.
	 *
	public void setNotifiable(Notifiable activity) {
		this.activity = activity;
	}
	*/
	/**
	 * Permet de débuter l'activité du GPS.
	 */
	public void startUpdates() {
		manager.requestLocationUpdates(this.provider, 10000, 15, this);
	}
	
	/**
	 * Permet de stopper l'activité du GPS.
	 */
	public void stopUpdates() {
		manager.removeUpdates(this);
	}
	
	/**
	 * Permet de personnaliser l'action cancel d'un dialogue.
	 * @see DialogInterface.OnCancelListener
	 */
	private class CancelAction implements DialogInterface.OnCancelListener {

		/**
		 * Permet de contrôler les actions effectuées lors d'un cancel.
		 * @param dialog Le dialogue qui subit le cancel.
		 */
		public void onCancel(DialogInterface dialog) {
			activity.onBackPressed();
		}
		
	}

}
