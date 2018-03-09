package be.umons.ibeatbox.forum;
/**
import java.util.ArrayList;
public class Censure implements ExplorerEntity {
private ArrayList<String> blacklist;
public Censure(){
	blacklist = new ArrayList<String>();
	blacklist.add("sexe");
	blacklist.add("tom");
	blacklist.add("bite");
	blacklist.add("porno");
}
	public void explore(DiscussionGroup dg) {
		// TODO Auto-generated method stub

	}

	public void explore(Message p) {
		// TODO Auto-generated method stub
	for(int i = 0;i<blacklist.size();i++){
			p.setAuthor(p.getAuthor().replaceAll(blacklist.get(i) ,"***"));
			p.setTitle(p.getTitle().replaceAll(blacklist.get(i) ,"***"));
			p.setMessage(p.getMessage().replaceAll(blacklist.get(i) ,"***"));
	}	
	System . out . println ( p . getTitle () + " ( " + p . getAuthor () + " ) " + p.getMessage()); 

	}
	public void explore(Object aDiscussionGroup_dg) {
		// TODO Auto-generated method stub
		
	}
	public void operation() {
		// TODO Auto-generated method stub
		
	}

}*/