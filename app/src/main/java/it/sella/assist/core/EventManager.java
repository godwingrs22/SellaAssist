package it.sella.assist.core;

import android.content.Context;
import android.net.ParseException;
import android.os.Parcel;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import it.sella.assist.http.HttpClient;
import it.sella.assist.model.Event;
import it.sella.assist.ui.EventsFragment;
import it.sella.assist.util.ServerUtils;
import okhttp3.HttpUrl;

/**
 * Created by GodwinRoseSamuel on 29-Jul-16.
 */
public class EventManager {
    private static final String TAG = FeedsManager.class.getSimpleName();
    private Context context;
    private static final String SUCCESS_CODE = "BIOK";
    private static final String FAILURE_CODE = "BIKO";

    public EventManager(Context context) {
        this.context = context;
    }

    public boolean doInterestedEvent(String gbsCode, int eventId) {
        Boolean isSuccess = false;
        try {
            final HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme(ServerUtils.HTTP_PROTOCOL)
                    .host(ServerUtils.HOSTNAME)
                    .port(ServerUtils.PORT_NO)
                    .addPathSegments(ServerUtils.INTERESTED_EVENTS_API)
                    .addPathSegment(gbsCode)
                    .addPathSegment(Integer.toString(eventId))
                    .build();
            final String eventResponse = HttpClient.GET(httpUrl);
            isSuccess = isEventInterestedUpdatedSuccessful(eventResponse);
        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
        }
        return isSuccess;
    }

    private boolean isEventInterestedUpdatedSuccessful(String response) {
        final String BIOMETRIC_STATUS = "status";
        final String BIOMETRIC_CODE = "code";
        try {
            JSONObject responseJSON = new JSONObject(response);
            JSONObject statusJSON = (JSONObject) responseJSON.get(BIOMETRIC_STATUS);
            String code = statusJSON.getString(BIOMETRIC_CODE);
            if (SUCCESS_CODE.equals(code))
                return true;
            else if (FAILURE_CODE.equals(code))
                return false;
        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
            return false;
        }
        return false;
    }

    public List<Event> loadEvents() {
        List<Event> events = new LinkedList<>();
        try {
            final HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme(ServerUtils.HTTP_PROTOCOL)
                    .host(ServerUtils.HOSTNAME)
                    .port(ServerUtils.PORT_NO)
                    .addPathSegments(ServerUtils.EVENTS_API)
                    .build();
            final String eventData = HttpClient.GET(httpUrl);
            events = getEventDataFromJson(eventData);
        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
        }
        return events;
//        return demoListEvents();
    }

