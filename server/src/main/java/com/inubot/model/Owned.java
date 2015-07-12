package com.inubot.model;

/**
 * @author Septron
 * @since July 12, 2015
 */
public class Owned {
    private int id;
    private int uid;

   public Owned(int id, int uid) {
       this.id = id;
       this.uid = uid;
   }

    public Owned() {

    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
