package it.sella.assist.util;

/**
 * Created by GodwinRoseSamuel on 23-Jul-16.
 */
public class ServerUtils {
    public final static String HTTP_PROTOCOL = "http";
    public final static String HOSTNAME = "117.218.241.218";
    public final static int PORT_NO = 8080;
    public final static String NOTIFICATION_API = "biot/rest/notifications";
    public final static String REGISTER_API = "biot/rest/notifications/user";
    public final static String LOGIN_API = "biot/rest/admin/user";
    public final static String IMAGE_API = "/biot/static/images";
    public final static String EVENTS_API = "biot/rest/communications/events";
    public final static String BIOMETRIC_API = "biot/rest/admin/biometric";
    public final static String BUSINESS_UNIT_API = "biot/rest/admin/businessunit";
    public final static String INTERESTED_EVENTS_API = "biot/rest/communications/interest/event";

    public static String getServerURL() {
        return HTTP_PROTOCOL + "://" + HOSTNAME + ":" + PORT_NO;
    }
}
