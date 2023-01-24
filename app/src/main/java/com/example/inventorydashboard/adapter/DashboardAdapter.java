package com.example.inventorydashboard.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorydashboard.R;
import com.example.inventorydashboard.model.DataItem;
import com.example.inventorydashboard.model.ProductModel;
import com.example.inventorydashboard.model.ResponseDelete;
import com.example.inventorydashboard.retrofit.ApiService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.viewHolder> {

    private List<DataItem> productList;
    private onAdapterListener listener;

    public DashboardAdapter(List<DataItem> productList, onAdapterListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DashboardAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder((ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardAdapter.viewHolder holder, @SuppressLint("RecyclerView") int position) {
        DataItem dataItem = productList.get(position);

        holder.productName.setText(dataItem.getName());
        holder.jenis.setText(dataItem.getCategory().getName());

        Picasso.get().load(dataItem.getPhoto()).into(holder.imageItem);

        holder.btnEdit.setOnClickListener(v -> {
            listener.onClick("edit", dataItem);
        });

        holder.btnDelete.setOnClickListener(v -> {
            listener.onClick("delete", dataItem);
        });


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        private TextView productName, jenis;
        private ImageView imageItem;
        private ImageView btnEdit, btnDelete;


        public viewHolder(@NonNull ViewGroup itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.productName);
            jenis = itemView.findViewById(R.id.jenis);
            imageItem = itemView.findViewById(R.id.imageItem);

            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    public void setData(List<DataItem> data) {
        productList.clear();
        productList.addAll(data);
        notifyDataSetChanged();
    }

    public interface onAdapterListener {
        void onClick(String action, DataItem dataItem);
    }
}
