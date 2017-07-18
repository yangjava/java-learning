package com.java.dbutils;

public class Card {

	private String cardid;
	
	private int id;
	
	//说明
	private String  note;

	public String getCardid() {
		return cardid;
	}

	public void setCardid(String cardid) {
		this.cardid = cardid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return "Card [cardid=" + cardid + ", id=" + id + ", note=" + note + "]";
	}

  
	
	
}
