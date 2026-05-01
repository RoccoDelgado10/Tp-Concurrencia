package com.cluster.model;

public class ComputeNode {

    private final int id;
    private NodeState state;
    private int executionCount;

    public ComputeNode(int id) {
        this.id = id;
        this.state = NodeState.FREE;
        this.executionCount = 0;
    }

    public int getId() {
        return id;
    }

    public synchronized NodeState getState() {
        return state;
    }

    public synchronized boolean isFree() {
        // retorna true solo si el estado es FREE
        return this.state == NodeState.FREE;
    }

    public synchronized void assignJob() {
        // cambia estado a BUSY
        this.state = NodeState.BUSY;
        // 2. Incrementa el contador de ejecuciones
        this.executionCount++;
    }

    public synchronized void setFree() {
        // cambia estado a FREE
        this.state = NodeState.FREE;
    }

    public synchronized void setOutOfService() {
        // cambia estado a OUT_OF_SERVICE
        this.state = NodeState.OUT_OF_SERVICE;
    }

    public synchronized int getExecutionCount() {
        return executionCount;
    }

    @Override
    public String toString() {
        return "Node{id=" + id + ", state=" + state + ", executions=" + executionCount + "}";
    }
}