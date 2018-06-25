package com.quantimodo.tools;

/**
 * Enter point of QuantimodoTools
 * Used to get instance of application, in order to use tools components
 * {@link QTools#init(QApplication)} should be called , with instance of QApplication
 */
public class QTools {

    private static QApplication mInstance;
    private static int mAppIconResId = 0;

    /**
     * @param instance Instance of QApplicaiton
     */
    public static void init(QApplication instance){
        mInstance = instance;
    }


    public static QApplication getInstance(){
        return mInstance;
    }

    /**
     * Sets the action icon to use for notifications
     * @param iconResId the app action icon resource id
     */
    public static void setAppActionIcon(int iconResId){
        mAppIconResId = iconResId;
    }

    /**
     * Gets the action icon to use for custom notifications
     * @return the app action icon resource id
     */
    public static int getAppIconResId(){
        return mAppIconResId;
    }
}
