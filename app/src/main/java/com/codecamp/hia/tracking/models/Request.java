package com.codecamp.hia.tracking.models;

public class Request {

    private String ticketNumber;
    private long vehicleNumber;
    private boolean isApproved;

    public static final String TICKET_NUMBER ="ticketNumber";
    public static final String VEHICLE_NUMBER ="vehicleNumber";
    public static final String IS_APPROVED = "isApproved";

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
