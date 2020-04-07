package com.example.bubbleup.OrderList;

import java.sql.Date;

public class ROrder implements java.io.Serializable {
    private String ro_no;
    private String ds_no;
    private String mem_no;
    private Integer tepc;
    private Integer tpriz;
    private String op_state;
    private Date rs_date;
    private Date rd_date;
    private Date rr_date;
    private Integer ffine;
    private String o_state;
    private String o_note;


    public ROrder() {
        super();
    }

    public ROrder(String ro_no, String ds_no, String mem_no, Integer tepc, Integer tpriz, String op_state, Date rs_date, Date rd_date, Date rr_date, Integer ffine, String o_state, String o_note) {
        this.ro_no = ro_no;
        this.ds_no = ds_no;
        this.mem_no = mem_no;
        this.tepc = tepc;
        this.tpriz = tpriz;
        this.op_state = op_state;
        this.rs_date = rs_date;
        this.rd_date = rd_date;
        this.rr_date = rr_date;
        this.ffine = ffine;
        this.o_state = o_state;
        this.o_note = o_note;
    }

    public String getRo_no() {
        return ro_no;
    }

    public void setRo_no(String ro_no) {
        this.ro_no = ro_no;
    }

    public String getDs_no() {
        return ds_no;
    }

    public void setDs_no(String ds_no) {
        this.ds_no = ds_no;
    }

    public String getMem_no() {
        return mem_no;
    }

    public void setMem_no(String mem_no) {
        this.mem_no = mem_no;
    }

    public Integer getTepc() {
        return tepc;
    }

    public void setTepc(Integer tepc) {
        this.tepc = tepc;
    }

    public Integer getTpriz() {
        return tpriz;
    }

    public void setTpriz(Integer tpriz) {
        this.tpriz = tpriz;
    }

    public String getOp_state() {
        return op_state;
    }

    public void setOp_state(String op_state) {
        this.op_state = op_state;
    }

    public Date getRs_date() {
        return rs_date;
    }

    public void setRs_date(Date rs_date) {
        this.rs_date = rs_date;
    }

    public Date getRd_date() {
        return rd_date;
    }

    public void setRd_date(Date rd_date) {
        this.rd_date = rd_date;
    }

    public Date getRr_date() {
        return rr_date;
    }

    public void setRr_date(Date rr_date) {
        this.rr_date = rr_date;
    }

    public Integer getFfine() {
        return ffine;
    }

    public void setFfine(Integer ffine) {
        this.ffine = ffine;
    }

    public String getO_state() {
        return o_state;
    }

    public void setO_state(String o_state) {
        this.o_state = o_state;
    }

    public String getO_note() {
        return o_note;
    }

    public void setO_note(String o_note) {
        this.o_note = o_note;
    }
}
