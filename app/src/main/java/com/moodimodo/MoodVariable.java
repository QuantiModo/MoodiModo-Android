package com.moodimodo;


public enum MoodVariable {
//    OVERALL_MOOD(true,"Overall Mood", COLUMN_MOOD_NAME),
//    GUILTY(false,"Guiltiness", COLUMN_GUILTY_NAME),
//    ALERT(true,"Alertness", COLUMN_ALERT_NAME),
//    AFRAID(false,"Fear", COLUMN_AFRAID_NAME),
//    EXCITED(true,"Excitability", COLUMN_EXCITED_NAME),
//    IRRITABLE(false,"Irritability", COLUMN_IRRITABLE_NAME),
//    SHAME(false,"Shame", COLUMN_ASHAMED_NAME),
//    ATTENTIVE(true,"Attentiveness", COLUMN_ATTENTIVE_NAME),
//    HOSTILE(false,"Hostility", COLUMN_HOSTILE_NAME),
//    ACTIVE(true,"Activeness", COLUMN_ACTIVE_NAME),
//    NERVOUS(false,"Nervousness", COLUMN_NERVOUS_NAME),
//    INTERESTED (true,"Interest", COLUMN_INTERESTED_NAME),
//    ENTHUSIASTIC (true,"Enthusiasm", COLUMN_ENTHUSIASTIC_NAME),
//    JITTERY (false,"Jitteriness", COLUMN_JITTERY_NAME),
//    STRONG (true,"Resilience", COLUMN_STRONG_NAME),
//    DISTRESSED (false,"Distress", COLUMN_DISTRESSED_NAME),
//    DETERMINED (true,"Determination", COLUMN_DETERMINED_NAME),
//    UPSET (false,"Upsettedness", COLUMN_UPSET_NAME),
//    PROUD (true,"Pride", COLUMN_PROUD_NAME),
//    SCARED (false,"Scaredness", COLUMN_SCARED_NAME),
//    INSPIRED (true,"Inspiration", COLUMN_INSPIRED_NAME),
//    ACCURATE_MOOD (true,"Overall Mood", COLUMN_MOOD_ACCURATE_NAME),
    
    ;
    private final boolean mIsPositive;
    private final String mVariableName;
    private final String mDbColumn;


    MoodVariable(boolean isPositive, String variableName, String mDbColumn) {
        this.mIsPositive = isPositive;
        mVariableName = variableName;
        this.mDbColumn = mDbColumn;
    }

    public boolean isPositive() {
        return mIsPositive;
    }

    public String getVariableName() {
        return mVariableName;
    }

    public String getColumnName(){ return mDbColumn;}
}