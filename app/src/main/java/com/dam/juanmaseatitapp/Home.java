package com.dam.juanmaseatitapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.juanmaseatitapp.Common.Common;
import com.dam.juanmaseatitapp.Interface.ItemClickListener;
import com.dam.juanmaseatitapp.Model.Category;
import com.dam.juanmaseatitapp.ViewHolder.MenuViewHolder;
import com.dam.juanmaseatitapp.databinding.ActivityHomeBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Atributos
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    FirebaseDatabase database;
    DatabaseReference category;
    TextView txtFullName;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        // setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        // Inicializamos la BD
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        ((FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Ponemos el nombre del usuario actual
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());

        // Cargamos el menú
        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        loadMenu();
    }

    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.txtMenuName.setText(model.getName());

                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);

                Category clickItem = model;

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Get CategoryId and send to new Activity
                        Intent foodList = new Intent(Home.this, FoodList.class);

                        // Because CategoryId is key, so we just get the key of this item
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }
        };
        recycler_menu.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        // inflater.inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Buscamos los elementos del menu
        MenuItem itemNavMenu = menu.findItem(R.id.nav_menu);
        MenuItem itemNavCart = menu.findItem(R.id.nav_cart);
        MenuItem itemNavOrders = menu.findItem(R.id.nav_orders);
        MenuItem itemNavLogOut = menu.findItem(R.id.nav_log_out);

        // Y los activamos
        itemNavMenu.setEnabled(true);
        itemNavCart.setEnabled(true);
        itemNavOrders.setEnabled(true);
        itemNavLogOut.setEnabled(true);

        return super.onPrepareOptionsMenu(menu);
    }*/



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Manejar los clics del elemento de vista de navegación aquí
        /*int id = item.getItemId();

        MenuItem itemNavMenu = item.getSubMenu().findItem(R.id.nav_menu);
        MenuItem itemNavCart = item.getSubMenu().findItem(R.id.nav_cart);
        MenuItem itemNavOrders = item.getSubMenu().findItem(R.id.nav_orders);
        MenuItem itemNavLogOut = item.getSubMenu().findItem(R.id.nav_log_out);
        MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {

            }
        };

        itemNavCart.setOnMenuItemClickListener(listener);
        itemNavMenu.setOnMenuItemClickListener(listener);
        itemNavOrders.setOnMenuItemClickListener(listener);
        itemNavLogOut.setOnMenuItemClickListener(listener);

        Toast.makeText(this, "ID Menu: " + R.id.nav_menu, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "ID Cart: " + R.id.nav_cart, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "ID Order: " + R.id.nav_orders, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "ID LogOut: " + R.id.nav_log_out, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "ID Item: " + item.getItemId(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "ID NavView: " + item.getTitle(), Toast.LENGTH_SHORT).show();

        drawerLayout.closeDrawer(GravityCompat.START);*/
        Toast.makeText(this, "Asdf", Toast.LENGTH_SHORT).show();
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        };
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.nav_menu):
                Toast.makeText(getApplicationContext(), "Funcionó", Toast.LENGTH_SHORT).show();
                return true;
            case (R.id.nav_cart):
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
                return true;
            case (R.id.nav_orders):
                Intent orderIntent = new Intent(Home.this, OrderStatus.class);
                startActivity(orderIntent);
                return true;
            case (R.id.nav_log_out):
                // Logout
                Intent signIn = new Intent(Home.this, SignIn.class);
                signIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signIn);
                return true;
            default: return false;
        }
    }
}