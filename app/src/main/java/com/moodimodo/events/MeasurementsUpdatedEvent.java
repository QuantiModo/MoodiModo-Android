package com.moodimodo.events;

public class MeasurementsUpdatedEvent {
    public final boolean fromSync;

    public MeasurementsUpdatedEvent(boolean fromSync) {
        this.fromSync = fromSync;
    }

    public MeasurementsUpdatedEvent(){
        fromSync = false;
    }
}
