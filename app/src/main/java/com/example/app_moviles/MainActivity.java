package com.example.app_moviles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_moviles.Config.Config;
import com.example.app_moviles.Fragment.Eventos.eventoFragment;
import com.example.app_moviles.Fragment.MainFragment;
import com.example.app_moviles.Fragment.Users.UserEdit;
import com.example.app_moviles.Fragment.Users.UserPerfil;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private TextView user_name_header, user_email_header;
    private String user_email, api_Usuarios, apiFoto;
    private RequestQueue requestQueue;
    private ImageView iv_foto_menu;

    Config config = new Config();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Aplicaciones Móviles");

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);



        SharedPreferences sesion = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        user_email = sesion.getString("email","");

        api_Usuarios = config.getAPI_URL()+"api_usuario.php?consulta_usuario&email="+user_email;




        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_fragment, new MainFragment());
        fragmentTransaction.commit();

        View headerMenu = navigationView.getHeaderView(0); //para acceder a usuario y correo del menu
        user_name_header = headerMenu.findViewById(R.id.user_name_header);
        user_email_header = headerMenu.findViewById(R.id.user_email_header);
        iv_foto_menu = headerMenu.findViewById(R.id.iv_foto_menu);

        obtenerDatosUsuario();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        Fragment fragment = new Fragment();

        if(item.getItemId()==R.id.inicio){
            fragment = new MainFragment();
        }else if(item.getItemId()==R.id.men_Evento){
            fragment = new eventoFragment();
        }else if(item.getItemId()==R.id.editar_usuario){
            fragment = new UserEdit();
        }else if(item.getItemId()==R.id.perfil_usuario){
            fragment = new UserPerfil();
        }else if(item.getItemId()==R.id.cerrar_sesion){
            /*
            Intent logout = new Intent(this, LoginActivity.class);
            startActivity(logout);
            finish();
             */

            AlertDialog.Builder alert = new  AlertDialog.Builder(this);
            alert.setTitle("RSU PROJECT");
            alert.setMessage("¿Seguro de cerrar sesión");
            alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    cerrar_sesion();
                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog alertDialog = alert.create();
            alertDialog.show();

            fragment = new MainFragment();
        }

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment, fragment);
        fragmentTransaction.commit();
        return false;
    }

    private void cerrar_sesion(){
        SharedPreferences sesion = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sesion.edit();
        editor.clear();
        editor.putBoolean("estado", false);
        editor.commit();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void obtenerDatosUsuario(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(api_Usuarios, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;


                    try {
                        for (int i = 0; i< response.length(); i++){
                        jsonObject = response.getJSONObject(i);
                    }

                    apiFoto = config.getAPI_URL() + "photos_users/"+jsonObject.getString("idUsuario")+".png";
                    Picasso.get().load(apiFoto).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .error(R.mipmap.ic_launcher).into(iv_foto_menu);

                    SharedPreferences sesion = getSharedPreferences("sesion", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sesion.edit();
                    editor.putString("idUsuario", jsonObject.getString("idUsuario"));
                    editor.putString("nombre", jsonObject.getString("nombre"));
                    editor.putString("apellidos", jsonObject.getString("apellidos"));
                    editor.putString("direccion", jsonObject.getString("direccion"));
                    editor.putString("descripcion", jsonObject.getString("descripcion"));
                    editor.putString("photo", jsonObject.getString("photo"));
                    editor.commit();


                    user_name_header.setText(jsonObject.getString("nombre"));
                    user_email_header.setText(jsonObject.getString("email"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

            }
        }, error -> Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show());

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

}