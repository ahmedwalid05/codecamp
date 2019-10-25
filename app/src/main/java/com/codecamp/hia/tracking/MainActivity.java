package com.codecamp.hia.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.codecamp.hia.tracking.Services.UpdateStatusService;
import com.codecamp.hia.tracking.models.Request;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView txtTicketNumber;
    private TextView txtVehicleNumber;
    private Button btnAskForApproval;
    private Button btnAccessAdmin;
    private FirebaseFirestore mDatabase;
    public static final String TAG = "MainActivity";
    public static final String DOCUMENT_REF = "documentRef";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//TODO - check if the app is tracking a request

        mDatabase = FirebaseFirestore.getInstance();

        txtTicketNumber = findViewById(R.id.editTicket);
        txtVehicleNumber = findViewById(R.id.editVehicle);
        btnAskForApproval = findViewById(R.id.btnApprove);
        btnAccessAdmin = findViewById(R.id.btnAccessAdmin);

        btnAskForApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNewRequest(txtTicketNumber.getText().toString(), Long.parseLong(txtVehicleNumber.getText().toString()));
            }
        });
        btnAccessAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminPanelActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean writeNewRequest(String ticketNumber, long vehicleNumber) {

        HashMap<String, Object> data = new HashMap<>();
        data.put(Request.TICKET_NUMBER, ticketNumber);
        data.put(Request.VEHICLE_NUMBER, vehicleNumber);
        data.put(Request.IS_APPROVED, false);
        final String id =mDatabase.collection("requests").document().getId();
        mDatabase.collection("requests").document(id)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(MainActivity.this, "Request sent", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, UpdateStatusService.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(DOCUMENT_REF, id);
                        intent.putExtras(bundle);
                        startService(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(MainActivity.this, "Request failed to send", Toast.LENGTH_LONG).show();

                    }
                });


        return false;
    }


}
