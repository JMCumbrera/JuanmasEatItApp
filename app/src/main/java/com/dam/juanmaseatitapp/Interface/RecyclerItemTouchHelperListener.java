package com.dam.juanmaseatitapp.Interface;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Interfaz que permite el desplazamiento de elementos
 */
public interface RecyclerItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
}
