package com.codecamp.hia.tracking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences.*;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;
    private TextView txtTicketNumber;
    private TextView txtVehicleNumber;
    private Button btnAskForApproval;
    private Button btnAccessAdmin;
    private FirebaseStorage storage;
    private ImageView imageToUpload;
    private FirebaseFirestore mDatabase;
    public static final String TAG = "MainActivity";
    public static final String DOCUMENT_REF = "documentRef";
    public static final String PREFRENECES_NAME = "shared";
    private SharedPreferences preferences;
    private Editor editor;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(PREFRENECES_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        String ticketNumber = preferences.getString(Request.TICKET_NUMBER, null);
        if (ticketNumber != null) {
            String id = preferences.getString(DOCUMENT_REF, null);
            Intent intent = new Intent(this, TrackingActivity.class);
            intent.putExtra(DOCUMENT_REF, id);
            startActivity(intent);
            finish();
        }
        mDatabase = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

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
                if (!txtTicketNumber.getText().toString().isEmpty() && !txtVehicleNumber.getText().toString().isEmpty())
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

    private boolean writeNewRequest(final String ticketNumber, final long vehicleNumber) {


        final StorageReference storageRef = storage.getReference("private/passports/" + vehicleNumber + ".jpg");
        final String id = mDatabase.collection("requests").document().getId();

        Log.d(TAG, "writeNewRequest: " + storageRef.getDownloadUrl());

        Bitmap bitmap = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] dataByte = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(dataByte);
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String downloadUrl = downloadUri.toString();
                    HashMap<String, Object> data = new HashMap<>();
                    data.put(Request.TICKET_NUMBER, ticketNumber);
                    data.put(Request.VEHICLE_NUMBER, vehicleNumber);
                    data.put(Request.IS_APPROVED, false);
                    Log.d(TAG, "onSuccess: " + downloadUrl);
                    data.put(Request.IMAGE_URL, downloadUrl);
                    mDatabase.collection("requests").document(id)
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    Toast.makeText(MainActivity.this, "Request sent", Toast.LENGTH_LONG).show();
//                                    Intent intent = new Intent(MainActivity.this, UpdateStatusService.class);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putString(DOCUMENT_REF, id);
//                                    intent.putExtras(bundle);
//                                    startService(intent);

                                    editor.putString(Request.TICKET_NUMBER, ticketNumber);
                                    editor.putString(DOCUMENT_REF, id);
                                    editor.commit();

                                    Intent trackActivityIntent = new Intent(MainActivity.this, TrackingActivity.class);
                                    trackActivityIntent.putExtra(DOCUMENT_REF, id);
                                    startActivity(trackActivityIntent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                    Toast.makeText(MainActivity.this, "Request failed to send", Toast.LENGTH_LONG).show();
                                }
                            });

                } else {
                    // Handle failures
                    // ...
                }
            }
        });
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//            }
//        });

        return false;


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
        }
    }

}
