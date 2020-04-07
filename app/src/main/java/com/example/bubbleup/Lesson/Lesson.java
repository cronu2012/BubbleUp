package com.example.bubbleup.Lesson;

import java.sql.Date;

public class Lesson implements java.io.Serializable {
    private String les_no;
    private String ds_no;
    private String ds_name;
    private String les_name;
    private String les_info;
    private String coach;
    private Integer cost;
    private Integer days;
    private Date signup_startdate;
    private Date signup_enddate;
    private String les_state;
    private Integer les_max;
    private Integer les_limit;
    private Date les_startdate;
    private Date les_enddate;
    private String less_state;

    public String getLes_no() {
        return les_no;
    }
    public void setLes_no(String les_no) {
        this.les_no = les_no;
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
    public String getLes_name() {
        return les_name;
    }
    public void setLes_name(String les_name) {
        this.les_name = les_name;
    }
    public String getLes_info() {
        return les_info;
    }
    public void setLes_info(String les_info) {
        this.les_info = les_info;
    }
    public String getCoach() {
        return coach;
    }
    public void setCoach(String coach) {
        this.coach = coach;
    }
    public Integer getCost() {
        return cost;
    }
    public void setCost(Integer cost) {
        this.cost = cost;
    }
    public Integer getDays() {
        return days;
    }
    public void setDays(Integer days) {
        this.days = days;
    }
    public Date getSignup_startdate() {
        return signup_startdate;
    }
    public void setSignup_startdate(Date signup_startdate) {
        this.signup_startdate = signup_startdate;
    }
    public Date getSignup_enddate() {
        return signup_enddate;
    }
    public void setSignup_enddate(Date signup_enddate) {
        this.signup_enddate = signup_enddate;
    }
    public String getLes_state() {
        return les_state;
    }
    public void setLes_state(String les_state) {
        this.les_state = les_state;
    }
    public Integer getLes_max() {
        return les_max;
    }
    public void setLes_max(Integer les_max) {
        this.les_max = les_max;
    }
    public Integer getLes_limit() {
        return les_limit;
    }
    public void setLes_limit(Integer les_limit) {
        this.les_limit = les_limit;
    }
    public Date getLes_startdate() {
        return les_startdate;
    }
    public void setLes_startdate(Date les_startdate) {
        this.les_startdate = les_startdate;
    }
    public Date getLes_enddate() {
        return les_enddate;
    }
    public void setLes_enddate(Date les_enddate) {
        this.les_enddate = les_enddate;
    }
    public String getLess_state() {
        return less_state;
    }
    public void setLess_state(String less_state) {
        this.less_state = less_state;
    }


}
