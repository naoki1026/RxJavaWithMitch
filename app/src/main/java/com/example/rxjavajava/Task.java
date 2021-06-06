package com.example.rxjavajava;

// クラスの作成
public class Task {

    // 変数の定義
    private String description;
    private boolean isComplete;
    private int priority;

    // コンストラクタ
    public Task(String description, boolean isComplete, int priority) {
        this.description = description;
        this.isComplete = isComplete;
        this.priority = priority;
    }

    // メソッドの定義
    public String getDescription() { return description;}
    public void setDescription(String description) {this.description = description;}
    public boolean isComplete(){return isComplete;}
    public void setComplete(boolean complete) {isComplete = complete;}
    public int getPriority(){return priority;}
    public void setPriority(int priority){this.priority = priority;}



}
