package com.example.bubbleup.Member;

import java.io.Serializable;
import java.sql.Timestamp;
import java.sql.Date;

public  class Member implements Serializable {
    private String mem_no;
    private String mem_id;
    private String mem_psw;
    private String mem_name;
    private Integer mem_sex;
    private Date mem_bd;
    private String mem_mail;
    private String mem_phone;
    private String mem_add;
    private Timestamp reg_time;
    private Integer mem_rep_no;
    private String mem_state;

    public Member() {
        super();
    }



    public Member(String mem_no, String mem_id, String mem_psw, String mem_name, Integer mem_sex, Date mem_bd, String mem_mail, String mem_phone, String mem_add, Timestamp reg_time, Integer mem_rep_no, String mem_state) {
        this.mem_no = mem_no;
        this.mem_id = mem_id;
        this.mem_psw = mem_psw;
        this.mem_name = mem_name;
        this.mem_sex = mem_sex;
        this.mem_bd = mem_bd;
        this.mem_mail = mem_mail;
        this.mem_phone = mem_phone;
        this.mem_add = mem_add;
        this.reg_time = reg_time;
        this.mem_rep_no = mem_rep_no;
        this.mem_state = mem_state;
    }

    public String getMem_no() {
        return mem_no;
    }

    public void setMem_no(String mem_no) {
        this.mem_no = mem_no;
    }

    public String getMem_id() {
        return mem_id;
    }

    public void setMem_id(String mem_id) {
        this.mem_id = mem_id;
    }

    public String getMem_psw() {
        return mem_psw;
    }

    public void setMem_psw(String mem_psw) {
        this.mem_psw = mem_psw;
    }

    public String getMem_name() {
        return mem_name;
    }

    public void setMem_name(String mem_name) {
        this.mem_name = mem_name;
    }

    public Integer getMem_sex() {
        return mem_sex;
    }

    public void setMem_sex(Integer mem_sex) {
        this.mem_sex = mem_sex;
    }

    public Date getMem_bd() {
        return mem_bd;
    }

    public void setMem_bd(Date mem_bd) {
        this.mem_bd = mem_bd;
    }

    public String getMem_mail() {
        return mem_mail;
    }

    public void setMem_mail(String mem_mail) {
        this.mem_mail = mem_mail;
    }

    public String getMem_phone() {
        return mem_phone;
    }

    public void setMem_phone(String mem_phone) {
        this.mem_phone = mem_phone;
    }

    public String getMem_add() {
        return mem_add;
    }

    public void setMem_add(String mem_add) {
        this.mem_add = mem_add;
    }

    public Timestamp getReg_time() {
        return reg_time;
    }

    public void setReg_time(Timestamp reg_time) {
        this.reg_time = reg_time;
    }

    public Integer getMem_rep_no() {
        return mem_rep_no;
    }

    public void setMem_rep_no(Integer mem_rep_no) {
        this.mem_rep_no = mem_rep_no;
    }

    public String getMem_state() {
        return mem_state;
    }

    public void setMem_state(String mem_state) {
        this.mem_state = mem_state;
    }

}