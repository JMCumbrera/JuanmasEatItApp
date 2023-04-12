package com.dam.juanmaseatitapp.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dam.juanmaseatitapp.Interface.ItemClickListener;
import com.dam.juanmaseatitapp.Model.Order;
import com.dam.juanmaseatitapp.R;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    // Atributos de clase
    public TextView txt_cart_name, txt_price, cart_count;

    // Imposibilidad de usar TextDrwable
    //public ImageView img_cart_count;

    private ItemClickListener itemClickListener;

    // Constructor
    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = (TextView)itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView)itemView.findViewById(R.id.cart_item_Price);
        cart_count = (TextView) itemView.findViewById(R.id.cart_item_count);
    }

    // Setter
    public void setTxt_cart_name(TextView txt_cart_name) { this.txt_cart_name = txt_cart_name; }

    @Override
    public void onClick(View view) {

    }
}
public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    // Atributos de clase
    private List<Order> listData = new ArrayList<>();
    private Context context;

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
        Locale locale = new Locale("es", "ES");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));

        holder.txt_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());

        // Sustituto del TextDrawable
        holder.cart_count.setText(listData.get(position).getQuantity());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}