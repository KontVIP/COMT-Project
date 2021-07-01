package com.example.stylishclothes.catalog;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylishclothes.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecommendedListAdapter extends RecyclerView.Adapter<RecommendedListAdapter.MyViewHolder> {
    private List<Product> productList;
    Context context;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView price, caption;
        ImageView imageView;

        MyViewHolder(View view) {
            super(view);
            price = view.findViewById(R.id.rec_price_text_view);
            imageView = view.findViewById(R.id.rec_image_view);
            caption = view.findViewById(R.id.rec_caption_text_view);
        }
    }

    public RecommendedListAdapter(List<Product> products, Context context) {
        this.productList = products;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recommended_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.price.setText("₴" + product.getPrice());
        Picasso.get().load(product.getTitleImagePath()).into(holder.imageView);
        holder.caption.setText(product.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentOneProductActivity(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void intentOneProductActivity(Product currentProduct) {
        Intent intent = new Intent(context, OneProductActivity.class);
        intent.putExtra("ProductId", currentProduct.getId());
        intent.putExtra("Title", currentProduct.getTitle());
        intent.putExtra("Category", currentProduct.getCategory());
        context.startActivity(intent);
    }

}
