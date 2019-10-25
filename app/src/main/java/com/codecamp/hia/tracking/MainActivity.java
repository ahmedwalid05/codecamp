package com.codecamp.hia.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


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

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        mDatabase.collection("requests").document()
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        //TODO - inform user about success
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);

                    }
                });


        return false;
    }


}
