package com.example.lbs_tester10;

public abstract class RoomContextRule {
    public void apply(RoomContextState context) {
        if (condition(context))
            action();
    }

    protected abstract boolean condition(RoomContextState context);

    protected abstract void action();

}
