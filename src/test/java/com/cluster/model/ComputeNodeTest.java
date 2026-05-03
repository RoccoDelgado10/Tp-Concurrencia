package test.java.com.cluster.model;

import com.cluster.model.ComputeNode;
import com.cluster.model.NodeState;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ComputeNodeTest {

    @Test
    public void testInitialStateFree() {
        ComputeNode node = new ComputeNode(1);
        assertEquals(NodeState.FREE, node.getState());
        assertTrue(node.isFree());
        assertEquals(0, node.getExecutionCount());
    }

    @Test
    public void testAssignAndSetFree() {
        ComputeNode node = new ComputeNode(2);
        node.assignJob();
        assertEquals(NodeState.BUSY, node.getState());
        assertFalse(node.isFree());
        assertEquals(1, node.getExecutionCount());
        node.setFree();
        assertEquals(NodeState.FREE, node.getState());
        assertTrue(node.isFree());
    }

    @Test
    public void testSetOutOfService() {
        ComputeNode node = new ComputeNode(3);
        node.setOutOfService();
        assertEquals(NodeState.OUT_OF_SERVICE, node.getState());
        assertFalse(node.isFree());
    }
}
