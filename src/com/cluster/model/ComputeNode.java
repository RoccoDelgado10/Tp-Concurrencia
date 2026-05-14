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

    public NodeState getState() {
        return state;
    }

    public void setFree() {
        // cambia estado a FREE
        this.state = NodeState.FREE;
    }
    /** Juntamos la logica de isFree() con assingJob()
     *  Checkeamos si está libre el nodo y marcamos busy
     *  aumenta contador de ejecución
    * */
    public boolean tryAssign() {
        if (this.state != NodeState.FREE) return false;
        this.state = NodeState.BUSY;
        this.executionCount++;
        return true;
    }

    public void setOutOfService() {
        // cambia estado a OUT_OF_SERVICE
        this.state = NodeState.OUT_OF_SERVICE;
    }

    @Override
    public String toString() {
        return "Node{id=" + id + ", state=" + state + ", executions=" + executionCount + "}";
    }
}