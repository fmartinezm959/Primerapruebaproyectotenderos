package proyecto.example.tiendas;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegistrarActivity extends AppCompatActivity {

    private EditText EmailUsuario;
    private EditText NombreUsuario;
    private EditText TelefonoUsuario;
    private EditText ContrasenaUsuario;
    private Button Registrar;
    private ProgressBar progressBar;
    private Pattern emailRegex;

    private DatabaseReference mDataBase;
    private final String nombreBaseDatos = "Usuarios";

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        firebaseAuth = FirebaseAuth.getInstance();

        emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        EmailUsuario = (EditText) findViewById(R.id.emailUsuario);
        NombreUsuario = (EditText) findViewById(R.id.nombreUsuario);
        TelefonoUsuario = (EditText) findViewById(R.id.TelefonoUsuario);
        ContrasenaUsuario = (EditText) findViewById(R.id.ConstraseñaUsuario);

        Registrar = (Button) findViewById(R.id.registrarUsuario);

        Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completarRegistro();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBarRegistrar);

    }
        private void registrarUsuario() {

            String email = EmailUsuario.getText().toString().trim();
            String nombre = NombreUsuario.getText().toString().trim();
            String telefono = TelefonoUsuario.getText().toString().trim();
            String constraseña = ContrasenaUsuario.getText().toString().trim();

            if(TextUtils.isEmpty(email)){
                Toast.makeText(this,"Se debe ingresar un email",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(nombre)){
                Toast.makeText(this,"Se debe ingresar un nombre",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(telefono)){
                Toast.makeText(this,"Se debe ingresar un telefono",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(constraseña)){
                Toast.makeText(this,"Se debe ingresar una contraseña",Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            Registrar.setVisibility(View.GONE);
            EmailUsuario.setVisibility(View.GONE);
            NombreUsuario.setVisibility(View.GONE);
            TelefonoUsuario.setVisibility(View.GONE);
            ContrasenaUsuario.setVisibility(View.GONE);

            firebaseAuth.createUserWithEmailAndPassword(email,constraseña)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(RegistrarActivity.this, "Se ha registrado el email", Toast.LENGTH_SHORT).show();
                        }else{
                                Toast.makeText(RegistrarActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                            }
                    }

            });

        }


    public void completarRegistro() {

        mDataBase = FirebaseDatabase.getInstance().getReference();

        String email = EmailUsuario.getText().toString().trim();
        String pass = ContrasenaUsuario.getText().toString().trim();

        Log.i("Login", "Inicio ok");

        if (mailIsOk(email)){
            firebaseAuth.createUserWithEmailAndPassword(email,pass).
                    addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.i("LoginTest", "pasa ok");
                            if (task.isSuccessful()){
                                Log.i("LoginTest", "ok");
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                Toast.makeText(getBaseContext(),"Registro Exitoso",Toast.LENGTH_SHORT).show();

                                String nombre = NombreUsuario.getText().toString().trim();
                                String telefono = TelefonoUsuario.getText().toString().trim();


                                mDataBase.child(nombreBaseDatos).child(firebaseAuth.getUid()).child("Nombre").setValue(nombre);
                                mDataBase.child(nombreBaseDatos).child(firebaseAuth.getUid()).child("Telefono").setValue(telefono);

                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);


                            }else{
                                Log.i("LoginTest", "bad");
                                Toast.makeText(getBaseContext(),"Registro fallido",Toast.LENGTH_SHORT).show();
                                Log.i("LoginTest", ""+ task.getException());
                            }
                        }
                    });
        }else{
            Toast.makeText(getBaseContext(),"Digita un correo válido",Toast.LENGTH_SHORT).show();
        }


    }

    public boolean mailIsOk (String email){return emailRegex.matcher(email).matches();}

}
