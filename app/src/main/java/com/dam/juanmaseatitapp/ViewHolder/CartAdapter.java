package com.dam.juanmaseatitapp.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dam.juanmaseatitapp.Model.Order;
import com.dam.juanmaseatitapp.R;
import com.squareup.picasso.Picasso;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    // Atributos de clase
    private List<Order> listData = new ArrayList<>();
    private Context context;
    private OnItemClickListener onItemClickListener;

    // Constructor
    public CartAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Picasso.with(context)
                .load(listData.get(position).getImage())
                .resize(70, 70)
                .into(holder.cart_image);

        Locale locale = new Locale("es", "ES");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        double price = (Double.parseDouble(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));

        holder.txt_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());

        // Sustituto del TextDrawable
        holder.cart_count.setText(listData.get(position).getQuantity());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    /**
     * Con este método obtendremos el número de elementos que contiene la lista listData
     * @return Número de elementos de la lista listData
     */
    @Override
    public int getItemCount() {
        return listData.size();
    }

    // Interfaz y Setter para click events
    public interface OnItemClickListener { void onItemClick(int position); }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}