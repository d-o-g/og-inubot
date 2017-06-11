package com.inubot.script2;

import com.inubot.api.util.Time;

import java.util.LinkedList;
import java.util.Queue;

public abstract class Script {

    private final Queue<ScriptEvent> delayed;
    private final Queue<ScriptEvent> immediate;

    protected Script() {
        delayed = immediate = new LinkedList<>();
    }

    public void process() {
        for (ScriptEvent event = next(); event != null; event = next()) {
            if (event.getCycleDelay() > 0) {
                Time.waitCycles(event.getCycleDelay());
            }
            event.process();
        }
    }

    private ScriptEvent next() {
        ScriptEvent event = immediate.peek();
        if (event == null) {
            event = delayed.peek();
        }
        return event;
    }

    public void delayAndQueue(ScriptEvent event) {
        delayed.offer(event);
    }

    public void immediatelyQueue(ScriptEvent event) {
        immediate.offer(event);
    }

    /**
     * Processes and clears events from the immediate queue, and delayed queue is specified
     * @param includeDelayed {@code true} to process and clear delayed events too.
     *                       They will be processed without delayAndQueue
     */
    public void flush(boolean includeDelayed) {
        if (includeDelayed) {
            immediate.addAll(delayed);
            delayed.clear();
        }
        immediate.forEach(ScriptEvent::process);
        immediate.clear();
    }

    /**
     * Clears events from the immediate queue, and delayed queue if specified
     * @param includeDelayed {@code true} to clear delayed events
     */
    public void clear(boolean includeDelayed) {
        immediate.clear();
        if (includeDelayed) {
            delayed.clear();
        }
    }
}
