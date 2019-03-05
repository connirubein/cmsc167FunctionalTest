package com.example.administrator.biodiversityapplication;

public class LatLong{

    public Integer id;
    public Integer record_id;
    public String latitude;
    public String longitude;

    public LatLong (Integer id, Integer record_id, String latitude, String longitude){
        this.id = id;
        this.record_id = record_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRecord_id() {
        return record_id;
    }

    public void setRecord_id(Integer record_id) {
        this.record_id = record_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}