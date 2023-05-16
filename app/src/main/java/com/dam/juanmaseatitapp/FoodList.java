package com.dam.juanmaseatitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dam.juanmaseatitapp.Common.Common;
import com.dam.juanmaseatitapp.Database.Database;
import com.dam.juanmaseatitapp.Interface.ItemClickListener;
import com.dam.juanmaseatitapp.Model.Food;
import com.dam.juanmaseatitapp.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.search.SearchBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {
    // Atributos de clase
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference foodList;
    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    // Barra de búsqueda
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar searchBar;
    // Favoritos
    Database localDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        // Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Food");

        // BD Local
        localDB = new Database(this);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Obtenemos el  Intent aquí
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty() && categoryId != null) {
            if (Common.isConnectedToInternet(this))
                loadListFood(categoryId);
            else
                Toast.makeText(FoodList.this, "Por favor, compruebe su conexión a Internet", Toast.LENGTH_SHORT).show();
        }

        // Búsqueda
        searchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        searchBar.setHint("Introduzca su plato");
        searchBar.clearFocus();
        loadSuggest();
        searchBar.setLastSuggestions(suggestList);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // A medida que el usuario va escribiendo su plato, la lista irá cambiando
                List<String> suggest = new ArrayList<String>();
                for (String search:suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase())) {
                        suggest.add(search);
                    }
                    searchBar.setLastSuggestions(suggest);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                // Cuando la barra de búsqueda se cierre restauraremos el adapter original
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                // Cuando termine la búsqueda mostraremos el resultado del adapter de búsqueda
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {}
        });
    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("name").equalTo(text.toString()) // Comparamos nombre con el texto
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int i) {
                foodViewHolder.food_name.setText(food.getName());
                Picasso.with(getBaseContext()).load(food.getImage())
                        .into(foodViewHolder.food_image);

                final Food local = food;
                foodViewHolder.setItemClickListener((view, position, isLongClick) -> {
                    // Iniciaremos una actividad nueva (Activity)
                    Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);

                    // Mandamos la FoodId a la actividad nueva
                    foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey());

                    // Y la iniciamos
                    startActivity(foodDetail);
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        foodList.orderByChild(String.valueOf("MenuId".equals(categoryId)))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren()) {
                            Food item = postSnapshot.getValue(Food.class);

                            // Añadimos el nombre de una comida a la lista de sugerencias
                            suggestList.add(item.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    /**
     * Método que cargará la lista de platos disponibles
     * @param categoryId ID de la categoría a la que pertenece el susodicho plato
     */
    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(categoryId)) { // Equivale a: SELECT * FROM Foods WHERE menuId =
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int i) {
                foodViewHolder.food_name.setText(food.getName());
                foodViewHolder.food_price.setText(String.format("%s €", food.getPrice().toString()));
                Picasso.with(getBaseContext()).load(food.getImage())
                        .into(foodViewHolder.food_image);

                // Favorites
                // Añadimos los platos favoritos
                //if (localDB.isFavorite(adapter.getRef(i).getKey()))
                //    foodViewHolder.fav_image.setImageResource(R.drawable.baseline_favorite_24);

                // Al hacer click cambiaremos su estado
                //foodViewHolder.fav_image.setOnClickListener(view_favorite -> {
                //    if (!localDB.isFavorite(adapter.getRef(i).getKey())) {
                //        localDB.addFavorites(adapter.getRef(i).getKey());
                //        foodViewHolder.fav_image.setImageResource(R.drawable.baseline_favorite_24);
                //        Toast.makeText(FoodList.this, ""+food.getName()+" fue añadido a Favoritos", Toast.LENGTH_SHORT).show();
                //    } else {
                //        localDB.removeFromFavorites(adapter.getRef(i).getKey());
                //        foodViewHolder.fav_image.setImageResource(R.drawable.baseline_favorite_24);
                //        Toast.makeText(FoodList.this, ""+food.getName()+" fue eliminado de Favoritos", Toast.LENGTH_SHORT).show();
                //    }
                //});

                final Food local = food;
                foodViewHolder.setItemClickListener((view, position, isLongClick) -> {
                    // Iniciaremos una actividad nueva (Activity)
                    Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);

                    // Mandamos la FoodId a la actividad nueva
                    foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());

                    // Y la iniciamos
                    startActivity(foodDetail);
                });
            }
        };

        // Set Adapter
        recyclerView.setAdapter(adapter);
    }
}