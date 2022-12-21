package com.example.app_moviles.Fragment.Eventos;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app_moviles.R;

import java.util.List;

public class eventoAdapter extends BaseAdapter {
    private Context context;
    private List<eventos> ListEvento;

    private TextView name_evento, descripcion_evento;

    private ImageView iv_foto_evento_list;

    public eventoAdapter(Context context,List<eventos> listEvento){
        this.context = context;
        ListEvento = listEvento;
    }

    @Override
    public int getCount() {
        return ListEvento.size();
    }

    @Override
    public Object getItem(int i) {
        return ListEvento.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View vista = View.inflate(context, R.layout.item_evento, null);

        name_evento =vista.findViewById(R.id.name_evento);

        descripcion_evento = vista.findViewById(R.id.descripcion_evento);

        name_evento.setText(ListEvento.get(i).getNombre());
        descripcion_evento.setText(ListEvento.get(i).getDescripcion());

        return vista;
    }

}
