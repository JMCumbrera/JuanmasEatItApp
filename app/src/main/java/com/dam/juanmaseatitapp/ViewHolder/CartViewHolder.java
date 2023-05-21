package com.dam.juanmaseatitapp.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.juanmaseatitapp.Common.Common;
import com.dam.juanmaseatitapp.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    // Atributos de clase
    public TextView txt_cart_name, txt_price, cart_count;
    public ImageView cart_image;
    public RelativeLayout view_background;
    public LinearLayout view_foreground;

    // Imposibilidad de usar TextDrawable
    //public ImageView img_cart_count;
    //private ItemClickListener itemClickListener;

    // Constructor
    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = (TextView)itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView)itemView.findViewById(R.id.cart_item_Price);
        cart_count = (TextView)itemView.findViewById(R.id.cart_item_count);
        cart_image = (ImageView)itemView.findViewById(R.id.cart_image);
        //view_background = (RelativeLayout)itemView.findViewById(R.id.view_background);
        //view_foreground = (LinearLayout)itemView.findViewById(R.id.view_foreground);
    }

    // Setter
    //public void setTxt_cart_name(TextView txt_cart_name) { this.txt_cart_name = txt_cart_name; }

    @Override
    public void onClick(View view) {}

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        contextMenu.setHeaderTitle("Seleccione una acción");
        contextMenu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}
