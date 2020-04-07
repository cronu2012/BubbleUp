package com.example.bubbleup.Member;

public class MemPic  {
    private String mem_no;
    private byte[] image;

    public MemPic(String mem_no, byte[] image) {
        this.mem_no = mem_no;
        this.image = image;
    }

    public String getMem_no() {
        return mem_no;
    }

    public void setMem_no(String mem_no) {
        this.mem_no = mem_no;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
