package scout.scoutmobile.utils;

public class Logger {

    private String callerClass;

    public Logger(String className) {
        callerClass = className;
    }

    public void log(String output) {
        System.out.println(callerClass + ": " + output);
    }
}
