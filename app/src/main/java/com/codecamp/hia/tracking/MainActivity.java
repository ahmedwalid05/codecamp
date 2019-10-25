package com.codecamp.hia.tracking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.codecamp.hia.tracking.Services.UpdateStatusService;
import com.codecamp.hia.tracking.models.Request;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;
    private TextView txtTicketNumber;
    private TextView txtVehicleNumber;
    private Button btnAskForApproval;
    private Button btnAccessAdmin;
    ImageView imageToUpload;
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
        imageToUpload = findViewById(R.id.imgView);
        imageToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallareyIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallareyIntent, RESULT_LOAD_IMAGE);
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
        }
    }

}
