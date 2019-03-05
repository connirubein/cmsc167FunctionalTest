package com.example.administrator.biodiversityapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Record {
    private int _id;
    private int _recorderID;
    private String _speciesName;
    private String _commonName;
    private String _remarks;
    private String _status;
    private String _datetime;
    private byte[] _imageData = null;

    public Record(int recorderID, String spec_name, String com_name, String remarks, byte[] imageData, String status, String datetime){
        System.out.println("record.java: status = "+status);
        this._recorderID = recorderID;
        this._speciesName = spec_name;
        this._commonName = com_name;
        this._remarks = remarks;
        this._imageData = imageData;
        this._status = status;
        this._datetime = datetime;
    }

    public String get_datetime() {
        return _datetime;
    }

    public void set_datetime(String _datetime) {
        this._datetime = _datetime;
    }

    public int get_recorderID() {
        return _recorderID;
    }

    public void set_recorderID(int _recorderID) {
        this._recorderID = _recorderID;
    }

    public byte[] get_imageData() {
        return _imageData;
    }

    public void set_imageData(byte[] _imageData) {
        this._imageData = _imageData;
    }

    public String get_status() {
        return _status;
    }

    public void set_status(String _status) {
        this._status = _status;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_speciesName() {
        return _speciesName;
    }

    public void set_speciesName(String _speciesName) {
        this._speciesName = _speciesName;
    }

    public String get_commonName() {
        return _commonName;
    }

    public void set_commonName(String _commonName) {
        this._commonName = _commonName;
    }

    public String get_remarks() {
        return _remarks;
    }

    public void set_remarks(String _remarks) {
        this._remarks = _remarks;
    }

    // Bitmap to byte[] to imageData
    public void setImageDataFromBitmap(Bitmap image) {
        if (image != null) {
            //bitmap to byte[]
            _imageData = bitmapToByte(image);
        } else {
            _imageData = null;
        }
    }

    // Bitmap to byte[]
    public byte[] bitmapToByte(Bitmap bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //bitmap to byte[] stream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] x = stream.toByteArray();
            //close stream to save memory
            stream.close();
            return x;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Convert imageData directly to bitmap
    public Bitmap getImageDataInBitmap() {
        if (_imageData != null) {
            //turn byte[] to bitmap
            return BitmapFactory.decodeByteArray(_imageData, 0, _imageData.length);
        }
        return null;
    }

    public byte[] getImageData() {
        return _imageData;
    }

}