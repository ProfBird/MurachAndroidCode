package com.murach.tasklist;

public class Task {

    private long taskId;
    private long listId;
    private String name;
    private String notes;
    private String completedDate;
    private String hidden;
    
    public static final String TRUE = "1";
    public static final String FALSE = "0";
    
    public Task() {
        name = "";
        notes = "";
        completedDate = FALSE;
        hidden = FALSE;
    }

    public Task(int listId, String name, String notes,
            String completed, String hidden) {
        this.listId = listId;
        this.name = name;
        this.notes = notes;
        this.completedDate = completed;
        this.hidden = hidden;
    }

    public Task(int taskId, int listId, String name, String notes,
            String completed, String hidden) {
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
    
    public String getCompletedDate() {
        return completedDate;
    }

    public long getCompletedDateMillis() {
        return Long.parseLong(completedDate);
    }

    public void setCompletedDate(String date_completed) {
        this.completedDate = date_completed;    
    }
    
    public void setCompletedDate(long millis) {
        this.completedDate = Long.toString(millis);    
    }
    
    public String getHidden(){
        return hidden;
    }
    
    public void setHidden(String hidden) {
        this.hidden = hidden;    
    }    
}