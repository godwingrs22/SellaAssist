package it.sella.assist.util;

/**
 * Created by GodwinRoseSamuel on 23-Jul-16.
 */
public class ServerUtils {
    public final static String HTTP_PROTOCOL = "http";
    public final static String HOSTNAME = "117.218.241.218";
    public final static String PORT_NO = "8080";
    private final static String NOTIFICATION_API = "/biot/rest/notifications";
    private final static String REGISTER_API = "/biot/rest/notifications/user";
    private final static String LOGIN_API = "/biot/rest/admin/user";
    private final static String IMAGE_API = "/biot/static/images";
    private final static String EVENTS_API = "/biot/rest/communications/events";
    private final static String BIOMETRIC_API = "/biot/rest/admin/biometric";
    private final static String BUSINESS_UNIT_API = "/biot/rest/admin/businessunit";
    private final static String INTERESTED_EVENTS_API = "/biot/rest/communications/interest/event";

    public static String getServerURL() {
        return HTTP_PROTOCOL + "://" + HOSTNAME + ":" + PORT_NO;
    }

    public static String getNotificationURL() {
        return HTTP_PROTOCOL + "://" + HOSTNAME + ":" + PORT_NO + NOTIFICATION_API;
    }

    public static String getRegisterURL() {
        return HTTP_PROTOCOL + "://" + HOSTNAME + ":" + PORT_NO + REGISTER_API;
    }

    public static String getLoginURL() {
        return HTTP_PROTOCOL + "://" + HOSTNAME + ":" + PORT_NO + LOGIN_API;
    }

    public static String getImageURL() {
        return HTTP_PROTOCOL + "://" + HOSTNAME + ":" + PORT_NO + IMAGE_API;
    }

    public static String getEventsURL() {
        return HTTP_PROTOCOL + "://" + HOSTNAME + ":" + PORT_NO + EVENTS_API;
    }

    public static String getBiometricURL() {
        return HTTP_PROTOCOL + "://" + HOSTNAME + ":" + PORT_NO + BIOMETRIC_API;
    }

    public static String getBusinessUnitURL() {
        return HTTP_PROTOCOL + "://" + HOSTNAME + ":" + PORT_NO + BUSINESS_UNIT_API;
    }

    public static String getInterestedEventsURL() {
        return HTTP_PROTOCOL + "://" + HOSTNAME + ":" + PORT_NO + INTERESTED_EVENTS_API;
    }

}
