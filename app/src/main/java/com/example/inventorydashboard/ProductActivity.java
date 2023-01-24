package com.example.inventorydashboard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.inventorydashboard.model.Category;
import com.example.inventorydashboard.model.CategoryModel;
import com.example.inventorydashboard.model.DataItem;
import com.example.inventorydashboard.model.ResponseDetail;
import com.example.inventorydashboard.retrofit.ApiService;
import com.example.inventorydashboard.utils.RealPathUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener {

    private String id;
    private DataItem productData;
    private List<Category> categoryList;

    private ImageView productImage;
    private EditText product_name, product_stock, product_price, product_description;
    private Spinner category;
    private TextView title;
    private Button btn_action;

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);

        productImage = findViewById(R.id.btn_photo);
        product_name = findViewById(R.id.product_name);
        category = findViewById(R.id.product_category);
        product_stock = findViewById(R.id.product_stock);
        product_price = findViewById(R.id.product_price);
        product_description = findViewById(R.id.product_description);
        title = findViewById(R.id.textTittle);
        btn_action = findViewById(R.id.btnAction);

        productImage.setOnClickListener(this);
        btn_action.setOnClickListener(this);

        loadCategory();

        id = String.valueOf(getIntent().getIntExtra("id", 0));

        if (id.equals("0") != true) {
            loadDataProduct(id);
            title.setText("Edit Product");
            btn_action.setText("Update");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_photo) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        }
        if (v.getId() == R.id.btnAction) {
            if (id.equals("0")) {
                addProduct();
            } else {
                updateProduct(id);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Picasso.get().load(data.getData()).resize(400, 200).centerCrop().into(productImage);

            imagePath = RealPathUtil.getRealPath(ProductActivity.this, data.getData());
        }
    }

    protected boolean formValidation() {
        if (imagePath == null && id.equals("0")) {
            Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (product_name.getText().toString().isEmpty()) {
            product_name.setError("Product name is required");
            return false;
        }
        if (product_stock.getText().toString().isEmpty()) {
            product_stock.setError("Product stock is required");
            return false;
        }
        if (product_price.getText().toString().isEmpty()) {
            product_price.setError("Product price is required");
            return false;
        }
        if (product_description.getText().toString().isEmpty()) {
            product_description.setError("Product description is required");
            return false;
        }
        return true;
    }

    protected void addProduct() {

        if (formValidation() == false) {
            return;
        }

        File file = new File(imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part image = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);

        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), product_name.getText().toString());
        RequestBody category_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(getIdSelectedSpinner()));
        RequestBody stock = RequestBody.create(MediaType.parse("multipart/form-data"), product_stock.getText().toString());
        RequestBody price = RequestBody.create(MediaType.parse("multipart/form-data"), product_price.getText().toString());
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), product_description.getText().toString());

        ApiService.endPoint().postProduct(
                image,
                name,
                category_id,
                stock,
                price,
                description
        ).enqueue(new Callback<ResponseDetail>() {
            @Override
            public void onResponse(Call<ResponseDetail> call, Response<ResponseDetail> response) {
                if (response.isSuccessful()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(ProductActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }, 2000);
                }else{
                    Toast.makeText(ProductActivity.this, "Failed : Server Trouble", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDetail> call, Throwable t) {
                Toast.makeText(ProductActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void updateProduct(String id){
        if (formValidation() == false) {
            return;
        }

        MultipartBody.Part image = null;

        if(imagePath != null){
            File file = new File(imagePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            image = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        }

        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), product_name.getText().toString());
        RequestBody stock = RequestBody.create(MediaType.parse("multipart/form-data"), product_stock.getText().toString());
        RequestBody category_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf((category.getSelectedItemId() == 0) ? productData.getCategory().getId() : getIdSelectedSpinner()));
        RequestBody price = RequestBody.create(MediaType.parse("multipart/form-data"), product_price.getText().toString());
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), product_description.getText().toString());

        ApiService.endPoint().updateProduct(
                id,
                name,
                category_id,
                stock,
                price,
                description,
                image
        ).enqueue(new Callback<ResponseDetail>() {
            @Override
            public void onResponse(Call<ResponseDetail> call, Response<ResponseDetail> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductActivity.this, "Success", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(ProductActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }, 2000);
                }
            }

            @Override
            public void onFailure(Call<ResponseDetail> call, Throwable t) {
                Toast.makeText(ProductActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void loadCategory() {
        ApiService.endPoint().getCategory().enqueue(new Callback<CategoryModel>() {
            @Override
            public void onResponse(Call<CategoryModel> call, Response<CategoryModel> response) {
                if(response.isSuccessful()) {

                    categoryList = response.body().getData();

                    String[] categoryArray = new String[categoryList.size()];

                    for(int i = 0; i < categoryList.size(); i++) {
                        categoryArray[i] = categoryList.get(i).getName();
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ProductActivity.this, android.R.layout.simple_spinner_item, categoryArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    category.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<CategoryModel> call, Throwable t) {
                Toast.makeText(ProductActivity.this, "Gagal memuat kategori", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void loadDataProduct(String id) {
        ApiService.endPoint().getProductDetail(id).enqueue(new Callback<ResponseDetail>() {
            @Override
            public void onResponse(Call<ResponseDetail> call, Response<ResponseDetail> response) {
                if (response.isSuccessful()) {
                    ResponseDetail responseDetail = response.body();
                    if (responseDetail != null && responseDetail.getData() != null) {
                        productData = responseDetail.getData();

                        Picasso.get().load(productData.getPhoto()).into(productImage);
                        product_name.setText(productData.getName());
                        product_stock.setText(String.valueOf(productData.getStock()));
                        product_price.setText(String.valueOf(productData.getPrice()));
                        product_description.setText(productData.getDescription());

                        int index = getIndex(category, productData.getCategory().getName());

                        category.setSelection(index, true);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseDetail> call, Throwable t) {
              Intent moveToMain =  new Intent(ProductActivity.this, MainActivity.class);
              startActivity(moveToMain);
            }
        });
    }

    private int getIdSelectedSpinner(){
        String selectedName = category.getSelectedItem().toString();

        for(int i = 0; i < categoryList.size(); i++){
            if(categoryList.get(i).getName().equals(selectedName)){
                return categoryList.get(i).getId();
            }
        }

        return 1;
    }

    private int getIndex(Spinner spinner, String name) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return 0;
    }

}
