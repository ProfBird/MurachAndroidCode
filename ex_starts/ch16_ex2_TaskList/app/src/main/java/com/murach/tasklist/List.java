package com.murach.tasklist;

public class List {
    
    private long id;
    private String name;
    
    public List() {}
    
    public List(String name) {
        this.name = name;
    }
    
    public List(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    @Override 
    public String toString() {
        return name;   // used for add/edit spinner
    }
}