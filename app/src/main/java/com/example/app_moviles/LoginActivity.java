package com.example.app_moviles;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText et_usuario_login, et_clave_login;
    private Button btn_ingresar_login, btn_registrar_login;
    private String API, message;
    private int code;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_usuario_login=findViewById(R.id.et_usuario_login);
        et_clave_login=findViewById(R.id.et_clave_login);

        btn_ingresar_login=findViewById(R.id.btn_ingresar_login);
        btn_registrar_login=findViewById(R.id.btn_registrar_login);

        Config config = new Config();
        API = config.getAPI_URL()+"api_usuario.php";

        btn_ingresar_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login();
            }
        });

        btn_registrar_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registrar_usuario = new Intent(LoginActivity.this, RegUserActivity.class);
                startActivity(registrar_usuario);
            }
        });

        obtener_sesion();
    }

    private void login(){
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Validando","Espere un momento", false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.cancel();
                    JSONObject jsonObject = new JSONObject(response);
                    message = jsonObject.getString("message");
                    code = jsonObject.getInt("code");

                    AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                    alert.setTitle("Proyecto");
                    alert.setMessage(message);
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.cancel();

                            if (code == 1) {
                                iniciar_sesion();
                                Intent menu = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(menu);
                                finish();
                            }
                        }
                    });
                    AlertDialog dialog = alert.create();
                    dialog.show();

                } catch (JSONException e) {
                    progressDialog.cancel();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String , String> parametros = new HashMap<>();
                parametros.put("email", et_usuario_login.getText().toString());
                parametros.put("pass", et_clave_login.getText().toString());
                parametros.put("valida_login", "");
                return parametros;
            }
        };

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void iniciar_sesion(){
        SharedPreferences sesion = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sesion.edit();
        editor.putString("email", et_usuario_login.getText().toString());
        editor.putString("pass", et_clave_login.getText().toString());
        editor.putBoolean("estado",true);
        editor.commit();
    }

    private void obtener_sesion(){
        SharedPreferences sesion = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        et_usuario_login.setText(sesion.getString("email", ""));
        et_clave_login.setText(sesion.getString("pass", ""));
    }
}