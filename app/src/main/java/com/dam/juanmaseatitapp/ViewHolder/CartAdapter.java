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

/**
 * Clase responsable de gestionar la visualización de los elementos del carro en un RecyclerView
 */
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

    /**
     * Método que se encarga de inflar el diseño del elemento de la vista del carrito "cart_layout"
     * y devuelve un nuevo objeto "CartViewHolder" que contiene la vista inflada
     * @param parent ViewGroup en el que se agregará la nueva vista después de vincularla a
     *               una posición del adaptador (adapter)
     * @param viewType El tipo de vista de la nueva Vista
     * @return Objeto de tipo CartViewHolder que contiene la vista inflada
     */
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    /**
     * Este método se utiliza para establecer los datos en cada elemento de la vista del carro
     * @param holder El ViewHolder que debe actualizarse para representar el contenido del elemento
     *               en la posición dada en el conjunto de datos
     * @param position La posición del elemento dentro del conjunto de datos del adaptador
     */
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

    // Interfaz y Setter para eventos de clic (click events)
    public interface OnItemClickListener { void onItemClick(int position); }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}