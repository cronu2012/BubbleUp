package com.example.bubbleup.OrderList;


public class LesOrder implements java.io.Serializable{
    private String les_o_no;
    private String mem_no;
    private String les_no;
    private String ds_no;
    private String mem_name;
    private String les_name;
    private Integer cost;
    private String coach;
    private String lo_state;

    public LesOrder() {
    }

    public LesOrder(String mem_no, String les_no, String ds_no, String mem_name, String les_name, Integer cost, String coach, String lo_state) {
        this.mem_no = mem_no;
        this.les_no = les_no;
        this.ds_no = ds_no;
        this.mem_name = mem_name;
        this.les_name = les_name;
        this.cost = cost;
        this.coach = coach;
        this.lo_state = lo_state;
    }

    public LesOrder(String les_o_no, String mem_no, String les_no, String ds_no, String mem_name, String les_name, Integer cost, String coach, String lo_state) {
        this.les_o_no = les_o_no;
        this.mem_no = mem_no;
        this.les_no = les_no;
        this.ds_no = ds_no;
        this.mem_name = mem_name;
        this.les_name = les_name;
        this.cost = cost;
        this.coach = coach;
        this.lo_state = lo_state;
    }

    public String getLes_o_no() {
        return les_o_no;
    }
    public void setLes_o_no(String les_o_no) {
        this.les_o_no = les_o_no;
    }
    public String getMem_no() {
        return mem_no;
    }
    public void setMem_no(String mem_no) {
        this.mem_no = mem_no;
    }
    public String getLes_no() {
        return les_no;
    }
    public void setLes_no(String les_no) {
        this.les_no = les_no;
    }
    public String getDs_no() { return ds_no; }
    public void setDs_no(String ds_no) { this.ds_no = ds_no; }
    public String getMem_name() {
        return mem_name;
    }
    public void setMem_name(String mem_name) {
        this.mem_name = mem_name;
    }
    public String getLes_name() {
        return les_name;
    }
    public void setLes_name(String les_name) {
        this.les_name = les_name;
    }
    public Integer getCost() {
        return cost;
    }
    public void setCost(Integer cost) {
        this.cost = cost;
    }
    public String getCoach() {
        return coach;
    }
    public void setCoach(String coach) {
        this.coach = coach;
    }
    public String getLo_state() {
        return lo_state;
    }
    public void setLo_state(String lo_state) {
        this.lo_state = lo_state;
    }


}

