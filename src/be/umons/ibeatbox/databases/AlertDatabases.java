package be.umons.ibeatbox.databases;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
public class AlertDatabases {
	public static void alertMessage(Context context,String message){
	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
			context);
	 
		// set title
		alertDialogBuilder.setTitle("Alert");

		// set dialog message
		alertDialogBuilder
			.setMessage(message)
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			  })
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
				}
			});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		}
	
  }
