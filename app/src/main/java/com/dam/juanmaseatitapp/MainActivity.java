package com.dam.juanmaseatitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.dam.juanmaseatitapp.Common.Common;
import com.dam.juanmaseatitapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import io.paperdb.Paper;

/**
 * Clase principal e inicial de la aplicación
 */
public class MainActivity extends AppCompatActivity {
    // Atributos
    Button btnSignIn, btnSignUp;
    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        txtSlogan = (TextView)findViewById(R.id.txtSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        txtSlogan.setTypeface(face);

        // Inicializamos Paper (para la función de recordar usuario)
        Paper.init(this);

        btnSignUp.setOnClickListener(view -> {
            Intent signUp = new Intent(MainActivity.this, SignUp.class);
            startActivity(signUp);
        });

        btnSignIn.setOnClickListener(view -> {
            Intent signIn = new Intent(MainActivity.this, SignIn.class);
            startActivity(signIn);
        });

        // Comprobamos si el usuario y contraseña coinciden con
        // los guardados por el botón de recordar usuario
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);

        if (user != null && pwd != null) {
            if (!user.isEmpty() && !pwd.isEmpty()) {
                login(user, pwd);
            }
        }
    }

    /**
     * Método que iniciará sesión de forma automática, siempre que tengamos marcada la
     * opción de recordar el usuario
     * @param phone Usuario guardado
     * @param pwd Contraseña guardada
     */
    private void login(String phone, String pwd) {
        // Inicializamos la BD Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        if (Common.isConnectedToInternet(getBaseContext())) {
            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Por favor espere...");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Comprueba si el usuario no existe en la BD
                    if (dataSnapshot.child(phone).exists()) {
                        // Conseguimos la info. del usuario
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);

                        // Usamos el set del atributo Phone (teléfono), para establecerlo
                        user.setPhone(phone);

                        // Si encontramos la clase accedemos al usuario
                        if (user.getPassword().equals(pwd)) {
                            {
                                Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                Common.currentUser = user;
                                startActivity(homeIntent);
                                finish();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "El usuario no existe en la BD", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        } else {
            Toast.makeText(this, "Por favor, compruebe su conexión a internet", Toast.LENGTH_SHORT).show();
        }
    }
}