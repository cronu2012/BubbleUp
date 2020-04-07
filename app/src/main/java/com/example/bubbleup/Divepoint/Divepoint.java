package com.example.bubbleup.Divepoint;

import java.io.Serializable;

public class Divepoint implements Serializable {

    private String dp_no;
    private String loc_no;
    private String dp_name;
    private Double dp_lat;
    private Double dp_lng;
    private String dp_info;
    private byte[] dp_pic1;
    private byte[] dp_pic2;
    private byte[] dp_pic3;
    private byte[] dp_pic4;

    public byte[] getDp_pic1() {
        return dp_pic1;
    }

    public void setDp_pic1(byte[] dp_pic1) {
        this.dp_pic1 = dp_pic1;
    }

    public byte[] getDp_pic2() {
        return dp_pic2;
    }

    public void setDp_pic2(byte[] dp_pic2) {
        this.dp_pic2 = dp_pic2;
    }

    public byte[] getDp_pic3() {
        return dp_pic3;
    }

    public void setDp_pic3(byte[] dp_pic3) {
        this.dp_pic3 = dp_pic3;
    }

    public byte[] getDp_pic4() {
        return dp_pic4;
    }

    public void setDp_pic4(byte[] dp_pic4) {
        this.dp_pic4 = dp_pic4;
    }

    public String getDp_no() {
        return dp_no;
    }

    public void setDp_no(String dp_no) {
        this.dp_no = dp_no;
    }

    public String getLoc_no() {
        return loc_no;
    }

    public void setLoc_no(String loc_no) {
        this.loc_no = loc_no;
    }

    public String getDp_name() {
        return dp_name;
    }

    public void setDp_name(String dp_name) {
        this.dp_name = dp_name;
    }

    public Double getDp_lat() {
        return dp_lat;
    }

    public void setDp_lat(Double dp_lat) {
        this.dp_lat = dp_lat;
    }

    public Double getDp_lng() {
        return dp_lng;
    }

    public void setDp_lng(Double dp_lng) {
        this.dp_lng = dp_lng;
    }

    public String getDp_info() {
        return dp_info;
    }

    public void setDp_info(String dp_info) {
        this.dp_info = dp_info;
    }


}
