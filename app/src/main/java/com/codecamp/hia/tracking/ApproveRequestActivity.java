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
import android.widget.ImageView;

import com.codecamp.hia.tracking.models.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ApproveRequestActivity extends AppCompatActivity {

    private static final String TAG = "ApproveRequestActivity";
    private String documentReference;
    private DocumentReference mDocument;
    ImageView passportImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_request);
        passportImageView = findViewById(R.id.passport_iv);
        Intent intent = getIntent();
        Bundle happyBundle = intent.getExtras();
        Request request = (Request) happyBundle.getSerializable(Request.REQUEST_COLLECTION_NAME);
        mDocument = FirebaseFirestore.getInstance().collection(Request.REQUEST_COLLECTION_NAME).document(request.getDocumentReference());
        getRequestData(request);

    }

    public void getRequestData(Request request) {
        DownloadThread downloadThread = new DownloadThread(request);
        downloadThread.execute(request);
    }



    class DownloadThread extends AsyncTask<Request,String,Request>{
        Request request;

        public DownloadThread(Request request) {
            this.request = request;
        }

        @Override
        protected Request doInBackground(Request... requests) {
            final Request request = new Request();
            mDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot snapshot = task.getResult();
                    request.setTicketNumber(snapshot.getString(Request.TICKET_NUMBER));
                    request.setVehicleNumber(snapshot.getLong(Request.VEHICLE_NUMBER));
                    request.setDocumentReference(mDocument.getId());
//                    Log.d(TAG, "onComplete: url:" +snapshot.getString(Request.IMAGE_URL));
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    URLConnection urlConnection = null;
                    try {
                        URL uri = new URL(snapshot.getString(Request.IMAGE_URL));
                        urlConnection = (uri.openConnection());
                        Log.d(TAG, "onComplete: ob: "+urlConnection);


                        int statusCode =((HttpURLConnection)urlConnection).getResponseCode();

                        if (statusCode != HttpURLConnection.HTTP_OK) {
                            Log.w(TAG, "run: Can't download Image", null);
                        } else {
                            InputStream inputStream = urlConnection.getInputStream();
//                            InputStream inputStream = uri.openStream();
                            if (inputStream != null) {
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                Log.wtf(TAG, "finished download", null);
                                request.setPassportPhoto(bitmap);
                                return;
                            }
                        }
//                       request.setPassportPhoto(BitmapFactory.decodeStream(new URL(snapshot.getString(Request.IMAGE_URL)).openConnection().getInputStream()));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return request;
        }

//        private Request downloadPhoto(final String urlString, Request request) {
//            HttpURLConnection urlConnection = null;
//            try {
//                URL url = new URL(urlString);
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.connect();
//
//                int statusCode = urlConnection.getResponseCode();
//                if (statusCode != HttpURLConnection.HTTP_OK) {
//                    Log.w(TAG, "run: Can't download Image", null);
//                } else {
//                    InputStream inputStream = urlConnection.getInputStream();
//                    if (inputStream != null) {
//                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                        Log.wtf(TAG, "finished download", null);
//                        request.setPassportPhoto(bitmap);
//                        return request;
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }

        @Override
        protected void onPostExecute(Request request) {
            super.onPostExecute(request);
            Log.d(TAG, "onPostExecute: ");
            passportImageView.setImageBitmap(request.getPassportPhoto());
            //todo set the rest of the data
        }
    }
}
