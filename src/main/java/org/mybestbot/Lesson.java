package org.mybestbot;

import com.google.gson.annotations.SerializedName;

public class Lesson {
    private String teacher;

    @SerializedName("second_teacher")
    private String secondTeacher;

    @SerializedName("subjectType")
    private String subjectType;

    private String week;
    private String name;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    @SerializedName("start_time_seconds")
    private int startTimeSeconds;

    @SerializedName("end_time_seconds")
    private int endTimeSeconds;
    private String room;
    private String comment;
    private String form;

    @SerializedName("temp_changes")
    private String[] tempChanges; // Массив строк для временных изменений

    private String url;

    // Конструктор
    public Lesson() {
    }

    // Геттеры и сеттеры
    public String getTeacher() {
        return teacher;
    }

    public String getSecondTeacher() {
        return secondTeacher;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public String getWeek() {
        return week;
    }

    public String getName() {
        return name;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getStartTimeSeconds() {
        return startTimeSeconds;
    }

    public int getEndTimeSeconds() {
        return endTimeSeconds;
    }

    public String getRoom() {
        return room;
    }

    public String getComment() {
        return comment;
    }

    public String getForm() {
        return form;
    }

    public String[] getTempChanges() {
        return tempChanges;
    }

    public String getUrl() {
        return url;
    }
}

