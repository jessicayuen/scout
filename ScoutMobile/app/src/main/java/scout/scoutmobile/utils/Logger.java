package scout.scoutmobile.utils;

import android.util.Log;

public class Logger {

    private String callerClass;

    public Logger(String className) {
        callerClass = className;
    }

    public void log(String output) {
        Log.w(callerClass, output);
    }

    public void logError(Throwable e) {
        Log.wtf(callerClass, e);
    }
}
