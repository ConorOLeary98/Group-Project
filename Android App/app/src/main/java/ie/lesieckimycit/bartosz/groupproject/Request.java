package ie.lesieckimycit.bartosz.groupproject;

import com.google.android.gms.maps.model.LatLng;

public class Request {

    public LatLng start;
    public LatLng finish;
    public String state;
    public String vehicleID;

    public Request(){}

    public Request(LatLng start, LatLng finish){
        this.start = start;
        this.finish = finish;
        this.state = "pending";
        this.vehicleID = "";
    }
}
