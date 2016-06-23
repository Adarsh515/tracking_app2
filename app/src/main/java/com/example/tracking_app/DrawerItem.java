package com.example.tracking_app;

public class DrawerItem {

	String ItemName;
	int imgResID;
	
	public DrawerItem(int imgResID,String itemName) {
		super();
		this.imgResID = imgResID;
		ItemName = itemName;
		
	}
	
	public String getItemName() {

		return ItemName;
	}
	public void setItemName(String itemName) {

		ItemName = itemName;
	}


	public int getImgResID() {
		return imgResID;
	}
	public void setImgResID(int imgResID) {
		this.imgResID = imgResID;
	}

	
	
}
