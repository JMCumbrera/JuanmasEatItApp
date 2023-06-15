package com.dam.juanmaseatitapp.ViewHolder;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dam.juanmaseatitapp.R;

/**
 * Clase usada para representar y manejar la vista de un elemento de pedido en un RecyclerView
 */
public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    // Atributos de clase
    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress;

    // Constructor
    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);

        itemView.setOnClickListener(this);
    }

    // Métodos
    @Override
    public void onClick(View view) {
        //itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
