package com.example.bubbleup.OrderList;

public class RoDetail implements java.io.Serializable {
    private String ro_no;
    private String ep_seq;
    private Integer ep_crp;

    public RoDetail() {

    }

    public String getRo_no() {
        return ro_no;
    }
    public void setRo_no(String ro_no) {
        this.ro_no = ro_no;
    }
    public String getEp_seq() {
        return ep_seq;
    }
    public void setEp_seq(String ep_seq) {
        this.ep_seq = ep_seq;
    }
    public Integer getEp_crp() {
        return ep_crp;
    }
    public void setEp_crp(Integer ep_crp) {
        this.ep_crp = ep_crp;
    }
}
