package com.codecamp.hia.tracking.Adapters;

import android.view.View;
import android.view.ViewGroup;

import com.codecamp.hia.tracking.models.Request;

import java.lang.reflect.Array;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterHIA extends RecyclerView.Adapter<myVh> {
    private ArrayList<Request> requests;

    public AdapterHIA(ArrayList<Request> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public myVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull myVh holder, int position) {

    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

}

class myVh extends RecyclerView.ViewHolder{

    public myVh(@NonNull View itemView) {
        super(itemView);
    }
}
