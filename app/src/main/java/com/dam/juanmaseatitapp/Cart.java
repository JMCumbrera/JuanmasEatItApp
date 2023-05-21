package com.dam.juanmaseatitapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
    //RelativeLayout rootLayout;

    // Métodos
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        // Init
        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Deslizar para eliminar item
        //ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        //new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

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
     * Ventana que aparecerá para que completes tu pedido y éste pueda llegar al restaurante
     */
    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("¡Solo un paso más!");
        alertDialog.setMessage("Introduzca su dirección: ");

        /*final EditText edtAddress = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        edtAddress.setLayoutParams(lp);

        // Añadimos un campo EditText al alertDialog
        alertDialog.setView(edtAddress);*/

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

    /*@Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)) {
            deleteCart(item.getOrder());
            return true;
        }

        return false;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)) {
            deleteCart(item.getOrder());
            return true;
        }

        return false;
    }*/



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
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        // Calculamos el precio total
        int total = 0;
        for (Order order : cart)
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));

        Locale locale = new Locale("es", "ES");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }

    /*@Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder) {
            int index = viewHolder.getAdapterPosition();
            CartAdapter cartAdapter = (CartAdapter)recyclerView.getAdapter();

            String name = String.valueOf(cartAdapter.getItem(index));

            Order deleteItem = cartAdapter.getItem(index);

            adapter.removeItem(index);
            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId(), Common.currentUser.getPhone());

            // Calculamos el precio
            int total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts();
            for (Order item : orders)
                total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
            Locale locale = new Locale("es", "ES");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            txtTotalPrice.setText(fmt.format(total));

            // Snackbar
            Snackbar snackbar = Snackbar.make(rootLayout, "¡" + name + " eliminado del carro!", Snackbar.LENGTH_LONG);
            snackbar.setAction("DESHACER", view -> {
                adapter.restoreItem(deleteItem, index);
                new Database(getBaseContext()).addToCart(deleteItem);

                // Calculamos el precio
                int total = 0;
                List<Order> orders = new Database(getBaseContext()).getCarts();
                for (Order item : orders)
                    total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
                Locale locale = new Locale("es", "ES");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                txtTotalPrice.setText(fmt.format(total));*//*
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }*/
}