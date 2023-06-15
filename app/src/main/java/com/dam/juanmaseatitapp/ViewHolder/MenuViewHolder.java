package com.dam.juanmaseatitapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dam.juanmaseatitapp.Interface.ItemClickListener;
import com.dam.juanmaseatitapp.R;

/**
 * Clase utilizada para representar y manejar la vista de un elemento del menú en un RecyclerView
 */
public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    // Atributos de clase
    public TextView txtMenuName;
    public ImageView imageView;
    private ItemClickListener itemClickListener;

    // Constructor
    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);

        txtMenuName = (TextView)itemView.findViewById(R.id.menu_name);
        imageView = (ImageView)itemView.findViewById(R.id.menu_image);

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
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
