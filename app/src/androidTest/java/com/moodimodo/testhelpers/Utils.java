package com.moodimodo.testhelpers;

public class Utils {

    public static final long SLEEP_STEP = 300;

    public static class ConditionNotMeetException extends Exception {
        public ConditionNotMeetException(Condition condition){
            super(condition.toString());
        }
    }

    public interface Condition{
        boolean check();
    }

    public static void waitForCondition(Condition condition,long timeout) throws InterruptedException, ConditionNotMeetException {
        long end = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < end){
            if (condition.check()){
                return;
            }
            Thread.sleep(SLEEP_STEP);
        }
        throw new ConditionNotMeetException(condition);
    }

}
