package com.inubot.script2;

import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

public class ScriptEvent<T> {

    private int cycleDelay = 0;
    private boolean success;
    private boolean unlinkOnProcess; // remove from queue after process?
    private BooleanSupplier condition;

    private final Supplier<T> target;
    private final Function<T, ?> action;

    public ScriptEvent(Supplier<T> target, Function<T, ?> action) {
        unlinkOnProcess = false; // scripts loop by default, so don't remove by default
        this.target = target;
        this.action = action;
    }

    public void process() {
        if (condition == null || condition.getAsBoolean()) {
            action.apply(target.get());
        }
    }

    public void delayCycle(long delay) {
        cycleDelay += delay;
    }

    public int getCycleDelay() {
        return cycleDelay;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isUnlinkOnProcess() {
        return unlinkOnProcess;
    }

    public void setUnlinkOnProcess(boolean unlinkOnProcess) {
        this.unlinkOnProcess = unlinkOnProcess;
    }

    public BooleanSupplier getCondition() {
        return condition;
    }

    public void setCondition(BooleanSupplier condition) {
        this.condition = condition;
    }
}
