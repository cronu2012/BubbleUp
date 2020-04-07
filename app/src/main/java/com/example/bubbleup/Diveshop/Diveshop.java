package com.example.bubbleup.Diveshop;

import java.io.Serializable;



public class Diveshop implements java.io.Serializable {
    private String ds_no;
    private String ds_name;
    private Integer brcid;
    private String tel;
    private String address;
    private String dsaccount;
    private String dspaw;
    private String dsmail;
    private String dsinfo;
    private String ds_latlng;
    private String ds_state;
    private Integer ds_ascore;
    private Integer ds_rep_no;
    private int count;


    public Diveshop(){super();}

    public Diveshop(String ds_no, String ds_name, Integer brcid, String tel, String address,
                    String dsaccount, String dspaw, String dsmail, String dsinfo, String ds_latlng,
                     String ds_state, Integer ds_ascore, Integer ds_rep_no) {
        this.ds_no = ds_no;
        this.ds_name = ds_name;
        this.brcid = brcid;
        this.tel = tel;
        this.address = address;
        this.dsaccount = dsaccount;
        this.dspaw = dspaw;
        this.dsmail = dsmail;
        this.dsinfo = dsinfo;
        this.ds_state = ds_state;
        this.ds_ascore = ds_ascore;
        this.ds_rep_no = ds_rep_no;
    }

    public String getDs_no() {
        return ds_no;
    }

    public void setDs_no(String ds_no) {
        this.ds_no = ds_no;
    }

    public String getDs_name() {
        return ds_name;
    }

    public void setDs_name(String ds_name) {
        this.ds_name = ds_name;
    }

    public Integer getBrcid() {
        return brcid;
    }

    public void setBrcid(Integer brcid) {
        this.brcid = brcid;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDsaccount() {
        return dsaccount;
    }

    public void setDsaccount(String dsaccount) {
        this.dsaccount = dsaccount;
    }

    public String getDspaw() {
        return dspaw;
    }

    public void setDspaw(String dspaw) {
        this.dspaw = dspaw;
    }

    public String getDsmail() {
        return dsmail;
    }

    public void setDsmail(String dsmail) {
        this.dsmail = dsmail;
    }

    public String getDsinfo() {
        return dsinfo;
    }

    public void setDsinfo(String dsinfo) {
        this.dsinfo = dsinfo;
    }

    public String getDs_latlng() {
        return ds_latlng;
    }

    public void setDs_latlng(String ds_latlng) {
        this.ds_latlng = ds_latlng;
    }
    public String getDs_state() {
        return ds_state;
    }

    public void setDs_state(String ds_state) {
        this.ds_state = ds_state;
    }

    public Integer getDs_ascore() {
        return ds_ascore;
    }

    public void setDs_ascore(Integer ds_ascore) {
        this.ds_ascore = ds_ascore;
    }

    public Integer getDs_rep_no() {
        return ds_rep_no;
    }

    public void setDs_rep_no(Integer ds_rep_no) {
        this.ds_rep_no = ds_rep_no;
    }

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
}