    private List<Event> getEventDataFromJson(final String data) {
        final String EVENT_LIST = "events";
        final String EVENT_ID = "id";
        final String EVENT_TITLE = "title";
        final String EVENT_CONTENT = "content";
        final String EVENT_LOCATION = "location";
        final String EVENT_BANNER_IMAGE = "bannerImage";
        final String EVENT_STARTTIMESTAMP = "startTimeStamp";
        final String EVENT_ENDTIMESTAMP = "endTimeStamp";
        final String EVENT_CREATEDBY = "createdBy";
        final String EVENT_CREATEDBY_NAME = "name";
        final String EVENT_CREATEDBY_IMAGE = "image";
        final String EVENT_CREATEDBY_TIMESTAMP = "timestamp";

        List<Event> events = new LinkedList<>();

        try {
            JSONObject eventDataJson = new JSONObject(data);
            JSONArray eventList = eventDataJson.getJSONArray(EVENT_LIST);

            if (eventList != null) {
                for (int i = 0; i < eventList.length(); i++) {
                    JSONObject eventJson = (JSONObject) eventList.get(i);
                    JSONObject createdBy = (JSONObject) eventJson.get(EVENT_CREATEDBY);

                    String createdByName = createdBy.getString(EVENT_CREATEDBY_NAME);
                    String profileImage = ServerUtils.getServerURL() + createdBy.getString(EVENT_CREATEDBY_IMAGE);
                    Long createdByTimestamp = createdBy.isNull(EVENT_CREATEDBY_TIMESTAMP) ? new Date().getTime() : createdBy.getLong(EVENT_CREATEDBY_TIMESTAMP);

                    int id = eventJson.getInt(EVENT_ID);
                    String title = eventJson.getString(EVENT_TITLE);
                    String content = eventJson.getString(EVENT_CONTENT);
                    String location = eventJson.getString(EVENT_LOCATION);
                    Long startTimestamp = eventJson.getLong(EVENT_STARTTIMESTAMP);
                    Long endTimestamp = eventJson.getLong(EVENT_ENDTIMESTAMP);
                    String bannerImage = eventJson.isNull(EVENT_BANNER_IMAGE) ? "" : ServerUtils.getServerURL() + eventJson.getString(EVENT_BANNER_IMAGE);


                    Event event = new Event(Parcel.obtain());
                    event.setId(id);
                    event.setTitle(title);
                    event.setStartTimestamp(startTimestamp);
                    event.setEndTimestamp(endTimestamp);
                    event.setBannerImage(bannerImage);
                    event.setCreatedByName(createdByName);
                    event.setCreatedByProfileImage(profileImage);
                    event.setCreatedByTimestamp(createdByTimestamp);
                    event.setAddress(location);
                    event.setInterested(EventsFragment.EVENT_INTERESTED_NONE);
                    event.setRemainder(false);
                    event.setDescription(content);

                    events.add(event);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Parsing err" + e);
        } catch (ParseException e) {
            Log.e(TAG, "Date Parsing err" + e);
        }
        return events;
    }

    private List<Event> demoListEvents() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        List<Event> demoEvents = new ArrayList<>();
        try {
            Event event1 = new Event(Parcel.obtain());
            event1.setId(11);
            event1.setTitle("Web Analytics Session");
            event1.setStartTimestamp(simpleDateFormat.parse("30-08-2016 10:00:00").getTime());
            event1.setEndTimestamp(simpleDateFormat.parse("30-08-2016 13:00:00").getTime());
            event1.setBannerImage("http://barnraisersllc.com/wp-content/uploads/2016/07/web-analytics-tool.png");
            event1.setCreatedByName("PREMKUMAR");
            event1.setCreatedByProfileImage("http://117.218.241.218:8080/biot/static/images/users/GBS02286.jpeg");
            event1.setCreatedByTimestamp(simpleDateFormat.parse("26-07-2016 09:00:00").getTime());
            event1.setAddress("Anna University");
            event1.setInterested(EventsFragment.EVENT_INTERESTED_NONE);
            event1.setRemainder(false);
            event1.setDescription("Web analytics is the measurement, collection, analysis and reporting of web data for purposes of understanding and optimizing web usage.[1] However, Web analytics is not just a process for measuring web traffic but can be used as a tool for business and market research, and to assess and improve the effectiveness of a website. Web analytics applications can also help companies measure the results of traditional print or broadcast advertising campaigns.");

            Event event2 = new Event(Parcel.obtain());
            event2.setId(12);
            event2.setTitle("IOT Workshop");
            event2.setStartTimestamp(simpleDateFormat.parse("31-07-2016 09:00:00").getTime());
            event2.setEndTimestamp(simpleDateFormat.parse("31-07-2016 12:00:00").getTime());
            event2.setBannerImage("");
            event2.setCreatedByName("BSINDIA HR");
            event2.setCreatedByProfileImage("https://pbs.twimg.com/profile_images/443749242000011264/SXwa4oTP.png");
            event2.setCreatedByTimestamp(simpleDateFormat.parse("26-07-2016 10:00:00").getTime());
            event2.setAddress("Kapalan & Norton");
            event2.setInterested(EventsFragment.EVENT_INTERESTED_NONE);
            event2.setRemainder(false);
            event2.setDescription("Web analytics is the measurement, collection, analysis and reporting of web data for purposes of understanding and optimizing web usage.[1] However, Web analytics is not just a process for measuring web traffic but can be used as a tool for business and market research, and to assess and improve the effectiveness of a website. Web analytics applications can also help companies measure the results of traditional print or broadcast advertising campaigns.");

            Event event3 = new Event(Parcel.obtain());
            event3.setId(13);
            event3.setTitle("Java 8 Conference");
            event3.setStartTimestamp(simpleDateFormat.parse("05-08-2016 15:00:00").getTime());
            event3.setEndTimestamp(simpleDateFormat.parse("05-08-2016 18:00:00").getTime());
            event3.setBannerImage("https://javakrakow2.ticketforevent.com/logos/51668/header.jpg");
            event3.setCreatedByName("PREMKUMAR");
            event3.setCreatedByProfileImage("http://117.218.241.218:8080/biot/static/images/users/GBS02286.jpeg");
            event3.setCreatedByTimestamp(simpleDateFormat.parse("02-08-2016 10:00:00").getTime());
            event3.setAddress("London");
            event3.setInterested(EventsFragment.EVENT_INTERESTED_NONE);
            event3.setRemainder(false);
            event3.setDescription("Web analytics is the measurement, collection, analysis and reporting of web data for purposes of understanding and optimizing web usage.[1] However, Web analytics is not just a process for measuring web traffic but can be used as a tool for business and market research, and to assess and improve the effectiveness of a website. Web analytics applications can also help companies measure the results of traditional print or broadcast advertising campaigns.");

            Event event4 = new Event(Parcel.obtain());
            event4.setId(14);
            event4.setTitle("Health Managment Awarness Program");
            event4.setStartTimestamp(simpleDateFormat.parse("15-08-2016 11:00:00").getTime());
            event4.setEndTimestamp(simpleDateFormat.parse("15-08-2016 11:30:00").getTime());
            event4.setBannerImage("http://www.valpoonline.com/media/7907789/careers-in-healthcare-management.jpg");
            event4.setCreatedByName("BSINDIA HR");
            event4.setCreatedByProfileImage("https://pbs.twimg.com/profile_images/443749242000011264/SXwa4oTP.png");
            event4.setCreatedByTimestamp(simpleDateFormat.parse("15-08-2016 06:00:00").getTime());
            event4.setAddress("Banca Sella");
            event4.setInterested(EventsFragment.EVENT_INTERESTED_NONE);
            event4.setRemainder(false);
            event4.setDescription("Web analytics is the measurement, collection, analysis and reporting of web data for purposes of understanding and optimizing web usage.[1] However, Web analytics is not just a process for measuring web traffic but can be used as a tool for business and market research, and to assess and improve the effectiveness of a website. Web analytics applications can also help companies measure the results of traditional print or broadcast advertising campaigns.");

            Event event5 = new Event(Parcel.obtain());
            event5.setId(15);
            event5.setTitle("Web Analytics Session");
            event5.setStartTimestamp(simpleDateFormat.parse("15-08-2016 10:00:00").getTime());
            event5.setEndTimestamp(simpleDateFormat.parse("16-08-2016 10:00:00").getTime());
            event5.setBannerImage("http://barnraisersllc.com/wp-content/uploads/2016/07/web-analytics-tool.png");
            event5.setCreatedByName("PREMKUMAR");
            event5.setCreatedByProfileImage("http://117.218.241.218:8080/biot/static/images/users/GBS02286.jpeg");
            event5.setCreatedByTimestamp(simpleDateFormat.parse("30-07-2016 10:00:00").getTime());
            event5.setAddress("Anna University");
            event5.setInterested(EventsFragment.EVENT_INTERESTED_NONE);
            event5.setRemainder(false);
            event5.setDescription("Web analytics is the measurement, collection, analysis and reporting of web data for purposes of understanding and optimizing web usage.[1] However, Web analytics is not just a process for measuring web traffic but can be used as a tool for business and market research, and to assess and improve the effectiveness of a website. Web analytics applications can also help companies measure the results of traditional print or broadcast advertising campaigns.");

            Event event6 = new Event(Parcel.obtain());
            event6.setId(16);
            event6.setTitle("Web Analytics Session");
            event6.setStartTimestamp(simpleDateFormat.parse("30-07-2016 10:00:00").getTime());
            event6.setEndTimestamp(simpleDateFormat.parse("30-07-2016 10:00:00").getTime());
            event6.setBannerImage("http://barnraisersllc.com/wp-content/uploads/2016/07/web-analytics-tool.png");
            event6.setCreatedByName("PREMKUMAR");
            event6.setCreatedByProfileImage("http://117.218.241.218:8080/biot/static/images/users/GBS02286.jpeg");
            event6.setCreatedByTimestamp(simpleDateFormat.parse("30-07-2016 10:00:00").getTime());
            event6.setAddress("Anna University");
            event6.setInterested(EventsFragment.EVENT_INTERESTED_NONE);
            event6.setRemainder(false);
            event6.setDescription("Web analytics is the measurement, collection, analysis and reporting of web data for purposes of understanding and optimizing web usage.[1] However, Web analytics is not just a process for measuring web traffic but can be used as a tool for business and market research, and to assess and improve the effectiveness of a website. Web analytics applications can also help companies measure the results of traditional print or broadcast advertising campaigns.");

            Event event7 = new Event(Parcel.obtain());
            event7.setId(17);
            event7.setTitle("Web Analytics Session");
            event7.setStartTimestamp(simpleDateFormat.parse("30-07-2016 10:00:00").getTime());
            event7.setEndTimestamp(simpleDateFormat.parse("30-07-2016 10:00:00").getTime());
            event7.setBannerImage("http://barnraisersllc.com/wp-content/uploads/2016/07/web-analytics-tool.png");
            event7.setCreatedByName("PREMKUMAR");
            event7.setCreatedByProfileImage("http://117.218.241.218:8080/biot/static/images/users/GBS02286.jpeg");
            event7.setCreatedByTimestamp(simpleDateFormat.parse("30-07-2016 10:00:00").getTime());
            event7.setAddress("Anna University");
            event7.setInterested(EventsFragment.EVENT_INTERESTED_NONE);
            event7.setRemainder(false);
            event7.setDescription("Web analytics is the measurement, collection, analysis and reporting of web data for purposes of understanding and optimizing web usage.[1] However, Web analytics is not just a process for measuring web traffic but can be used as a tool for business and market research, and to assess and improve the effectiveness of a website. Web analytics applications can also help companies measure the results of traditional print or broadcast advertising campaigns.");

            Event event8 = new Event(Parcel.obtain());
            event8.setId(18);
            event8.setTitle("Web Analytics Session");
            event8.setStartTimestamp(simpleDateFormat.parse("30-07-2016 10:00:00").getTime());
            event8.setEndTimestamp(simpleDateFormat.parse("30-07-2016 10:00:00").getTime());
            event8.setBannerImage("http://barnraisersllc.com/wp-content/uploads/2016/07/web-analytics-tool.png");
            event8.setCreatedByName("PREMKUMAR");
            event8.setCreatedByProfileImage("http://117.218.241.218:8080/biot/static/images/users/GBS02286.jpeg");
            event8.setCreatedByTimestamp(simpleDateFormat.parse("30-07-2016 10:00:00").getTime());
            event8.setAddress("Anna University");
            event8.setInterested(EventsFragment.EVENT_INTERESTED_NONE);
            event8.setRemainder(false);
            event8.setDescription("Web analytics is the measurement, collection, analysis and reporting of web data for purposes of understanding and optimizing web usage.[1] However, Web analytics is not just a process for measuring web traffic but can be used as a tool for business and market research, and to assess and improve the effectiveness of a website. Web analytics applications can also help companies measure the results of traditional print or broadcast advertising campaigns.");

            Event event9 = new Event(Parcel.obtain());
            event9.setId(23);
            event9.setTitle("Web Analytics Session");
            event9.setStartTimestamp(simpleDateFormat.parse("28-07-2016 01:55:00").getTime());
            event9.setEndTimestamp(simpleDateFormat.parse("30-07-2016 10:00:00").getTime());
            event9.setBannerImage("http://barnraisersllc.com/wp-content/uploads/2016/07/web-analytics-tool.png");
            event9.setCreatedByName("PREMKUMAR");
            event9.setCreatedByProfileImage("http://117.218.241.218:8080/biot/static/images/users/GBS02286.jpeg");
            event9.setCreatedByTimestamp(simpleDateFormat.parse("30-07-2016 10:00:00").getTime());
            event9.setAddress("Anna University");
            event9.setInterested(EventsFragment.EVENT_INTERESTED_NONE);
            event9.setRemainder(false);
            event9.setDescription("Web analytics is the measurement, collection, analysis and reporting of web data for purposes of understanding and optimizing web usage.[1] However, Web analytics is not just a process for measuring web traffic but can be used as a tool for business and market research, and to assess and improve the effectiveness of a website. Web analytics applications can also help companies measure the results of traditional print or broadcast advertising campaigns.");

            Event event10 = new Event(Parcel.obtain());
            event10.setId(22);
            event10.setTitle("Web Analytics Session");
            event10.setStartTimestamp(simpleDateFormat.parse("28-07-2016 01:59:00").getTime());
            event10.setEndTimestamp(simpleDateFormat.parse("27-07-2016 23:55:00").getTime());
            event10.setBannerImage("http://barnraisersllc.com/wp-content/uploads/2016/07/web-analytics-tool.png");
            event10.setCreatedByName("PREMKUMAR");
            event10.setCreatedByProfileImage("http://117.218.241.218:8080/biot/static/images/users/GBS02286.jpeg");
            event10.setCreatedByTimestamp(simpleDateFormat.parse("27-07-2016 20:47:00").getTime());
            event10.setAddress("Anna University");
            event10.setInterested(EventsFragment.EVENT_INTERESTED_NONE);
            event10.setRemainder(false);
            event10.setDescription("Web analytics is the measurement, collection, analysis and reporting of web data for purposes of understanding and optimizing web usage.[1] However, Web analytics is not just a process for measuring web traffic but can be used as a tool for business and market research, and to assess and improve the effectiveness of a website. Web analytics applications can also help companies measure the results of traditional print or broadcast advertising campaigns.");


            demoEvents.add(event1);
            demoEvents.add(event2);
            demoEvents.add(event3);
            demoEvents.add(event4);
            demoEvents.add(event5);
            demoEvents.add(event6);
            demoEvents.add(event7);
            demoEvents.add(event8);
            demoEvents.add(event9);
            demoEvents.add(event10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return demoEvents;
    }
}
