package com.murach.tasklist;

public class Task {

    private long taskId;
    private long listId;
    private String name;
    private String notes;
    private long completedDate;
    private int hidden;
    
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    
    public Task() {
        name = "";
        notes = "";
        completedDate = FALSE;
        hidden = FALSE;
    }

    public Task(int listId, String name, String notes,
            long completed, int hidden) {
        this.listId = listId;
        this.name = name;
        this.notes = notes;
        this.completedDate = completed;
        this.hidden = hidden;
    }

    public Task(int taskId, int listId, String name, String notes,
            long completed, int hidden) {
        this.taskId = taskId;
        this.listId = listId;
        this.name = name;
        this.notes = notes;
        this.completedDate = completed;
        this.hidden = hidden;
    }

    public long getId() {
        return taskId;
    }

    public void setId(long taskId) {
        this.taskId = taskId;
    }
    
    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public long getCompletedDate() {
        return completedDate;
    }

    public long getCompletedDateMillis() {
        return completedDate;
    }

    public void setCompletedDate(long completedDate) {
        this.completedDate = completedDate;    
    }
    
    public int getHidden(){
        return hidden;
    }
    
    public void setHidden(int hidden) {
        this.hidden = hidden;    
    }    
}