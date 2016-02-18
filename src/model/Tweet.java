package model;

public class Tweet {
	private String user;
	private String text;
	
	private int polarity; 

	Tweet(){
		this.polarity = 0; //default value negative
	}
	
	public Tweet(String us, String txt, int pol){
		this.user = us;
		this.text = txt;
		this.polarity = pol;
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getPolarity() {
		return polarity;
	}

	public void setPolarity(int polarity) {
		this.polarity = polarity;
	}
}
