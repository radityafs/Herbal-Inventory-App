package com.example.inventorydashboard.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CategoryModel{

	@SerializedName("data")
	private List<Category> data;

	@SerializedName("status")
	private String status;

	public void setData(List<Category> data){
		this.data = data;
	}

	public List<Category> getData(){
		return data;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"CategoryModel{" + 
			"data = '" + data + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}