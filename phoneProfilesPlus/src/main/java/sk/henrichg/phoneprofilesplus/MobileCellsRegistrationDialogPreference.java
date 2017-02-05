package sk.henrichg.phoneprofilesplus;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.redmadrobot.inputmask.MaskedTextChangedListener;

public class MobileCellsRegistrationDialogPreference extends DialogPreference
                                        implements SeekBar.OnSeekBarChangeListener {

    private String value;
    private Context context;

    private int mMin, mMax;

    MaterialDialog mDialog;
    private EditText mValue;
    private SeekBar mSeekBarHours;
    private SeekBar mSeekBarMinutes;
    private SeekBar mSeekBarSeconds;
    private EditText mCellsName;
    private TextView mStatus;
    private TextView mRemainingTime;

    //private int mColor = 0;

    public MobileCellsRegistrationDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray durationDialogType = context.obtainStyledAttributes(attrs,
                R.styleable.DurationDialogPreference, 0, 0);

        mMax = durationDialogType.getInt(R.styleable.DurationDialogPreference_dMax, 5);
        mMin = durationDialogType.getInt(R.styleable.DurationDialogPreference_dMin, 0);

        durationDialogType.recycle();

        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        //    mColor = DialogUtils.resolveColor(context, R.attr.colorAccent);

        this.context = context;

        if (!PhoneProfilesService.isPhoneStateStarted()) {
            //Log.d("MobileCellsPreference","no scanner started");
            PPApplication.startPhoneStateScanner(context);
        }
        //else
        //    Log.d("MobileCellsPreference","scanner started");

        PPApplication.getMobileCellsAutoRegistration(context);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MobileCellsRegistrationService.ACTION_COUNT_DOWN_TICK);
        PhoneProfilesPreferencesNestedFragment.mobileCellsRegistrationBroadcastReceiver =
                new MobileCellsRegistrationBroadcastReceiver(this);
        context.registerReceiver(PhoneProfilesPreferencesNestedFragment.mobileCellsRegistrationBroadcastReceiver, intentFilter);

    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void showDialog(Bundle state) {
        MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(getContext())
                .title(getDialogTitle())
                        //.disableDefaultFonts()
                .icon(getDialogIcon())
                .positiveText(R.string.mobile_cells_registration_pref_dlg_start_button)
                .negativeText(getNegativeButtonText())
                .neutralText(R.string.mobile_cells_registration_pref_dlg_stop_button)
                .content(getDialogMessage())
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        int hours = mSeekBarHours.getProgress();
                        int minutes = mSeekBarMinutes.getProgress();
                        int seconds = mSeekBarSeconds.getProgress();

                        int iValue = (hours * 3600 + minutes * 60 + seconds);
                        if (iValue < mMin) iValue = mMin;
                        if (iValue > mMax) iValue = mMax;

                        //Log.d("MobileCellsRegistrationDialogPreference.onPositive","iValue="+iValue);

                        if (PhoneProfilesService.isPhoneStateStarted()) {
                            //Log.d("MobileCellsRegistrationDialogPreference.onPositive","is started");
                            PPApplication.setMobileCellsAutoRegistrationRemainingDuration(context, iValue);
                            PhoneProfilesService.phoneStateScanner.durationForAutoRegistration = iValue;
                            PhoneProfilesService.phoneStateScanner.cellsNameForAutoRegistration = mCellsName.getText().toString();
                            PhoneProfilesService.phoneStateScanner.enabledAutoRegistration = true;
                            PPApplication.setMobileCellsAutoRegistration(context, false);
                            PhoneProfilesService.phoneStateScanner.startAutoRegistration();
                        }
                        value = String.valueOf(iValue);
                        setSummaryDDP(0);

                        /*
                        value = String.valueOf(iValue);

                        if (callChangeListener(value)) {
                            //persistInt(mNumberPicker.getValue());
                            persistString(value);
                        }
                        */
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        if (PhoneProfilesService.isPhoneStateStarted()) {
                            PPApplication.setMobileCellsAutoRegistrationRemainingDuration(context, 0);
                            //PPApplication.phoneProfilesService.phoneStateScanner.durationForAutoRegistration = 0;
                            //PPApplication.phoneProfilesService.phoneStateScanner.cellsNameForAutoRegistration = "";
                            PhoneProfilesService.phoneStateScanner.enabledAutoRegistration = false;
                            PPApplication.setMobileCellsAutoRegistration(context, false);
                            setSummaryDDP(0);
                            PhoneProfilesService.phoneStateScanner.stopAutoRegistration();
                        }
                    }
                })
                ;

        View layout = LayoutInflater.from(getContext()).inflate(R.layout.activity_mobile_cells_registration_pref_dialog, null);
        onBindDialogView(layout);

        TextView mTextViewRange = (TextView) layout.findViewById(R.id.duration_pref_dlg_range);
        mValue = (EditText) layout.findViewById(R.id.duration_pref_dlg_value);
        mSeekBarHours = (SeekBar) layout.findViewById(R.id.duration_pref_dlg_hours);
        mSeekBarMinutes = (SeekBar) layout.findViewById(R.id.duration_pref_dlg_minutes);
        mSeekBarSeconds = (SeekBar) layout.findViewById(R.id.duration_pref_dlg_seconds);
        mCellsName = (EditText)  layout.findViewById(R.id.mobile_cells_registration_cells_name);
        mStatus = (TextView)  layout.findViewById(R.id.mobile_cells_registration_status);
        mRemainingTime = (TextView)  layout.findViewById(R.id.mobile_cells_registration_remaining_time);

        mCellsName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = mCellsName.getText().toString();
                MDButton button = mDialog.getActionButton(DialogAction.POSITIVE);
                button.setEnabled(!value.isEmpty());
            }
        });

        //mSeekBarHours.setRotation(180);
        //mSeekBarMinutes.setRotation(180);
        //mSeekBarSeconds.setRotation(180);

        // Initialize state
        int hours;
        int minutes;
        int seconds;
        hours = mMax / 3600;
        minutes = (mMax % 3600) / 60;
        seconds = mMax % 60;
        final String sMax = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        mSeekBarHours.setMax(hours);
        if (hours == 0)
            mSeekBarMinutes.setMax(minutes);
        else
            mSeekBarMinutes.setMax(59);
        if ((hours == 0) && (minutes == 0))
            mSeekBarSeconds.setMax(seconds);
        else
            mSeekBarSeconds.setMax(59);
        hours = mMin / 3600;
        minutes = (mMin % 3600) / 60;
        seconds = mMin % 60;
        final String sMin = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        int iValue = Integer.valueOf(value);
        hours = iValue / 3600;
        minutes = (iValue % 3600) / 60;
        seconds = iValue % 60;
        mSeekBarHours.setProgress(hours);
        mSeekBarMinutes.setProgress(minutes);
        mSeekBarSeconds.setProgress(seconds);

        mValue.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

        final MaskedTextChangedListener listener = new MaskedTextChangedListener(
                "[00]{:}[00]{:}[00]",
                true,
                mValue,
                null,
                new MaskedTextChangedListener.ValueListener() {
                    @Override
                    public void onTextChanged(boolean maskFilled, @NonNull final String extractedValue) {
                        Log.d(DurationDialogPreference2.class.getSimpleName(), extractedValue);
                        Log.d(DurationDialogPreference2.class.getSimpleName(), String.valueOf(maskFilled));

                        int hours = 0;
                        int minutes = 0;
                        int seconds = 0;
                        String[] splits = extractedValue.split(":");
                        try {
                            hours = Integer.parseInt(splits[0].replaceFirst("\\s+$", ""));
                        } catch (Exception ignored) {
                        }
                        try {
                            minutes = Integer.parseInt(splits[1].replaceFirst("\\s+$", ""));
                        } catch (Exception ignored) {
                        }
                        try {
                            seconds = Integer.parseInt(splits[2].replaceFirst("\\s+$", ""));
                        } catch (Exception ignored) {
                        }

                        int iValue = (hours * 3600 + minutes * 60 + seconds);

                        boolean badText = false;
                        if (iValue < mMin) {
                            iValue = mMin;
                            badText = true;
                        }
                        if (iValue > mMax) {
                            iValue = mMax;
                            badText = true;
                        }

                        if (mDialog != null) {
                            MDButton button = mDialog.getActionButton(DialogAction.POSITIVE);
                            button.setEnabled(!badText);
                        }

                        hours = iValue / 3600;
                        minutes = (iValue % 3600) / 60;
                        seconds = iValue % 60;

                        mSeekBarHours.setProgress(hours);
                        mSeekBarMinutes.setProgress(minutes);
                        mSeekBarSeconds.setProgress(seconds);
                    }
                }
        );
        mValue.addTextChangedListener(listener);
        mValue.setOnFocusChangeListener(listener);
        mValue.setHint(listener.placeholder());

        mSeekBarHours.setOnSeekBarChangeListener(this);
        mSeekBarMinutes.setOnSeekBarChangeListener(this);
        mSeekBarSeconds.setOnSeekBarChangeListener(this);

        mTextViewRange.setText(sMin + " - " + sMax);

        mBuilder.customView(layout, false);

        mDialog = mBuilder.build();
        if (state != null)
            mDialog.onRestoreInstanceState(state);

        mDialog.setOnDismissListener(this);

        String value = mCellsName.getText().toString();
        MDButton button = mDialog.getActionButton(DialogAction.POSITIVE);
        button.setEnabled(!value.isEmpty());

        mDialog.show();

        updateInterface(0);

    }

    @Override
    protected Object onGetDefaultValue(TypedArray ta, int index)
    {
        super.onGetDefaultValue(ta, index);
        return ta.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        /*if(restoreValue)
        {
            value = getPersistedString(value);
        }
        else
        {
            value = (String)defaultValue;
            persistString(value);
        }*/

        if (PhoneProfilesService.isPhoneStateStarted()) {
            value = Integer.toString(PhoneProfilesService.phoneStateScanner.durationForAutoRegistration);
            //Log.d("MobileCellsRegistrationDialogPreference.onSetInitialValue", "value=" + value);
        } else {
            value = "0";
            //Log.d("MobileCellsRegistrationDialogPreference.onSetInitialValue", "value=" + value);
        }

        setSummaryDDP(0);
    }

    @SuppressLint("DefaultLocale")
    private void setSummaryDDP(long millisUntilFinished)
    {
        String summary = "";
        boolean started = false;
        if (PhoneProfilesService.isPhoneStateStarted()) {
            if (PhoneProfilesService.phoneStateScanner.enabledAutoRegistration) {
                if (millisUntilFinished > 0) {
                    summary = getContext().getString(R.string.mobile_cells_registration_pref_dlg_status_started);
                    String time = getContext().getString(R.string.mobile_cells_registration_pref_dlg_status_remaining_time);
                    long iValue = millisUntilFinished / 1000;
                    long hours = iValue / 3600;
                    long minutes = (iValue % 3600) / 60;
                    long seconds = iValue % 60;
                    time = time + ": " + String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    summary = summary + "; " + time;
                            started = true;
                }
            }
        }
        if (!started) {
            summary = getContext().getString(R.string.mobile_cells_registration_pref_dlg_status_stopped);
            int iValue = Integer.parseInt(value);
            int hours = iValue / 3600;
            int minutes = (iValue % 3600) / 60;
            int seconds = iValue % 60;
            summary = summary + "; " + String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        setSummary(summary);
    }

    @SuppressLint("DefaultLocale")
    private void updateInterface(long millisUntilFinished) {
        if ((mDialog != null) && mDialog.isShowing()) {
            boolean started = false;
            if (PhoneProfilesService.isPhoneStateStarted()) {
                mCellsName.setText(PhoneProfilesService.phoneStateScanner.cellsNameForAutoRegistration);
                if (PhoneProfilesService.phoneStateScanner.enabledAutoRegistration) {
                    mStatus.setText(R.string.mobile_cells_registration_pref_dlg_status_started);
                    if (millisUntilFinished > 0) {
                        mRemainingTime.setVisibility(View.VISIBLE);
                        String time = getContext().getString(R.string.mobile_cells_registration_pref_dlg_status_remaining_time);
                        long iValue = millisUntilFinished / 1000;
                        long hours = iValue / 3600;
                        long minutes = (iValue % 3600) / 60;
                        long seconds = iValue % 60;
                        time = time + ": " + String.format("%02d:%02d:%02d", hours, minutes, seconds);
                        mRemainingTime.setText(time);
                        started = true;
                    }
                }
            } else {
                mCellsName.setText("");
            }
            if (!started) {
                mStatus.setText(R.string.mobile_cells_registration_pref_dlg_status_stopped);
                mRemainingTime.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            int hours = mSeekBarHours.getProgress();
            int minutes = mSeekBarMinutes.getProgress();
            int seconds = mSeekBarSeconds.getProgress();

            int iValue = (hours * 3600 + minutes * 60 + seconds);
            if (iValue < mMin) iValue = mMin;
            if (iValue > mMax) iValue = mMax;

            hours = iValue / 3600;
            minutes = (iValue % 3600) / 60;
            seconds = iValue % 60;

            mValue.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public class MobileCellsRegistrationBroadcastReceiver extends BroadcastReceiver {

        MobileCellsRegistrationDialogPreference preference;

        MobileCellsRegistrationBroadcastReceiver(MobileCellsRegistrationDialogPreference preference) {
            this.preference = preference;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d("MobileCellsRegistrationBroadcastReceiver", "xxx");
            long millisUntilFinished = intent.getLongExtra(MobileCellsRegistrationService.EXTRA_COUNTDOWN, 0L);
            preference.updateInterface(millisUntilFinished);
            preference.setSummaryDDP(millisUntilFinished);
        }
    }

}