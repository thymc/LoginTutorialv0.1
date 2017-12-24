package com.example.thymc.logintutorial;

/**
 * Created by Thymc on 12/20/2017.
 */


public class User {

    private String name;
    private String descr;
    private int idImg;



    public User(String name) {
        this.name = name;
    }

    public User(String name, String descr) {
        this.name = name;
        this.descr = descr;
    }

    public User(String name, String descr, int idImg) {
        this.name = name;
        this.descr = descr;
        this.idImg = idImg;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIdImg() {
        return idImg;
    }
    public void setIdImg(int idImg) {
        this.idImg = idImg;
    }


}
