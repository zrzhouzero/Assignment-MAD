package android.mad.assignment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import model.ApplicationSettingsHandler;

public class SettingPageFragment extends DialogFragment {

    private static final String TAG = "SettingPageFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup viewGroups, Bundle saveInstanceState) {

        Log.d(TAG, "onCreateView: created.");

        View view = inflater.inflate(R.layout.setting_fragment, viewGroups, false);

        this.getDialog().setTitle("Settings");

        EditText threshold = view.findViewById(R.id.threshold_input);
        threshold.setHint(String.valueOf(ApplicationSettingsHandler.getThresholdInMinutes()));
        EditText remind = view.findViewById(R.id.remind_again_input);
        remind.setHint(String.valueOf(ApplicationSettingsHandler.getRemindAgainDuration()));
        EditText period = view.findViewById(R.id.notification_period_input);
        period.setHint(String.valueOf(ApplicationSettingsHandler.getNotificationPeriod()));

        Button saveButton = view.findViewById(R.id.setting_page_save_button);
        saveButton.setOnClickListener(l -> {
            int t = 15;
            int r = 5;
            int p = 10;
            try {
                t = Integer.parseInt(threshold.getText().toString());
            } catch (Exception e) {
                Log.e(TAG, "onCreateView: Invalid threshold.");
            }
            try {
                r = Integer.parseInt(remind.getText().toString());
            } catch (Exception e) {
                Log.e(TAG, "onCreateView: Invalid remind.");
            }
            try {
                p = Integer.parseInt(period.getText().toString());
            } catch (Exception e) {
                Log.e(TAG, "onCreateView: Invalid period.");
            }
            ApplicationSettingsHandler.updateSettings(t, r, p);
            dismiss();
        });

        return view;
    }

}
