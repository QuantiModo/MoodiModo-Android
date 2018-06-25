package com.moodimodo.things;

public enum Ratings {
    RATING_1(1),
    RATING_2(2),
    RATING_3(3),
    RATING_4(4),
    RATING_5(5);

    public final int intValue;

    Ratings(int intValue) {
        this.intValue = intValue;
    }
}
