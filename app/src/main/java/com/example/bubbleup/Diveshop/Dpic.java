package com.example.bubbleup.Diveshop;


public class Dpic implements java.io.Serializable{
    private String dpic_seq;
    private String ds_no;


    public Dpic() {
        super();
    }
    public Dpic(String dpic_seq, String ds_no, String dpic_name) {
        super();
        this.dpic_seq = dpic_seq;
        this.ds_no = ds_no;

    }
    public String getDpic_seq() {
        return dpic_seq;
    }
    public void setDpic_seq(String dpic_seq) {
        this.dpic_seq = dpic_seq;
    }
    public String getDs_no() {
        return ds_no;
    }
    public void setDs_no(String ds_no) {
        this.ds_no = ds_no;
    }

}
