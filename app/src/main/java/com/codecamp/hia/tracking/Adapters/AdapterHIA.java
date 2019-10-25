package com.codecamp.hia.tracking.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codecamp.hia.tracking.ApproveRequestActivity;
import com.codecamp.hia.tracking.R;
import com.codecamp.hia.tracking.models.Request;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterHIA extends RecyclerView.Adapter<MyVh> {
    private ArrayList<Request> requests;
    private Context context;

    public AdapterHIA() {
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<Request> requests) {
        this.requests = requests;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public AdapterHIA(ArrayList<Request> requests, Context context) {
        this.requests = requests;
        this.context = context;
    }

    public AdapterHIA(ArrayList<Request> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public MyVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.request_view_holder, parent,false);
        MyVh vh = new MyVh(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyVh holder, final int position) {
        holder.ticketTV.setText(requests.get(position).getTicketNumber());
        holder.ticketTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callActivity(requests.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    private void callActivity(Request request){
        Intent intent = new Intent(context, ApproveRequestActivity.class);
        Bundle happyBundle = new Bundle();
        happyBundle.putSerializable(Request.REQUEST_COLLECTION_NAME,request);
        intent.putExtras(happyBundle);
        context.startActivity(intent);
    }
}

class MyVh extends RecyclerView.ViewHolder{
    TextView ticketTV;

    public MyVh(@NonNull View itemView) {
        super(itemView);
        ticketTV = itemView.findViewById(R.id.ticket_number);
    }
}
