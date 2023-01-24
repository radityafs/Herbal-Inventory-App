package com.example.inventorydashboard.retrofit;

import com.example.inventorydashboard.model.CategoryModel;
import com.example.inventorydashboard.model.ProductModel;
import com.example.inventorydashboard.model.ResponseDelete;
import com.example.inventorydashboard.model.ResponseDetail;
import com.example.inventorydashboard.model.SummaryModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiEndpoint {
    @GET("summary")
    Call<SummaryModel> getSummary();

    @GET("list")
    Call<ProductModel> getProduct();

    @GET("category")
    Call<CategoryModel> getCategory();

    @GET("list")
    Call<ProductModel> getProductSearch(@Query("search") String search);

    @Multipart
    @POST("create")
    Call<ResponseDetail> postProduct(
            @Part MultipartBody.Part photo,
            @Part("name") RequestBody name,
            @Part("category_id") RequestBody category_id,
            @Part("stock") RequestBody stock,
            @Part("price") RequestBody price,
            @Part("description") RequestBody description
    );

    @Multipart
    @POST("update/{id}")
    Call<ResponseDetail> updateProduct(
            @Path("id") String id,
            @Part("name") RequestBody name,
            @Part("category_id") RequestBody category_id,
            @Part("stock") RequestBody stock,
            @Part("price") RequestBody price,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part photo
    );

    @GET("detail/{id}")
    Call<ResponseDetail> getProductDetail(@Path("id") String id);

    @POST("delete/{id}")
    Call<ResponseDelete> deleteProduct(@Path("id") int id);
}
