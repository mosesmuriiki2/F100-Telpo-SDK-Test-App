package com.ftpos.ftappdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ftpos.library.smartpos.buzzer.Buzzer;
import com.ftpos.library.smartpos.buzzer.BuzzerMode;
import com.ftpos.library.smartpos.errcode.ErrCode;
import com.ftpos.library.smartpos.util.BytesUtils;


public class BuzzerActivity extends BaseActivity {

    private TextView mShowResultTv;
    private RelativeLayout mSetBuzzerState;

    private EditText mFrequency;
    private EditText mTimes;
    private EditText mOnTimes;
    private EditText mOffTimes;
    private EditText mVolume;

    private Spinner mAmountCmdSpinner, mAmountIndexSpinner, mContentCmdSpinner, mContentIndexSpinner;

    private EditText mAmount, mContent;

    private RelativeLayout mRlAmount, mRlContent;

    private int mAmountCmd, mAmountIndex, mContentCmd, mContentIndex;

    private Buzzer buzzer;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buzzer);
        initView();

        this.buzzer = MainActivity.buzzer;

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        mSetBuzzerState.setEnabled(true);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void initView() {
        TextView mTitle = findViewById(R.id.ukey_navbar_title);
        mTitle.setText("Buzzer View");

        findViewById(R.id.ukey_navbar_left_btn).setOnClickListener(this);
        findViewById(R.id.ukey_navbar_right_btn).setOnClickListener(this);
        findViewById(R.id.set_buzzer_frequency).setOnClickListener(this);
        findViewById(R.id.set_volume).setOnClickListener(this);

        mSetBuzzerState = findViewById(R.id.set_buzzer_status);
        mSetBuzzerState.setOnClickListener(this);

        mFrequency = findViewById(R.id.frequency);
        mFrequency.setText("2000");

        mTimes = findViewById(R.id.times);
        mTimes.setText("3");

        mOnTimes = findViewById(R.id.onTimes);
        mOnTimes.setText("200");

        mOffTimes = findViewById(R.id.offTimes);
        mOffTimes.setText("200");

        mVolume = findViewById(R.id.volume);
        mVolume.setText("4");

        mShowResultTv = findViewById(R.id.function_return_result_tv);

        mAmountCmdSpinner = findViewById(R.id.amount_cmd_spinner);
        mAmountIndexSpinner = findViewById(R.id.amount_index_spinner);
        mContentCmdSpinner = findViewById(R.id.content_cmd_spinner);
        mContentIndexSpinner = findViewById(R.id.content_index_spinner);

        mAmount = findViewById(R.id.amount);
        mContent = findViewById(R.id.content);

        mRlAmount = findViewById(R.id.beep_amount);
        mRlContent = findViewById(R.id.beep_content);

        mRlAmount.setOnClickListener(this);
        mRlContent.setOnClickListener(this);

        mAmountCmdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mAmountCmd = 1 + i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mAmountCmd = 1;
            }
        });

        mAmountIndexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mAmountIndex = 1 + i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mAmountCmd = 1;
            }
        });

        mContentCmdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mContentCmd = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mContentCmd = 0;
            }
        });

        mContentIndexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mContentIndex = 51 + i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mContentIndex = 51;
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ukey_navbar_left_btn: {
                finish();
            }
            break;
            case R.id.ukey_navbar_right_btn: {
                mShowResultTv.setText("");
            }
            break;
            case R.id.set_buzzer_frequency: {
                setBuzzerFrequency();
            }
            break;
            case R.id.set_buzzer_status: {
                buzzerBeep();
            }
            break;
            case R.id.set_volume: {
                setVolume();
            }
            break;
            case R.id.beep_amount: {
                beepAmount();
            }
            break;
            case R.id.beep_content: {
                beepContent();
            }
            break;
            default:
                break;
        }
    }

    private void beepContent() {
        String content = mContent.getText().toString();
        logMsg("mContentCmd : " + mContentCmd
        + " \nmContentIndex : " + mContentIndex
        + " \ncontent : " + content);
        int ret = buzzer.beepContent(mContentCmd, mContentIndex, content.getBytes());
        if (ret == 0) {
            logMsg("beepContent Success");
        } else {
            logMsg("beepContent Failed : " + ret);
        }
    }

    private void beepAmount() {
        String s = mAmount.getText().toString();
        int amount = TextUtils.isEmpty(s)? 0 :Integer.parseInt(mAmount.getText().toString());
        int ret = buzzer.beepAmount(mAmountCmd, amount, mAmountIndex);
        if (ret == 0) {
            logMsg("beepAmount Success");
        } else {
            logMsg("beepAmount Failed : " + ret);
        }
    }

    public synchronized void logMsg(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String oldMsg = mShowResultTv.getText().toString();
                mShowResultTv.setText(oldMsg + "\n" + msg);
            }
        });
    }

    void setVolume() {
        int vol;
        int ret;

        try {
            String str = mVolume.getText().toString();
            vol = (int) Long.parseLong(str);

            ret = buzzer.setSystemVolume(vol);
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("setVolume failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            logMsg("setVolume success\n");
        } catch (Exception e) {
            logMsg("Exception " + e.toString() + "\n");
        }
    }

    /**
     * Set the buzzer frequency.
     */
    void setBuzzerFrequency() {
        int frequency;
        int ret;

        try {
            String str = mFrequency.getText().toString();

            frequency = (int) Long.parseLong(str);

            if (frequency < 500 || frequency > 6000) {
                logMsg("Set fail, parameter invalid (500-6k)" + String.format(" frequency = %d\n", frequency));
                return;
            }

            ret = buzzer.setBuzzerFrequency(frequency);
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("setBuzzerFrequency failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            logMsg("setBuzzerFrequency success\n");
        } catch (Exception e) {
            logMsg("Exception " + e.toString() + "\n");
        }
    }

    /**
     * Example of invoking a buzzer buzzer interface.
     */
    void buzzerBeep() {
        mSetBuzzerState.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int times, onTimes, offtimes;
                int ret;

                try {
                    String str = mTimes.getText().toString();

                    times = (int) Long.parseLong(str);
                    if (times < 1) {
                        logMsg("Parameter invalid " + String.format(" times = %d\n", times));
                        return;
                    }

                    str = mOnTimes.getText().toString();
                    onTimes = (int) Long.parseLong(str);
                    if (onTimes < 0) {
                        logMsg("Parameter invalid " + String.format(" onTimes = %d\n", onTimes));
                        return;
                    }

                    str = mOffTimes.getText().toString();
                    offtimes = (int) Long.parseLong(str);
                    if (offtimes < 0) {
                        logMsg("Parameter invalid " + String.format(" offtimes = %d\n", offtimes));
                        return;
                    }

                    ret = buzzer.beep(times, onTimes, offtimes, BuzzerMode.BUZZER_MODE_SYNC);
                    if (ret != ErrCode.ERR_SUCCESS) {
                        logMsg("setBuzzerFrequency failed" + String.format(" errCode = 0x%x\n", ret));
                        return;
                    }

                    logMsg("buzzerBeep success\n");
                } catch (Exception e) {
                    logMsg("Exception " + e.toString() + "\n");
                } finally {
                    Message msg = new Message();
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
}
