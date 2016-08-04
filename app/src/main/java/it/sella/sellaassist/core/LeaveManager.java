package it.sella.sellaassist.core;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

import it.sella.sellaassist.R;

/**
 * Created by GodwinRoseSamuel on 26-Jul-16.
 */
public class LeaveManager {
    public static String LMS_PHONE_NO = "+917358036185";
    // public static String LMS_PHONE_NO = "+919597474017";
    public static String LMS_SEPARATOR = "#";

    public static String LMS_SL = "SL";
    public static String LMS_CL = "CL";
    public static String LMS_EL = "EL";
    public static String LMS_FULL_DAY = "F";
    public static String LMS_MORNING = "M";
    public static String LMS_AFTERNOON = "A";

    public static String getLeaveType(Context context, int lmsLeaveTypeId) {
        String leaveType = null;
        if (lmsLeaveTypeId == -1) {
            leaveType = "None";
            Toast.makeText(context, "Please Select a Leave Type!!!", Toast.LENGTH_SHORT).show();
        } else {
            if (lmsLeaveTypeId == R.id.lms_sl_button) {
                leaveType = LMS_SL;
            } else if (lmsLeaveTypeId == R.id.lms_cl_button) {
                leaveType = LMS_CL;
            } else if (lmsLeaveTypeId == R.id.lms_el_button) {
                leaveType = LMS_EL;
            }
        }
        return leaveType;
    }

    public static String getLeaveSession(Context context, int lmsLeaveSessionId) {
        String leaveSession = null;
        if (lmsLeaveSessionId == -1) {
            leaveSession = "None";
            Toast.makeText(context, "Please Select a Leave Session!!!", Toast.LENGTH_SHORT).show();
        } else {
            if (lmsLeaveSessionId == R.id.lms_full_day_button) {
                leaveSession = LMS_FULL_DAY;
            } else if (lmsLeaveSessionId == R.id.lms_morning_button) {
                leaveSession = LMS_MORNING;
            } else if (lmsLeaveSessionId == R.id.lms_afternoon_button) {
                leaveSession = LMS_AFTERNOON;
            }
        }
        return leaveSession;
    }

    public static Boolean sendSMSMessage(String gbsCode, String leaveType, String leaveSession, String leaveDate, String leaveReason) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(LMS_PHONE_NO, null, getMessage(gbsCode, leaveType, leaveSession, leaveDate, leaveReason), null, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getMessage(String gbsCode, String leaveType, String leaveSession, String leaveDate, String leaveReason) {
        if (leaveType.equals(LMS_SL) && leaveSession.equals(LMS_FULL_DAY)) {
            return leaveType + LMS_SEPARATOR + gbsCode;
        }
        return leaveType + LMS_SEPARATOR + gbsCode + LMS_SEPARATOR + leaveReason + LMS_SEPARATOR + leaveSession + LMS_SEPARATOR + leaveDate;
    }
}
