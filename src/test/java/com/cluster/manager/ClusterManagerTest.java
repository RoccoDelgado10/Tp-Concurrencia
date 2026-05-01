package test.java.com.cluster.manager;

import com.cluster.manager.ClusterManager;
import com.cluster.model.Job;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClusterManagerTest {

    @Test
    public void testConstructorInitializes() {
        ClusterManager cm = new ClusterManager();
        assertEquals(ClusterManager.TOTAL_NODES, cm.getNodes().length);
        assertEquals(0, cm.getQueueSize());
        assertEquals(0, cm.getExecutionSize());
        assertEquals(0, cm.getFinishedCount());
        assertEquals(0, cm.getFailedCount());
        assertEquals(0, cm.getValidatedCount());
    }

    @Test
    public void testEnqueueAndPollQueue() throws InterruptedException {
        ClusterManager cm = new ClusterManager();
        Job job = new Job(1);
        cm.enqueueJob(job);
        assertEquals(1, cm.getQueueSize());
        Job polled = cm.pollFromQueue();
        assertEquals(job, polled);
    }

    @Test
    public void testMoveToExecutionAndPoll() throws InterruptedException {
        ClusterManager cm = new ClusterManager();
        Job job = new Job(2);
        cm.enqueueJob(job);
        cm.moveToExecution(job);
        assertEquals(1, cm.getExecutionSize());
        Job polled = cm.pollFromExecution();
        assertEquals(job, polled);
    }

    @Test
    public void testMoveToFinishedAndPollFromFinished() {
        ClusterManager cm = new ClusterManager();
        Job job = new Job(3);
        cm.moveToFinished(job);
        assertEquals(1, cm.getFinishedCount());
        Job polled = cm.pollFromFinished();
        assertEquals(job, polled);
    }

    @Test
    public void testFailedAndValidatedStats() {
        ClusterManager cm = new ClusterManager();
        Job jf = new Job(4);
        Job jv = new Job(5);
        cm.moveToFailed(jf);
        cm.moveToValidated(jv);
        assertEquals(1, cm.getFailedCount());
        assertEquals(1, cm.getValidatedCount());
        int[] stats = cm.getStats();
        assertEquals(1, stats[0]);
        assertEquals(1, stats[1]);
    }

    @Test
    public void testIncrementProcessedAndIsFinished() {
        ClusterManager cm = new ClusterManager();
        for (int i = 0; i < ClusterManager.TOTAL_JOBS; i++) {
            cm.incrementProcessed();
        }
        assertTrue(cm.isFinished());
    }
}
