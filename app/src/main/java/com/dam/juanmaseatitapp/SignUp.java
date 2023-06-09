package com.dam.juanmaseatitapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.dam.juanmaseatitapp.Common.Common;
import com.dam.juanmaseatitapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.Objects;

/**
 * Clase encargada de los registros de usuarios en la aplicación
 */
public class SignUp extends AppCompatActivity {
    // Atributos de clase
    MaterialEditText edtPhone, edtName, edtPassword, edtSecureCode;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName = (MaterialEditText)findViewById(R.id.edtName);
        edtPassword = (MaterialEditText)findViewById(R.id.edtPassword);
        edtPhone = (MaterialEditText)findViewById(R.id.edtPhone);
        edtSecureCode = (MaterialEditText)findViewById(R.id.edtSecureCode);

        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        // Inicializamos la BD Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(view -> {
            if (Common.isConnectedToInternet(getBaseContext())) {
                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                mDialog.setMessage("Por favor espere...");
                mDialog.show();

                if (edtName.getText().toString().isEmpty() ||
                        edtPassword.getText().toString().isEmpty() ||
                        edtPhone.getText().toString().isEmpty() ||
                        edtSecureCode.getText().toString().isEmpty()) {
                    mDialog.dismiss();
                    Toast.makeText(this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Comprobamos si el nº de tel. está registrado
                            if(dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "Número de teléfono ya registrado", Toast.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();

                                User user = new User(Objects.requireNonNull(edtName.getText()).toString(),
                                        Objects.requireNonNull(edtPassword.getText()).toString(),
                                        Objects.requireNonNull(edtSecureCode.getText()).toString());
                                table_user.child(edtPhone.getText().toString()).setValue(user);

                                Toast.makeText(SignUp.this, "Se ha registrado con éxito", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            } else {
                Toast.makeText(this, "Por favor, compruebe su conexión a internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}