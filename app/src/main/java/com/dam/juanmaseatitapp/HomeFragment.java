package com.dam.juanmaseatitapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dam.juanmaseatitapp.Common.Common;
import com.dam.juanmaseatitapp.Model.Category;
import com.dam.juanmaseatitapp.ViewHolder.MenuViewHolder;
import com.dam.juanmaseatitapp.databinding.FragmentHomeBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * Fragmento que representa la pantalla principal de la aplicación (Home)
 */
public class HomeFragment extends Fragment {
    // Attributos de clase
    private FragmentHomeBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference category;
    private RecyclerView recycler_menu;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    View homeFragmentRoot;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        homeFragmentRoot = root;

        // Inicializamos la BD
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        // Cargamos el menú
        recycler_menu = binding.recyclerHome;
        recycler_menu.setHasFixedSize(true);
        recycler_menu.setLayoutManager(new GridLayoutManager(getContext(), 2));

        if (Common.isConnectedToInternet(root.getContext()))
            loadMenu(root.getContext());
        else
            Toast.makeText(root.getContext(), "Por favor, compruebe su conexión a Internet", Toast.LENGTH_SHORT).show();

        return root;
    }

    /**
     * Método que carga el menú desde Firebase y lo muestra en el Recycler View
     * @param context El contexto de la aplicación
     */
    private void loadMenu(Context context) {
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.txtMenuName.setText(model.getName());

                Picasso.with(context).load(model.getImage())
                        .into(viewHolder.imageView);

                Category clickItem = model;

                viewHolder.setItemClickListener((view, position1, isLongClick) -> {
                    // Obtenemos CategoryId y lo enviamos a la nueva Activity
                    Intent foodList = new Intent(context, FoodList.class);

                    // Ya que CategoryId es una key, simplemente obtenemos la key de este item
                    foodList.putExtra("CategoryId", adapter.getRef(position1).getKey());
                    startActivity(foodList);
                });
            }
        };
        recycler_menu.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}