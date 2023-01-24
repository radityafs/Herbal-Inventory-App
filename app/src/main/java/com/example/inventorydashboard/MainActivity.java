package com.example.inventorydashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventorydashboard.adapter.DashboardAdapter;
import com.example.inventorydashboard.model.DataItem;
import com.example.inventorydashboard.model.ProductModel;
import com.example.inventorydashboard.model.ResponseDelete;
import com.example.inventorydashboard.model.SummaryModel;
import com.example.inventorydashboard.retrofit.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView addButton;
    private TextView sum_product, sum_stock, sum_category;
    private EditText search_box;
    private RecyclerView recyclerView;
    private List<DataItem> productList = new ArrayList<>();
    private DashboardAdapter dashboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.btn_tambah);
        sum_product = findViewById(R.id.sum_product);
        sum_stock = findViewById(R.id.sum_stock);
        sum_category = findViewById(R.id.sum_category);
        recyclerView = findViewById(R.id.recyclerView);
        search_box = findViewById(R.id.search_box);

        search_box.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = search_box.getText().toString();
                searchDataProduct(query);
                return true;
            }
            return false;
        });

        addButton.setOnClickListener(this);

        getSummary();
        setRecyclerView();
        getDataProduct();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_tambah) {
            Intent intent = new Intent(MainActivity.this, ProductActivity.class);
            startActivity(intent);
        }
    }

    public void setRecyclerView() {
        dashboardAdapter = new DashboardAdapter(productList, new DashboardAdapter.onAdapterListener() {
            @Override
            public void onClick(String type, DataItem dataItem) {
                if (type.equals("edit")) {
                    Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                    intent.putExtra("id", dataItem.getId());
                    startActivity(intent);
                } else if (type.equals("delete")) {
                    deleteDataProduct(dataItem.getId());
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(dashboardAdapter);
    }

    public void searchDataProduct(String keyword) {
        if(keyword.isEmpty()) {
            getDataProduct();
        } else {
            ApiService.endPoint().getProductSearch(keyword).enqueue(new Callback<ProductModel>() {
                @Override
                public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Search Success", Toast.LENGTH_SHORT).show();
                        productList.clear();
                        productList.addAll(response.body().getData());
                        dashboardAdapter.setData(productList);
                    }
                }

                @Override
                public void onFailure(Call<ProductModel> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Search Errro", Toast.LENGTH_SHORT).show();

                    Log.d("Error", t.getMessage());
                }
            });
        }
    }

    public void deleteDataProduct(int id) {
        ApiService.endPoint().deleteProduct(id).enqueue(new Callback<ResponseDelete>() {
            @Override
            public void onResponse(Call<ResponseDelete> call, Response<ResponseDelete> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                    getDataProduct();
                    getSummary();
                } else {
                    Toast.makeText(MainActivity.this, "Data gagal dihapus", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDelete> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Data gagal dihapus", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDataProduct(){
        ApiService.endPoint().getProduct().enqueue(new Callback<ProductModel>() {
            @Override
            public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {
                if (response.isSuccessful()) {
                    productList = response.body().getData();
                    dashboardAdapter.setData(productList);

                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getSummary() {
        ApiService.endPoint().getSummary().enqueue(new Callback<SummaryModel>() {
            @Override
            public void onResponse(Call<SummaryModel> call, Response<SummaryModel> response) {
                if (response.isSuccessful()) {
                    String product = String.valueOf(response.body().getData().getCountProduct());
                    String stock = String.valueOf(response.body().getData().getCountStock());
                    String category = String.valueOf(response.body().getData().getCountCategory());

                    sum_product.setText(product);
                    sum_stock.setText(stock);
                    sum_category.setText(category);
                } else {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SummaryModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}