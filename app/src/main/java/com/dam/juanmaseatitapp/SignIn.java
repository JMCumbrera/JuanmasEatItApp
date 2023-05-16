package com.dam.juanmaseatitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.juanmaseatitapp.Common.Common;
import com.dam.juanmaseatitapp.Model.User;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    // Atributos
    EditText edtPhone, edtPassword;
    Button btnSignIn;
    MaterialCheckBox cKbRemember;
    TextView txtForgotPwd;
    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = (MaterialEditText)findViewById(R.id.edtPassword);
        edtPhone = (MaterialEditText)findViewById(R.id.edtPhone);
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        cKbRemember = (MaterialCheckBox)findViewById(R.id.ckbRemember);
        txtForgotPwd = (TextView)findViewById(R.id.txtForgotPwd);

        // Inicializamos Paper (para la función de recordar usuario)
        Paper.init(this);

        // Inicializamos la BD Firebase
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        txtForgotPwd.setOnClickListener(view -> showForgotPwdDialog());

        btnSignIn.setOnClickListener(view -> {

            if (Common.isConnectedToInternet(getBaseContext())) {
                // Guardamos el usuario y la contraseña (Paper)
                if (cKbRemember.isChecked()) {
                    Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                    Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                }

                final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                mDialog.setMessage("Por favor espere...");
                mDialog.show();

                table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Comprueba si el usuario no existe en la BD
                        if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                            // Conseguimos la info. del usuario
                            mDialog.dismiss();
                            User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);

                            // Usamos el set del atributo Phone (teléfono), para establecerlo
                            user.setPhone(edtPhone.getText().toString());

                            // Si encontramos la clase accedemos al usuario
                            if (user.getPassword().equals(edtPassword.getText().toString())) {
                                {
                                    Intent homeIntent = new Intent(SignIn.this, Home.class);
                                    Common.currentUser = user;
                                    startActivity(homeIntent);
                                    finish();

                                    table_user.removeEventListener(this);
                                }
                            } else {
                                Toast.makeText(SignIn.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(SignIn.this, "El usuario no existe en la BD", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            } else {
                Toast.makeText(this, "Por favor, compruebe su conexión a Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showForgotPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Olvidó su conbtraseña?");
        builder.setMessage("Teclee su código de seguridad");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.forgot_password_layout, null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.baseline_security_24);

        MaterialEditText edtPhone = (MaterialEditText)forgot_view.findViewById(R.id.edtPhone);
        MaterialEditText edtSecureCode = (MaterialEditText)forgot_view.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("SI", (dialog, which) -> {
            // Comprobamos que el usuario esté disponible
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.child(Objects.requireNonNull(edtPhone.getText()).toString()).getValue(User.class);

                    if (user.getSecureCode().equals(edtSecureCode.getText().toString()))
                        Toast.makeText(SignIn.this, "Su contraseña es: " + user.getPassword(), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(SignIn.this, "Código de seguridad incorrecto", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        builder.setNegativeButton("NO", (dialog, which) -> {});

        builder.show();
    }
}