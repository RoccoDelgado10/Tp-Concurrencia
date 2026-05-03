package com.cluster.model;

public class Job {
//Roco es crack
    private final int id;
    private JobStatus status;
    private int assignedNodeId;

    public Job(int id) {
        this.id = id;
        this.status = JobStatus.QUEUED;
        this.assignedNodeId = -1;
    }

    public int getId() {
        return id;
    }

    public synchronized JobStatus getStatus() {
        return status;
    }

    public synchronized void setStatus(JobStatus status) {
        this.status = status;
    }

    public synchronized int getAssignedNodeId() {
        return assignedNodeId;
    }

    public synchronized void setAssignedNodeId(int nodeId) {
        this.assignedNodeId = nodeId;
    }

    @Override
    public String toString() {
        return "Job{id=" + id + ", status=" + status + ", nodeId=" + assignedNodeId + "}";
    }
}