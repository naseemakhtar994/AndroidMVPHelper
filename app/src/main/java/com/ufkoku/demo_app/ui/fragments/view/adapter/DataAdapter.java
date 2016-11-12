package com.ufkoku.demo_app.ui.fragments.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;

import java.util.List;

/**
 * Created by Zwei on 12.11.2016.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.AdapterViewHolder> {

    private LayoutInflater inflater;
    private List<AwesomeEntity> entities;

    public DataAdapter(Context context, List<AwesomeEntity> entities) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.entities = entities;
    }

    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AdapterViewHolder(inflater.inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {
        holder.bind(entities.get(position));
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;

        private AwesomeEntity binded;

        public AdapterViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }

        public void bind(AwesomeEntity entity){
            binded = entity;
            textView.setText(binded.getImportantDataField() + "");
        }

    }

}
