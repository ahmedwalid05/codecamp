package com.codecamp.hia.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;

import com.codecamp.hia.tracking.Adapters.AdapterHIA;
import com.codecamp.hia.tracking.models.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminPanelActivity extends AppCompatActivity {

    private static final String TAG = "AdminPanelActivity";
    ArrayList<Request> requests = new ArrayList<>();
    private FirebaseFirestore mDatabase;
    private RecyclerView recyclerView;
    private LinearLayoutManager lm;
    AdapterHIA adapterHIA;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panal);
        mDatabase = FirebaseFirestore.getInstance();
        requests = getData();
        recyclerView = findViewById(R.id.requests_recycler);
        lm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        adapterHIA = new AdapterHIA(requests,this);
        recyclerView.setAdapter(adapterHIA);
    }

    private ArrayList<Request> getData() {
        final ArrayList<Request> requests = new ArrayList<>();
        mDatabase.collection(Request.REQUEST_COLLECTION_NAME)
                .whereEqualTo("isApproved", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> snapshotList = task.getResult().getDocuments();
                if(snapshotList.size()==0){
                    Log.d(TAG, "Collection Empty");
                }else{
                    for(int i=0;i<snapshotList.size();i++){
                        Request request = new Request();
                        request.setTicketNumber(snapshotList.get(i).getString(Request.TICKET_NUMBER));
                        request.setVehicleNumber(snapshotList.get(i).getLong(Request.VEHICLE_NUMBER));
                        request.setApproved(snapshotList.get(i).getBoolean(Request.IS_APPROVED));
                        request.setDocumentReference(snapshotList.get(i).getId());
                        requests.add(request);
                    }
                    adapterHIA.setRequests(requests);
                    adapterHIA.notifyDataSetChanged();
                    Log.d(TAG, "Added all documents");

                }
            }
        });


        return requests;
    }

}
