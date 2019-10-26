package com.codecamp.hia.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codecamp.hia.tracking.models.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ApproveRequestActivity extends AppCompatActivity {

    private static final String TAG = "ApproveRequestActivity";
    private DocumentReference mDocument;
    ImageView passportImageView;
    private EditText txtTicketNumber;
    private EditText txtVehicleNumber;
    private Button btnApprove;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_request);
        passportImageView = findViewById(R.id.passport_iv);
        txtTicketNumber = findViewById(R.id.txtTicketNumber);
        txtVehicleNumber = findViewById(R.id.txtVehicleNumber);
        btnApprove  = findViewById(R.id.btnApprove);
        Intent intent = getIntent();
        Bundle happyBundle = intent.getExtras();
        Request request = (Request) happyBundle.getSerializable(Request.REQUEST_COLLECTION_NAME);
        mDocument = FirebaseFirestore.getInstance().collection(Request.REQUEST_COLLECTION_NAME).document(request.getDocumentReference());
        getRequestData(request);
        txtTicketNumber.setText(request.getTicketNumber());
        txtVehicleNumber.setText(request.getVehicleNumber()+"");

    }

    public void getRequestData(Request request) {
        DownloadThread downloadThread = new DownloadThread(request);
        downloadThread.execute(request);
    }


    class DownloadThread extends AsyncTask<Request, Request, Request> {
        Request request;
        private boolean done = false;

        public DownloadThread(Request request) {
            this.request = request;
        }

        @Override
        protected Request doInBackground(Request... requests) {
            final Request request = new Request();

            btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    approveRequest();
                }
            });

            mDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    DocumentSnapshot snapshot = task.getResult();
                    request.setTicketNumber(snapshot.getString(Request.TICKET_NUMBER));
                    request.setVehicleNumber(snapshot.getLong(Request.VEHICLE_NUMBER));
                    request.setDocumentReference(mDocument.getId());
                    if(snapshot.getString(Request.IMAGE_URL)==null) {
                        return;
                    }
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    URLConnection urlConnection = null;
                    try {
                        URL uri = new URL(snapshot.getString(Request.IMAGE_URL));
                        urlConnection = (uri.openConnection());
                        Log.d(TAG, "onComplete: ob: " + urlConnection);


                        int statusCode = ((HttpURLConnection) urlConnection).getResponseCode();

                        if (statusCode != HttpURLConnection.HTTP_OK) {
                            Log.w(TAG, "run: Can't download Image", null);
                        } else {
                            InputStream inputStream = urlConnection.getInputStream();
                            if (inputStream != null) {
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                Log.wtf(TAG, "finished download", null);
                                request.setPassportPhoto(bitmap);
                                done = true;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            while (!done) ;
            return request;
        }


        @Override
        protected void onPostExecute(Request request) {
            super.onPostExecute(request);
            Log.wtf(TAG, "onPostExecute: ");
            passportImageView.setImageBitmap(request.getPassportPhoto());

        }
    }

    private void approveRequest() {
        mDocument.update(Request.IS_APPROVED, true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: updated");
                HashMap<String, Object> data = new HashMap<>();
                data.put(Request.STATUS_FIELD, 0);
                data.put(Request.TIMESTAMP_FIELD, FieldValue.serverTimestamp());
                mDocument.collection(Request.PROGRESS_COLLECTION_NAME)
                        .document()
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ApproveRequestActivity.this, "Approved", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });
    }
}
