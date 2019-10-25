package com.codecamp.hia.tracking.models;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.Serializable;

public class Request implements Serializable {

    public static final String REQUEST_COLLECTION_NAME = "requests";
    public static final String PROGRESS_COLLECTION_NAME = "progress";
    private String ticketNumber;
    private long vehicleNumber;
    private boolean isApproved;
    private Bitmap passportPhoto;
    private String documentReference;
    public static final String TICKET_NUMBER ="ticketNumber";
    public static final String VEHICLE_NUMBER ="vehicleNumber";
    public static final String IS_APPROVED = "isApproved";
    public static final String IMAGE_URL = "imageURL";


    public String getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(String documentReference) {
        this.documentReference = documentReference;
    }

    public Bitmap getPassportPhoto() {
        return passportPhoto;
    }

    public void setPassportPhoto(Bitmap passportPhoto) {
        this.passportPhoto = passportPhoto;
    }

    public Request(String ticketNumber, long vehicleNumber, boolean isApproved) {
        this.ticketNumber = ticketNumber;
        this.vehicleNumber = vehicleNumber;
        this.isApproved = isApproved;

    }

    public Request() {
        isApproved =false;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public long getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(long vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
