package com.dam.juanmaseatitapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.dam.juanmaseatitapp.Common.Common;
import com.dam.juanmaseatitapp.Database.Database;
import com.dam.juanmaseatitapp.Model.Order;
import com.dam.juanmaseatitapp.Model.Request;
import com.dam.juanmaseatitapp.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Actividad que muestra el contenido del carrito de compras
 */
public class Cart extends AppCompatActivity {
    // Atributos de clase
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests;
    TextView txtTotalPrice;
    AppCompatButton btnPlace;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    // Métodos
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        // Inicializamos
        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setOnClickListener(this::registerForContextMenu);

        txtTotalPrice = (TextView)findViewById(R.id.total);
        btnPlace = (AppCompatButton)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(view -> {
            if (cart.size() > 0)
                showAlertDialog();
            else
                Toast.makeText(Cart.this, "Tu carro está vacío", Toast.LENGTH_SHORT).show();
        });

        loadListFood();
    }

    /**
     * Clase que nos permitirá crear una ventana de diálogo que aparecerá cuando hagamos click
     * sobre un elemento del carro de compra, con la opción de eliminar dicho elemento.
     * @param position Posición del elemento en el carro
     */
    private void showDeleteItemDialog(int position) {
        // Creamos el AlertDialog usando el constructor de Builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Cart.this);

        // Inflamos el layout de la vista de los elementos eliminables
        LayoutInflater inflater = this.getLayoutInflater();
        View deleteItemView = inflater.inflate(R.layout.delete_item_dialog, null);
        alertDialogBuilder.setView(deleteItemView);

        // Creamos un array final con un elemento para almacenar la instancia del AlertDialog
        final AlertDialog[] alertDialogArray = new AlertDialog[1];

        Button deleteItemButton = deleteItemView.findViewById(R.id.delete_item_button);
        deleteItemButton.setOnClickListener(view -> {
            // Eliminamos el elemento del carrito
            deleteCart(position);

            // Cerramos el diálogo
            if (alertDialogArray[0] != null) {
                alertDialogArray[0].dismiss();
            }
        });

        // Inicializamos el AlertDialog después de configurar la vista
        alertDialogArray[0] = alertDialogBuilder.create();
        alertDialogArray[0].show();
    }

    /**
     * Ventana que aparecerá para que completes tu pedido y éste pueda llegar al restaurante
     */
    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("¡Solo un paso más!");
        alertDialog.setMessage("Introduzca su dirección: ");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment, null);

        MaterialEditText edtAddress = (MaterialEditText)order_address_comment.findViewById(R.id.edtAddress);
        MaterialEditText edtComment = (MaterialEditText)order_address_comment.findViewById(R.id.edtComment);

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.baseline_shopping_cart_24);

        alertDialog.setPositiveButton("SI", (dialog, which) -> {
            if (edtAddress.getText().toString().isEmpty()) {
                Toast.makeText(this, "Por favor, rellene la dirección", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                // Creamos una nueva petición (clase Request encargada de los envíos)
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart,
                        edtComment.getText().toString()
                );

                // Lo submiteamos a la BD Firebase
                requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);

                // Una vez subido el pedido a la BD, borramos el carro de compra
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this, "¡Gracias por su compra!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDialog.setNegativeButton("NO", (dialogInterface, which) -> dialogInterface.dismiss());

        alertDialog.show();
    }

    /**
     * Método que nos permitirá borrar un elemento de nuestro carro de compra
     * @param order Elemento de nuestro carro que borraremos
     */
    private void deleteCart(int order) {
        // Eliminaremos el item en List<Order> a través de su posición
        cart.remove(order);

        // Una vez hecho esto borraremos todos los datos desactualizados de SQLite
        new Database(this).cleanCart();

        // Y finalmente, actualizaremos los datos de SQLite usando List<Order>
        for (Order item:cart)
            new Database(this).addToCart(item);

        // Refrescamos los datos
        loadListFood();
    }

    /**
     * Gracias a este método podremos visualizar la lista de platos que hayamos pedido. Estos se
     * hayan ahora en el carro de compra, en el cual se visualizarán
     */
    private void loadListFood() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);

        adapter.setOnItemClickListener(this::showDeleteItemDialog);

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        // Calculamos el precio total
        double total = 0;
        for (Order order : cart) {
            String price = order.getPrice() == null ? "0" : order.getPrice();
            String quantity = order.getQuantity() == null ? "0" : order.getQuantity();
            String discount = order.getDiscount() == null ? "0" : order.getDiscount();

            if (!price.isEmpty() && !price.equals("null") && !quantity.isEmpty() && !quantity.equals("null") && !discount.isEmpty() && !discount.equals("null")) {
                double accumulatedPrice = Double.parseDouble(price) * Integer.parseInt(quantity);

                if (Double.parseDouble(price) > 0 || Double.parseDouble(discount) < Double.parseDouble(price)) {
                    total += (accumulatedPrice - Double.parseDouble(discount));
                } else if (Double.parseDouble(price) <= 0 && Double.parseDouble(discount) >= Double.parseDouble(price)) {
                    total += accumulatedPrice;
                }
            }
        }

        Locale locale = new Locale("es", "ES");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }

}