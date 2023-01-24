package com.example.inventorydashboard.model;

import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("count_category")
	private int countCategory;

	@SerializedName("count_product")
	private int countProduct;

	@SerializedName("count_stock")
	private String countStock;

	public void setCountCategory(int countCategory){
		this.countCategory = countCategory;
	}

	public int getCountCategory(){
		return countCategory;
	}

	public void setCountProduct(int countProduct){
		this.countProduct = countProduct;
	}

	public int getCountProduct(){
		return countProduct;
	}

	public void setCountStock(String countStock){
		this.countStock = countStock;
	}

	public String getCountStock(){
		return countStock;
	}

	@Override
 	public String toString(){
		return 
			"Data{" + 
			"count_category = '" + countCategory + '\'' + 
			",count_product = '" + countProduct + '\'' + 
			",count_stock = '" + countStock + '\'' + 
			"}";
		}
}