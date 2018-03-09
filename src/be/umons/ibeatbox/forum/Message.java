package be.umons.ibeatbox.forum;
public class Message{
	private String id;
	private String message;

	public Message(String id,String message){
		this.setId(id);
		this.setMessage(message);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}