package com.tripsta.models;

/**
 * Created by georgiossarris on 15/07/16.
 */

public class Card {

	private int number;
	private int colorHex;


	public Card(int number, int colorHex) {
		this.number = number;
		this.colorHex = colorHex;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getColorHex() {
		return colorHex;
	}

	public void setColorHex(int colorHex) {
		this.colorHex = colorHex;
	}
}
