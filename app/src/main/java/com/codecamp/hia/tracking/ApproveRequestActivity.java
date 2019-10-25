package com.codecamp.hia.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import java.util.List;

public class ApproveRequestActivity extends AppCompatActivity {

    private static final String TAG = "ApproveRequestActivity";
    private String documentReference;
    private DocumentReference mDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_request);
        Intent intent = getIntent();
        documentReference = intent.getStringExtra("request");
        mDocument = FirebaseFirestore.getInstance().collection(Request.REQUEST_COLLECTION_NAME).document(documentReference);

    }

    public Request getRequestData(String DocumentReference) {
        final Request request = new Request();
        mDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot = task.getResult();
                request.setTicketNumber(snapshot.getString(Request.TICKET_NUMBER));
                request.setVehicleNumber(snapshot.getLong(Request.VEHICLE_NUMBER));
                request.setDocumentReference(mDocument.getId());
                request.setPassportPhoto(downloadPhoto(snapshot.getString(Request.IMAGE_URL)));
            }
        });
        return request;
    }

    private Bitmap downloadPhoto(final String url) {

        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, "run: Can't download Image", null);
            } else {
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                    //TODO set image view bitmap or return it
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
