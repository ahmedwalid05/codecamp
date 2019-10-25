package com.codecamp.hia.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panal);
        mDatabase = FirebaseFirestore.getInstance();
        listView = findViewById(R.id.list_view);
        requests = getData();
        ArrayAdapter adapter = new ArrayAdapter<Request>(this, R.layout.activity_admin_panal, requests);
        listView.setAdapter(adapter);
    }

    private ArrayList<Request> getData() {
        final ArrayList<Request> requests = new ArrayList<>();
        mDatabase.collection("requests")
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
                        requests.add(request);
                    }
                    Log.d(TAG, "Added all documents");
                }
            }
        });


        return requests;
    }
//    private class MyAdapter extends BaseAdapter {
//
//        // override other abstract methods here
//
//        @Override
//        public int getCount() {
//            return 0;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup container) {
//            if (convertView == null) {
//                convertView = getLayoutInflater().inflate(R.layout.list_item, container, false);
//            }
//
//            ((TextView) convertView.findViewById(R.id.fldTicketNumber))
//                    .setText(getItem(position).toString());
//            return convertView;
//        }
//    }
}