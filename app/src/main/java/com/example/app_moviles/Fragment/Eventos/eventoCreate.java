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
import android.widget.AdapterView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_moviles.Config.Config;
import com.example.app_moviles.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class eventoCreate extends Fragment {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private RequestQueue requestQueue;

    private TextInputEditText m_nombre_evento,m_descripcions_evento,ubicacion_evento,precio_evento,categoria_evento,fecha_evento;

    private Button btn_registrar_ev, btn_cancelar_ev;

    private String APIEvento, message;
    private int dia, mes, annio,code;

    private Bitmap bitmap;

    private ImageView iv_foto_evento_c;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evento_create, container, false);
        getActivity().setTitle("Registro de Casos");

        iv_foto_evento_c = view.findViewById(R.id.iv_foto_evento_c);
        btn_registrar_ev = view.findViewById(R.id.btn_registrar_ev);
        btn_cancelar_ev = view.findViewById(R.id.btn_cancelar_ev);
        m_nombre_evento = view.findViewById(R.id.m_nombre_evento);
        m_descripcions_evento = view.findViewById(R.id.m_descripcions_evento);
        ubicacion_evento = view.findViewById(R.id.ubicacion_evento);
        precio_evento = view.findViewById(R.id.precio_evento);
        categoria_evento = view.findViewById(R.id.categoria_evento);
        fecha_evento = view.findViewById(R.id.fecha_evento);

        final Calendar calendar = Calendar.getInstance();
        dia=calendar.get(Calendar.DAY_OF_MONTH);
        mes=calendar.get(Calendar.MONTH);
        annio=calendar.get(Calendar.YEAR);

        Config config = new Config();
        APIEvento = config.getAPI_URL()+"api_evento.php";




        btn_cancelar_ev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_fragment, new eventoFragment());
                fragmentTransaction.commit();
            }
        });

        fecha_evento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar(fecha_evento);
            }
        });

        btn_registrar_ev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrarEvento();
            }
        });

        iv_foto_evento_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarImagen();
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
            iv_foto_evento_c.setImageURI(ruta);
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

    private void RegistrarEvento(){

        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Registrando","Espere un momento", false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIEvento, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.cancel();
                    JSONObject jsonObject = new JSONObject(response);
                    message = jsonObject.getString("message");
                    code = jsonObject.getInt("code");

                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Registro de Casos");
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
                parametros.put("nombreEvento", m_nombre_evento.getText().toString());
                parametros.put("descripcionEvento", m_descripcions_evento.getText().toString());
                parametros.put("direccionEvento", ubicacion_evento.getText().toString());
                parametros.put("precioEvento", precio_evento.getText().toString());
                parametros.put("categoriaEvento", categoria_evento.getText().toString());
                parametros.put("fechaEvento", fecha_evento.getText().toString());
                parametros.put("idUsuario", sesion.getString("idUsuario",""));
                parametros.put("fotoEvento",  conversionBase64(bitmap));
                parametros.put("registrar_evento","");
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}
