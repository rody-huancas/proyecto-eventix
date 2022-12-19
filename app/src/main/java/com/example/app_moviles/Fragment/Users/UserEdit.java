package com.example.app_moviles.Fragment.Users;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_moviles.Config.Config;
import com.example.app_moviles.Fragment.MainFragment;
import com.example.app_moviles.R;
import com.example.app_moviles.RegUserActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class UserEdit extends Fragment {

    private TextInputEditText usuario_nombres_e, usuario_nacimiento_e, usuario_email_e, usuario_clave_e;
    private Button btn_actualizar_usuario, btn_cancelar_usuario_e;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private String api_obtener_usuario, apiUsuario, message, apiPhoto;
    private int dia, mes, annio, code;

    private ImageView iv_foto_perfil;

    private RequestQueue requestQueue;
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_edit, container, false);
        getActivity().setTitle("Actualizar datos de usuario");

        usuario_nombres_e = view.findViewById(R.id.usuario_nombres_e);
        usuario_nacimiento_e = view.findViewById(R.id.usuario_nacimiento_e);
        usuario_email_e = view.findViewById(R.id.usuario_email_e);
        usuario_clave_e = view.findViewById(R.id.usuario_clave_e);
        iv_foto_perfil = view.findViewById(R.id.iv_foto_perfil);

        btn_actualizar_usuario = view.findViewById(R.id.btn_actualizar_usuario);
        btn_cancelar_usuario_e = view.findViewById(R.id.btn_cancelar_usuario_e);

        final Calendar calendar = Calendar.getInstance();
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        mes = calendar.get(Calendar.MONTH);
        annio = calendar.get(Calendar.YEAR);


        fragmentManager =  getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        btn_cancelar_usuario_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction.replace(R.id.container_fragment, new MainFragment());
                fragmentTransaction.commit();
            }
        });

        btn_actualizar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActualizarUsuario();
            }
        });

        usuario_nacimiento_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar(usuario_nacimiento_e);
            }
        });

        Config config = new Config();
        SharedPreferences sesion = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        api_obtener_usuario = config.getAPI_URL()+"api_usuario.php?listar_usuarios_email&email=" + sesion.getString("email", "");
        apiUsuario = config.getAPI_URL() + "api_usuario.php";

        obtenerUsuario();

        iv_foto_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarImagen();
            }
        });

        apiPhoto = config.getAPI_URL()+"photos_users/"+sesion.getString("id","")+".png";

        Picasso.get().load(apiPhoto).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .error(R.drawable.user_add).into(iv_foto_perfil);

        return view;
    }

    private void obtenerUsuario(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(api_obtener_usuario, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i< response.length(); i++){
                    try {
                        jsonObject = response.getJSONObject(i);

                        usuario_nombres_e.setText(jsonObject.getString("names"));
                        usuario_nacimiento_e.setText(jsonObject.getString("birthdate"));
                        usuario_email_e.setText(jsonObject.getString("email"));
                        usuario_clave_e.setText(jsonObject.getString("pass"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void Calendar(TextInputEditText textInputEditText){

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.app.AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                textInputEditText.setText(formatoFecha(i2, i1, i));
            }
        }, annio,mes,dia);

        datePickerDialog.show();

    }

    private String formatoFecha(int d, int m, int a){
        String s_mes, dia, fecha="";
        int mes;

        if(d < 10){
            dia = "0"+d;
        }else{
            dia = String.valueOf(d);
        }

        mes = m+1;

        if (mes < 10){
            s_mes = "0"+mes;
        }else{
            s_mes = String.valueOf(mes);
        }

        fecha = a+"/"+s_mes+"/"+dia;
        return fecha;
    }

    private void seleccionarImagen(){
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galeria.setType("image/*");
        startActivityForResult(galeria.createChooser(galeria,"Seleccione una imagen"),10);
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        try {
            Uri ruta = data.getData();
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), ruta);
            iv_foto_perfil.setImageURI(ruta);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private String conversionBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        byte[] imagebyte = byteArrayOutputStream.toByteArray();

        String encodedImage = Base64.getEncoder().encodeToString(imagebyte);

        return encodedImage;
    }

    private void ActualizarUsuario(){

        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Actualizando","Espere un momento", false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiUsuario, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.cancel();
                    JSONObject jsonObject = new JSONObject(response);
                    message = jsonObject.getString("message");
                    code = jsonObject.getInt("code");

                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Actualización de Usuarios");
                    alert.setMessage(message);
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            if(code == 1){
                                /*fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.container_fragment, new FamiliesFragment());
                                fragmentTransaction.commit();*/

                                SharedPreferences sesion =getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sesion.edit();

                                editor.putString("names", usuario_nombres_e.getText().toString());
                                editor.putString("birthdate", usuario_nacimiento_e.getText().toString());
                                editor.putString("email", usuario_email_e.getText().toString());
                                editor.putString("pass", usuario_clave_e.getText().toString());
                                //editor.putString("photo", jsonObject.getString("photo"));
                                editor.commit();

                            }
                        }
                    });
                    AlertDialog dialog = alert.create();
                    dialog.show();

                } catch (JSONException e) {
                    progressDialog.cancel();
                    //Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    //e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> parametros = new HashMap<>();

                SharedPreferences sesion =getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

                parametros.put("id", sesion.getString("id",""));
                parametros.put("names", usuario_nombres_e.getText().toString());
                parametros.put("birthdate", usuario_nacimiento_e.getText().toString());
                parametros.put("email", usuario_email_e.getText().toString());
                parametros.put("pass", usuario_clave_e.getText().toString());
                parametros.put("photo", conversionBase64(bitmap));
                parametros.put("actualizar_usuario","");

                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}