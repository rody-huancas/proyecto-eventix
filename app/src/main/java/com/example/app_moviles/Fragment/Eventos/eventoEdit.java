package com.example.app_moviles.Fragment.Eventos;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_moviles.Config.Config;
import com.example.app_moviles.R;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class eventoEdit extends Fragment {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private RequestQueue requestQueue;

    private TextInputEditText m_nombre_evento_e, m_descripcions_evento_e, fecha_evento_e, ubicacion_evento_e, precio_evento_e, categoria_evento_e;

    private Button btn_editar_ev, btn_cancelar_e,btn_eliminar_e;

    private String APIEvento,APIEditar,APIFoto, evento_id,message;
    private int dia, mes, annio, code;

    private Bitmap bitmap;

    private ImageView iv_foto_evento_e;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evento_edit, container, false);
        getActivity().setTitle("Registro de Eventos");

         m_nombre_evento_e = view.findViewById(R.id.m_nombre_evento_e);
         m_descripcions_evento_e = view.findViewById(R.id.m_descripcions_evento_e);
        btn_editar_ev = view.findViewById(R.id.btn_editar_ev);
        btn_cancelar_e = view.findViewById(R.id.btn_cancelar_e);
        ubicacion_evento_e = view.findViewById(R.id.ubicacion_evento_e);
        precio_evento_e = view.findViewById(R.id.precio_evento_e);
        categoria_evento_e = view.findViewById(R.id.categoria_evento_e);
        fecha_evento_e = view.findViewById(R.id.fecha_evento_e);
        iv_foto_evento_e = view.findViewById(R.id.iv_foto_evento_e);
        btn_eliminar_e = view.findViewById(R.id.btn_eliminar_e);

        Bundle datosRecuperados = getArguments();
        evento_id =datosRecuperados.getString("evento_id");
        Toast.makeText(getContext(), evento_id, Toast.LENGTH_SHORT).show();

        final Calendar calendar = Calendar.getInstance();
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        mes = calendar.get(Calendar.MONTH);
        annio = calendar.get(Calendar.YEAR);

        Config config = new Config();
        APIEvento = config.getAPI_URL() + "api_evento.php?listar_evento_id&idEvento="+evento_id;
        APIEditar = config.getAPI_URL() + "api_evento.php";

        ObtenerEvento();

        btn_cancelar_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_fragment, new eventoFragment());
                fragmentTransaction.commit();
            }
        });

        fecha_evento_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Calendar(fecha_evento_e);
            }
        });

        btn_editar_ev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActualizarEvento();
            }
        });

        iv_foto_evento_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarImagen();
            }
        });

        btn_eliminar_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EliminarEvento();
            }
        });

        return view;
    }

    private void seleccionarImagen(){
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galeria.setType("image/*");
        startActivityForResult(galeria.createChooser(galeria,"Seleccione una imagen"),10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        try {
            Uri ruta = data.getData();
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), ruta);
            iv_foto_evento_e.setImageURI(ruta);
        }catch (IOException e){
            e.printStackTrace();
        }

    }


    private String conversionBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);

        byte[] imagebyte = byteArrayOutputStream.toByteArray();

        String encodedImage = Base64.getEncoder().encodeToString(imagebyte);

        return  encodedImage;
    }


    private void Calendar(TextInputEditText textInputEditText){

        DatePickerDialog fecha1 = new DatePickerDialog(getContext(), android.app.AlertDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//                int m=i1+1;
                textInputEditText.setText(formatoFecha(i2,i1,i));
            }
        },annio,mes,dia);

        fecha1.show();

    }

    private String formatoFecha(int d, int m,int a){
        String s_mes,dia,fecha="";
        int mes;

        if(d<10){
            dia="0"+d;
        }else{
            dia=String.valueOf(d);
        }

        mes=m+1;

        if(mes<10){
            s_mes="0"+mes;
        }else{
            s_mes=String.valueOf(mes);
        }

        fecha=a+"/"+s_mes+"/"+dia;

        return fecha;
    }

    private void ObtenerEvento(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(APIEvento, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;

                for (int i = 0; i< response.length(); i++){
                    try {

                        jsonObject = response.getJSONObject(i);
                        Config config = new Config();

                        m_nombre_evento_e.setText(jsonObject.getString("nombreEvento"));
                        m_descripcions_evento_e.setText(jsonObject.getString("descripcionEvento"));
                        fecha_evento_e.setText(jsonObject.getString("fechaEvento"));
                        ubicacion_evento_e.setText(jsonObject.getString("direccionEvento"));
                        precio_evento_e.setText(jsonObject.getString("precioEvento"));
                        categoria_evento_e.setText(jsonObject.getString("categoriaEvento"));
                        APIFoto = config.getAPI_URL() + "fotos_evento/"+jsonObject.getString("fotoEvento")+".png";
                        Picasso.get().load(APIFoto).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .error(R.mipmap.ic_launcher).into(iv_foto_evento_e);
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

    private void ActualizarEvento(){
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Editando","Espere un momento", false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIEditar, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.cancel();
                    JSONObject jsonObject = new JSONObject(response);
                    message = jsonObject.getString("message");
                    code = jsonObject.getInt("code");

                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Actualización de Eventos");
                    alert.setMessage(message);
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            if(code == 1){
                                fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.container_fragment, new eventoFragment());
                                fragmentTransaction.commit();
                            }
                        }
                    });
                    AlertDialog dialog = alert.create();
                    dialog.show();

                } catch (JSONException e) {
                    progressDialog.cancel();
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
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

                SharedPreferences sesion = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);


                Map<String,String> parametros = new HashMap<>();
                parametros.put("idEvento",evento_id );
                parametros.put("nombreEvento", m_nombre_evento_e.getText().toString());
                parametros.put("descripcionEvento", m_descripcions_evento_e.getText().toString());
                parametros.put("direccionEvento", ubicacion_evento_e.getText().toString());
                parametros.put("precioEvento", precio_evento_e.getText().toString());
                parametros.put("categoriaEvento", categoria_evento_e.getText().toString());
                parametros.put("fechaEvento", fecha_evento_e.getText().toString());
                parametros.put("idUsuario", sesion.getString("idUsuario",""));
                parametros.put("fotoEvento",  conversionBase64(bitmap));
                parametros.put("actualizar_evento","");
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void EliminarEvento(){
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Eliminando","Espere un momento", false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIEditar, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.cancel();
                    JSONObject jsonObject = new JSONObject(response);
                    message = jsonObject.getString("message");
                    code = jsonObject.getInt("code");

                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Eliminación de Evento");
                    alert.setMessage(message);
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            if(code == 1){
                                fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.container_fragment, new eventoFragment());
                                fragmentTransaction.commit();
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

                parametros.put("idEvento", evento_id);
                parametros.put("eliminar_evento","");

                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    }
