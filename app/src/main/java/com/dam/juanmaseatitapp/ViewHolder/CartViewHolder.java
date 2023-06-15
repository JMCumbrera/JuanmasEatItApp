package com.dam.juanmaseatitapp.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dam.juanmaseatitapp.Common.Common;
import com.dam.juanmaseatitapp.R;

/**
 * Clase encargada de mantener y gestionar la vista de cada elemento de la lista del carro de
 * compra en un RecyclerView
 */
public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    // Atributos de clase
    public TextView txt_cart_name, txt_price, cart_count;
    public ImageView cart_image;

    // Constructor
    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = (TextView)itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView)itemView.findViewById(R.id.cart_item_Price);
        cart_count = (TextView)itemView.findViewById(R.id.cart_item_count);
        cart_image = (ImageView)itemView.findViewById(R.id.cart_image);
    }

    @Override
    public void onClick(View view) {}

    /**
     * Clase que crea la opción de eliminar al mantener un clic sobre un elemento del carro
     * @param contextMenu El contexto del menú que se está construyendo
     * @param v La vista para la que se está construyendo el contexto del menú
     * @param menuInfo Información adicional sobre el elemento para el que se debe mostrar
     *      *          el contexto del menú. Esta información variará dependiendo de la clase de
     *      *          la vista
     */
    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        contextMenu.setHeaderTitle("Seleccione una acción");
        contextMenu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}
