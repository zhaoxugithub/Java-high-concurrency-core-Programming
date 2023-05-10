package com.crazymakercircle.threadpool;

public class RunnableWrapper implements Runnable{
    private final Integer taskId;

    public RunnableWrapper(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskId() {
        return this.taskId;
    }

    @Override
    public void run() {
        System.out.println("Task " + taskId + " is running.");
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
        System.out.println("Task " + taskId + " is completed.");
    }
}
