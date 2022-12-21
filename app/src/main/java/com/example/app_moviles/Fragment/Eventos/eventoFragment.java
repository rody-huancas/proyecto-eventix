package com.example.app_moviles.Fragment.Eventos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
import com.example.app_moviles.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class eventoFragment extends Fragment {

    private FloatingActionButton btn_nuevo_evento;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private RequestQueue requestQueue;
    private String Api;
    private Bundle evento_data;
    private ListView lv_evento_list;
    private Button btn_verevento;

    private eventoAdapter eventoadapter;
    private List<eventos> ListEvento;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_eventos, container, false);

        getActivity().setTitle("Listado de Eventos");

        lv_evento_list = view.findViewById(R.id.lv_evento_list);

        btn_nuevo_evento = (FloatingActionButton) view.findViewById(R.id.btn_nuevo_evento);

        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        btn_nuevo_evento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction.replace(R.id.container_fragment, new eventoCreate());
                fragmentTransaction.commit();
            }
        });

        SharedPreferences sesion = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        Config config = new Config();
        Api = config.getAPI_URL()+"api_evento.php?listar_evento_usuario&idUsuario="+sesion.getString("idUsuario","");

        listarEventos();

        lv_evento_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                evento_data = new Bundle();
                evento_data.putString("evento_id", view.getTag().toString());

                Fragment fragment = new eventoEdit();
                fragment.setArguments(evento_data);

                fragmentTransaction.replace(R.id.container_fragment, fragment);
                fragmentTransaction.commit();
            }
        });
        return view;

    }

    private void listarEventos(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Api, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ListEvento = new ArrayList<>();
                JSONObject jsonObject = null;
                for(int i=0; i<response.length(); i++){
                    try {
                        jsonObject = response.getJSONObject(i);
                        ListEvento.add(new eventos(jsonObject.getInt("idEvento"),
                                jsonObject.getInt("estadoEvento"),
                                jsonObject.getDouble("precioEvento"),
                                jsonObject.getString("nombreEvento"),
                                jsonObject.getString("descripcionEvento"),
                                jsonObject.getString("direccionEvento"),
                                jsonObject.getString("categoriaEvento"),
                                jsonObject.getString("fechaEvento"),
                                jsonObject.getString("cant_meinteresa"),
                                jsonObject.getString("nombre"),
                                jsonObject.getString("fotoEvento")

                                ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                eventoadapter = new eventoAdapter(getContext(), ListEvento);
                lv_evento_list.setAdapter(eventoadapter);
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


