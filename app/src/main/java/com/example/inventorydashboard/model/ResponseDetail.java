package com.example.inventorydashboard.model;

import com.google.gson.annotations.SerializedName;

public class ResponseDetail{

	@SerializedName("data")
	private DataItem data;

	@SerializedName("status")
	private String status;

	public DataItem getData(){
		return data;
	}

	public String getStatus(){
		return status;
	}
}