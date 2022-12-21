package com.example.app_moviles.Fragment.Users;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_moviles.Config.Config;
import com.example.app_moviles.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UserPerfil extends Fragment {

    TextView tv_nombre_perfil, tv_apellidos_perfil, tv_email_perfil, tv_direccion_perfil, tv_descripcion_perfil;
    ImageView imagen_foto_perfil;
    private String api_obtener_usuario, apiUsuario, message, apiPhoto, id_usuario;
    private RequestQueue requestQueue;
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        getActivity().setTitle("Mi perfil");

        tv_nombre_perfil = view.findViewById(R.id.tv_nombre_perfil);
        tv_apellidos_perfil = view.findViewById(R.id.tv_apellidos_perfil);
        tv_email_perfil = view.findViewById(R.id.tv_email_perfil);
        tv_direccion_perfil = view.findViewById(R.id.tv_direccion_perfil);
        tv_descripcion_perfil = view.findViewById(R.id.tv_descripcion_perfil);
        imagen_foto_perfil = view.findViewById(R.id.imagen_foto_perfil);

        Config config = new Config();
        SharedPreferences sesion = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        api_obtener_usuario = config.getAPI_URL()+"api_usuario.php?listar_usuarios_email&email=" + sesion.getString("email", "");
        apiUsuario = config.getAPI_URL() + "api_usuario.php";

        obtenerUsuario();

        return view;

    }

    private void obtenerUsuario(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(api_obtener_usuario, new Response.Listener<JSONArray>() {
            Config config = new Config();
            SharedPreferences sesion = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i< response.length(); i++){
                    try {
                        jsonObject = response.getJSONObject(i);

                        tv_nombre_perfil.setText(jsonObject.getString("nombre"));
                        tv_apellidos_perfil.setText(jsonObject.getString("apellidos"));
                        tv_email_perfil.setText(jsonObject.getString("email"));
                        tv_direccion_perfil.setText(jsonObject.getString("direccion"));
                        tv_descripcion_perfil.setText(jsonObject.getString("descripcion"));
                        apiPhoto = config.getAPI_URL()+"photos_users/"+sesion.getString("idUsuario","")+".png";

                        Picasso.get().load(apiPhoto).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .error(R.drawable.user_add).into(imagen_foto_perfil);


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
}
