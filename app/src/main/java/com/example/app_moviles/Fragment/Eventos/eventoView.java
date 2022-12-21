package com.example.app_moviles.Fragment.Eventos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_moviles.Config.Config;
import com.example.app_moviles.Fragment.MainFragment;
import com.example.app_moviles.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class eventoView extends Fragment {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private RequestQueue requestQueue;
    private String Api,APIFoto,evento_id;
    private Bundle evento_data;
    private Button btn_regresar_ev;

    private TextView tv_nombre_evento, tv_fecha_evento,tv_descripcion,tv_direccion_evento,tv_precio_evento,tv_categoria_evento;
    private ImageView iv_evento;
    private eventoAdapter eventoadapter;
    private List<eventos> ListEvento;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ventana_evento, container, false);

        getActivity().setTitle("Listado de Eventos");


        Bundle datosRecuperados = getArguments();
        evento_id =datosRecuperados.getString("evento_id");
        Toast.makeText(getContext(), evento_id, Toast.LENGTH_SHORT).show();

        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        tv_nombre_evento = view.findViewById(R.id.tv_nombre_evento);
        tv_fecha_evento = view.findViewById(R.id.tv_fecha_evento);
        tv_descripcion = view.findViewById(R.id.tv_descripcion);
        iv_evento = view.findViewById(R.id.iv_evento);
        tv_direccion_evento = view.findViewById(R.id.tv_direccion_evento);
        tv_precio_evento = view.findViewById(R.id.tv_precio_evento);
        tv_categoria_evento = view.findViewById(R.id.tv_categoria_evento);
        btn_regresar_ev = view.findViewById(R.id.btn_regresar_ev);

        Config config = new Config();
        Api = config.getAPI_URL() + "api_evento.php?listar_evento_id&idEvento="+evento_id;

        obtenerEvento();

        btn_regresar_ev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_fragment, new MainFragment());
                fragmentTransaction.commit();
            }
        });

        return view;

    }

    private void obtenerEvento(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Api, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;

                for (int i = 0; i< response.length(); i++){
                    try {

                        jsonObject = response.getJSONObject(i);
                        Config config = new Config();

                        tv_nombre_evento.setText(jsonObject.getString("nombreEvento"));
                        tv_fecha_evento.setText(jsonObject.getString("fechaEvento"));
                        tv_descripcion.setText(jsonObject.getString("descripcionEvento"));
                        tv_direccion_evento.setText(jsonObject.getString("direccionEvento"));
                        tv_precio_evento.setText("S/."+jsonObject.getString("precioEvento"));
                        tv_categoria_evento.setText(jsonObject.getString("categoriaEvento"));

                        APIFoto = config.getAPI_URL() + "fotos_evento/"+jsonObject.getString("fotoEvento")+".png";
                        Picasso.get().load(APIFoto).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .error(R.mipmap.ic_launcher).into(iv_evento);
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
