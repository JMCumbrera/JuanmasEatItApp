package com.dam.juanmaseatitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.dam.juanmaseatitapp.Common.Common;
import com.dam.juanmaseatitapp.Database.Database;
import com.dam.juanmaseatitapp.Model.Food;
import com.dam.juanmaseatitapp.Model.Order;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * Clase (Actividad) que muestra los detalles de un alimento específico
 */
public class FoodDetail extends AppCompatActivity {
    // Atributos de clase
    TextView food_name, food_price, food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    com.shawnlin.numberpicker.NumberPicker numberPicker;
    String foodId = "";
    FirebaseDatabase database;
    DatabaseReference foods;

    // Comida cuyos detalles estás viendo ahora mismo
    Food currentFood;

    // Cantidad de comida pedida mínima y máxima
    public static int MAX_FOOD_QUANTITY = 20;
    public static int MIN_FOOD_QUANTITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        // Firebase
        // OJO: Recordemos que el path debe coincidir con el de la BD
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Food");

        // Init view
        numberPicker = (com.shawnlin.numberpicker.NumberPicker) findViewById(R.id.number_button);

        // Establecemos el color del divisor
        numberPicker.setDividerColor(ContextCompat.getColor(this, R.color.colorPrimary));
        numberPicker.setDividerColorResource(R.color.colorPrimary);

        // Numeros máximo y mínimo del selector de cantidades
        numberPicker.setMinValue(MIN_FOOD_QUANTITY);
        numberPicker.setMaxValue(MAX_FOOD_QUANTITY);

        btnCart = (FloatingActionButton)findViewById(R.id.btnCart);

        // Función de compra
        btnCart.setOnClickListener(view -> {
            new Database(getBaseContext()).addToCart(new Order(
                    foodId,
                    currentFood.getName(),
                    Integer.toString(numberPicker.getValue()),
                    currentFood.getPrice(),
                    currentFood.getDiscount(),
                    currentFood.getImage()
            ));

            Toast.makeText(FoodDetail.this, "Añadido al carro", Toast.LENGTH_SHORT).show();
        });

        food_description = (TextView)findViewById(R.id.food_description);
        food_name = (TextView)findViewById(R.id.food_name);
        food_price = (TextView)findViewById(R.id.food_price);
        food_image = (ImageView)findViewById(R.id.food_image);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        // Obtenemos el FoodId de la actividad
        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty()) {
            if (Common.isConnectedToInternet(this))
                getDetailFood(foodId);
            else
                Toast.makeText(FoodDetail.this, "Por favor, compruebe su conexión a Internet", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método diseñado para obtener los detalles de un plato en concreto pasado como parámetro
     * @param foodId Identificador del plato cuyos detalles queremos obtener
     */
    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentFood = snapshot.getValue(Food.class);

                // Ponemos la imagen
                // Debemos asegurarnos que la comida en cuestión no devuelve null
                if (currentFood != null) {
                    Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);
                    collapsingToolbarLayout.setTitle(currentFood.getName());

                    food_price.setText(currentFood.getPrice());
                    food_name.setText(currentFood.getName());
                    food_description.setText(currentFood.getDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}