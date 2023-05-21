package com.dam.juanmaseatitapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.dam.juanmaseatitapp.Common.Common;
import com.dam.juanmaseatitapp.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Home extends AppCompatActivity {
    // Attributos de clase
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding homeBinding;
    TextView txtFullName;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());

        Toolbar toolbar = homeBinding.appBarHome.toolbar;
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        // Paper
        Paper.init(this);

        drawerLayout = homeBinding.drawerLayout;
        NavigationView navView = homeBinding.navView;
        navView.bringToFront();

        homeBinding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, /*R.id.nav_gallery,*/ R.id.nav_order_status)
                .setOpenableLayout(drawerLayout)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Ponemos el nombre del usuario actual
        View headerView = navView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return true;
    }

    /**
     * Método que permite al ActionMenu del menú Home funcionar. Este método escuchará los clicks
     * realizados en elementos del ActionMenu tales como el LogOut o el cambio de contraseña.
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        //homeBinding.navView.bringToFront();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        homeBinding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.nav_menu):
                    navController.navigate(R.id.nav_home);
                    Toast.makeText(Home.this, "Menu", Toast.LENGTH_SHORT).show();
                    return true;
                case (R.id.nav_cart):
                    //navController.navigate(R.id.nav_cart);
                    Intent intentCart = new Intent(Home.this, Cart.class);
                    startActivity(intentCart);
                    return true;
                case (R.id.nav_orders):
                    navController.navigate(R.id.nav_order_status);
                    Toast.makeText(Home.this, "Order status", Toast.LENGTH_SHORT).show();
                    return true;
                case (R.id.nav_log_out):
                    // Borramos el usuario y la clave guardadas por la función de recordar usuario
                    Paper.book().destroy();

                    Intent intentLogout = new Intent(Home.this, MainActivity.class);
                    startActivity(intentLogout);
                    finish();
                    return true;
                case (R.id.nav_change_pwd):
                    showChangePasswordDialog();
                    return true;
                default: return false;
            }
        });
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("CAMBIAR CONTRASEÑA");
        alertDialog.setMessage("Por favor, rellene toda la información");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_pwd = inflater.inflate(R.layout.change_password_layout, null);

        MaterialEditText edtPassword = (MaterialEditText) layout_pwd.findViewById(R.id.edtPassword);
        MaterialEditText edtNewPassword = (MaterialEditText) layout_pwd.findViewById(R.id.edtNewPassword);
        MaterialEditText edtRepeatPassword = (MaterialEditText) layout_pwd.findViewById(R.id.edtRepeatPassword);

        alertDialog.setView(layout_pwd);

        // Botón
        alertDialog.setPositiveButton("CAMBIAR", (dialog, which) -> {
            // Cambiamos la contraseña aquí

            android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
            waitingDialog.show();

            // Comprobamos la contraseña anterior
            if (edtPassword.getText().toString().equals(Common.currentUser.getPassword())) {
                // Comprobamos la contraseña nueva y la repetimos
                if (edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString())) {
                    Map<String, Object> passwordUpdate = new HashMap<>();
                    passwordUpdate.put("Password", edtNewPassword.getText().toString());

                    // Llevamos la actualización
                    DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                    user.child(Common.currentUser.getPhone())
                            .updateChildren(passwordUpdate)
                            .addOnCompleteListener(task -> {
                                waitingDialog.dismiss();
                                Toast.makeText(Home.this, "La contraseña fue actualizada", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    waitingDialog.dismiss();
                    Toast.makeText(Home.this, "Las contraseñas introducidas no coinciden", Toast.LENGTH_SHORT).show();
                }
            } else {
                waitingDialog.dismiss();
                Toast.makeText(Home.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.setNegativeButton("CANCELAR", (dialog, which) -> dialog.dismiss());

        alertDialog.show();
    }
}