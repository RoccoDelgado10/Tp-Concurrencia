//Rocco es crack

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
        // TODO: retornar true solo si el estado es FREE
        return false;
    }

    public synchronized void assignJob() {
        // TODO: cambiar estado a BUSY e incrementar executionCount
    }

    public synchronized void setFree() {
        // TODO: cambiar estado a FREE
    }

    public synchronized void setOutOfService() {
        // TODO: cambiar estado a OUT_OF_SERVICE
    }

    public synchronized int getExecutionCount() {
        return executionCount;
    }

    @Override
    public String toString() {
        return "Node{id=" + id + ", state=" + state + ", executions=" + executionCount + "}";
    }
}