package com.dam.juanmaseatitapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dam.juanmaseatitapp.Interface.ItemClickListener;
import com.dam.juanmaseatitapp.R;

/**
 * Clase encargada de mantener y gestionar la vista de cada elemento de la lista de platos
 * en un RecyclerView
 */
public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    // Atributos de clase
    public TextView food_name, food_price;
    public ImageView food_image, fav_image;
    private ItemClickListener itemClickListener;

    // Constructor
    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        food_name = (TextView)itemView.findViewById(R.id.food_name);
        food_image = (ImageView)itemView.findViewById(R.id.food_image);
        fav_image = (ImageView)itemView.findViewById(R.id.fav);
        food_price = (TextView)itemView.findViewById(R.id.food_price);

        itemView.setOnClickListener(this);
    }

    // Métodos
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * Este método se invocará cuando se produzca un clic en un elemento de la interfaz de usuario
     * @param view Vista en la que se hizo clic
     */
    @Override
    public void onClick(View view) { itemClickListener.onClick(view, getAdapterPosition(), false); }
}
