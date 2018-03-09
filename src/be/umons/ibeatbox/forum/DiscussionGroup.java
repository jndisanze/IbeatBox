package be.umons.ibeatbox.forum;
/**
import java . util . ArrayList ;

public class DiscussionGroup implements Explorable {

	private ArrayList < Message > posts ;
	public DiscussionGroup ( Message p )
	{
		 posts = new ArrayList < Message >();
		posts . add ( p ); }
	public void accept ( ExplorerEntity ee ){
	ee.explore ( this );
	for ( Message p : posts ) p.accept( ee ); 
		}
}
*/