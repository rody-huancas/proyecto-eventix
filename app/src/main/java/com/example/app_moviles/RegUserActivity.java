package com.example.app_moviles;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_moviles.Config.Config;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegUserActivity extends AppCompatActivity {

    private TextInputEditText usuario_nombres_reg, usuario_apellidos_reg, usuario_email_reg, usuario_clave_reg, usuario_direccion_reg, usuario_descripcion_reg;
    private Button btn_registrar_usuario, btn_cancelar_usuario;

    private RequestQueue requestQueue;
    private String API, message;
    private int code, dia, mes, annio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_user);

        setTitle("Registro de usuario");

        usuario_nombres_reg = findViewById(R.id.usuario_nombres_reg);
        usuario_apellidos_reg = findViewById(R.id.usuario_apellidos_reg);
        usuario_email_reg = findViewById(R.id.usuario_email_reg);
        usuario_clave_reg = findViewById(R.id.usuario_clave_reg);
        usuario_direccion_reg = findViewById(R.id.usuario_direccion_reg);
        usuario_descripcion_reg = findViewById(R.id.usuario_descripcion_reg);

        btn_cancelar_usuario = findViewById(R.id.btn_cancelar_usuario);
        btn_registrar_usuario = findViewById(R.id.btn_registrar_usuario);

        Config config = new Config();
        API = config.getAPI_URL() + "api_usuario.php";

        btn_cancelar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(RegUserActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });

        btn_registrar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String validacion = "";
                if (usuario_nombres_reg.getText().toString().isEmpty()) {
                    validacion = validacion + "Ingrese sus nombres";
                } else if (usuario_apellidos_reg.getText().toString().isEmpty()) {
                    validacion = validacion + "Ingrese sus apellidos";
                } else if (usuario_email_reg.getText().toString().isEmpty()) {
                    validacion = validacion + "Ingrese su correo";
                } else if (usuario_clave_reg.getText().toString().isEmpty()) {
                    validacion = validacion + "Ingrese su contraseña";
                } else if (usuario_direccion_reg.getText().toString().isEmpty()) {
                    validacion = validacion + "Ingrese su direccion";
                } else if (usuario_descripcion_reg.getText().toString().isEmpty()) {
                    validacion = validacion + "Ingrese su descripcion";
                }

                if (validacion.isEmpty()) {
                    RegistrarUsuario();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(RegUserActivity.this);
                    alert.setTitle("Registro de usuarios");
                    alert.setMessage(validacion);
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
            }
        });

    }

    private void RegistrarUsuario() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Registrando", "Espere un momento", false, false);
        Toast.makeText(RegUserActivity.this, "prueba", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.cancel();
                    JSONObject jsonObject = new JSONObject(response);
                    message = jsonObject.getString("message");
                    code = jsonObject.getInt("code");

                    AlertDialog.Builder alert = new AlertDialog.Builder(RegUserActivity.this);
                    alert.setTitle("Registro de usuarios");
                    alert.setMessage(message);
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            if (code == 1) {
                                finish();
                            }
                        }
                    });
                    AlertDialog dialog = alert.create();
                    dialog.show();

                } catch (JSONException e) {
                    progressDialog.cancel();
                    Toast.makeText(RegUserActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(RegUserActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nombre", usuario_nombres_reg.getText().toString());
                parametros.put("apellidos", usuario_apellidos_reg.getText().toString());
                parametros.put("email", usuario_email_reg.getText().toString());
                parametros.put("pass", usuario_clave_reg.getText().toString());
                parametros.put("direccion", usuario_direccion_reg.getText().toString());
                parametros.put("descripcion", usuario_direccion_reg.getText().toString());
                parametros.put("registra_usuario", "");
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

