package be.umons.ibeatbox.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.util.Log;
import be.umons.ibeatbox.forum.Message;

public class Chat {

	private ArrayList<Message> msgs = new  ArrayList<Message>();
public void setMessage(Message msg){
Date date = new Date();	
   //Log.d(msg.getId(), msg.getMessage()); 
   msgs.add(msg);	
}
public  ArrayList<Message> getMessage(){
	return msgs;
}

}