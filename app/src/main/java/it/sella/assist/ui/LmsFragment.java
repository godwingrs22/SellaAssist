package it.sella.assist.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import it.sella.assist.R;
import it.sella.assist.core.LeaveManager;
import it.sella.assist.model.User;
import it.sella.assist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 26-Jul-16.
 */
public class LmsFragment extends Fragment {
    private static final String TAG = LmsFragment.class.getSimpleName();
    private TextView lmsGBSIdText;
    private EditText lmsDateText;
    private EditText lmsReasonText;
    private RadioGroup lmsLeaveTypeGroup;
    private RadioGroup lmsSessionGroup;
    private Button lmsSubmitButton;
    private LeaveManager leaveManager;
    private User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lms, container, false);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();

        TabLayout tabLayout = (TabLayout) appCompatActivity.findViewById(R.id.tabs);
        tabLayout.setVisibility(View.GONE);

        MainActivity activity = (MainActivity) getActivity();
        user = activity.getUser();

        leaveManager = new LeaveManager();

        lmsGBSIdText = (TextView) rootView.findViewById(R.id.lms_gbs_id_text);
        lmsLeaveTypeGroup = (RadioGroup) rootView.findViewById(R.id.lms_leave_type_group);
        lmsSessionGroup = (RadioGroup) rootView.findViewById(R.id.lms_session_group);
        lmsDateText = (EditText) rootView.findViewById(R.id.lms_date_text);
        lmsReasonText = (EditText) rootView.findViewById(R.id.lms_reason_text);
        lmsSubmitButton = (Button) rootView.findViewById(R.id.lms_submit);

        lmsDateText.setText(Utility.getFormattedDate(new Date().getTime()));

        lmsGBSIdText.setText(user.getGbsID());

        lmsDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerDialogFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, day);
                        lmsDateText.setText(Utility.getFormattedDate(c.getTimeInMillis()));
                    }
                };
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        lmsSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    int lmsLeaveTypeId = lmsLeaveTypeGroup.getCheckedRadioButtonId();
                    int lmsLeaveSessionId = lmsSessionGroup.getCheckedRadioButtonId();
                    String lmsLeaveType = leaveManager.getLeaveType(getContext(), lmsLeaveTypeId);
                    String lmsLeaveSession = leaveManager.getLeaveSession(getContext(), lmsLeaveSessionId);
                    String lmsLeaveDate = String.valueOf(lmsDateText.getText());
                    String lmsLeaveReason = String.valueOf(lmsReasonText.getText());

                    Log.v(TAG, "<----Selected Leave Type and Session--->" + lmsLeaveType + " / " + lmsLeaveSession);

                    boolean isSend = leaveManager.sendSMSMessage(user.getGbsID(), lmsLeaveType, lmsLeaveSession, lmsLeaveDate, lmsLeaveReason);
                    if (isSend) {
                        Snackbar.make(getView(), "Leave Request Sent Successfully", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                })
                                .show();
                        Log.v(TAG, "<----Message Sent Successfully--->");
                    } else {
                        Snackbar.make(getView(), "Unable to apply leave at the moment", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                })
                                .show();
                        Log.e(TAG, "<----Unable to Send Message--->");
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

        return rootView;
    }

}
