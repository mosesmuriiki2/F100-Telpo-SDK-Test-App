package com.ftpos.ftappdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ftpos.library.smartpos.accessory.AccessoryManager;
import com.ftpos.library.smartpos.datautils.IntTypeValue;
import com.ftpos.library.smartpos.errcode.ErrCode;


public class AccessoryManagerActivity extends BaseActivity {

    private final String TAG = AccessoryManagerActivity.class.getSimpleName();

    private TextView mShowResultTv;
    private EditText etPort;

    private AccessoryManager accessoryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessory_manager);

        initView();

        this.accessoryManager = MainActivity.accessoryManager;
    }

    private void initView() {
        TextView mTitle = findViewById(R.id.ukey_navbar_title);
        mTitle.setText("Accessory View");
        findViewById(R.id.ukey_navbar_left_btn).setOnClickListener(this);
        findViewById(R.id.ukey_navbar_right_btn).setOnClickListener(this);
        findViewById(R.id.readWbsGpio).setOnClickListener(this);
        findViewById(R.id.wbsGpioOn).setOnClickListener(this);
        findViewById(R.id.wbsGpioOff).setOnClickListener(this);

        mShowResultTv = findViewById(R.id.function_return_result_tv);
        etPort = findViewById(R.id.etPort);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ukey_navbar_left_btn) {
            finish();
        } else if (id == R.id.ukey_navbar_right_btn) {
            mShowResultTv.setText("");
        } else if (id == R.id.readWbsGpio) {
            readWbsGpio();
        } else if (id == R.id.wbsGpioOn) {
            wbsGpioOn();
        } else if (id == R.id.wbsGpioOff) {
            wbsGpioOff();
        }
    }

    public synchronized void logMsg(String msg) {
        runOnUiThread(() -> {
            String oldMsg = mShowResultTv.getText().toString();
            mShowResultTv.setText(oldMsg + "\n" + msg);
        });
    }


    private void readWbsGpio() {
        try {
            int port = Integer.parseInt(etPort.getText().toString());
            IntTypeValue status = new IntTypeValue();
            int ret = accessoryManager.controlPort(port, status);
            if (ret == ErrCode.ERR_SUCCESS) {
                logMsg("controlPort success\n");
                logMsg("controlPort success  " + "status = " + status.getData() + "\n");
            } else {
                logMsg("controlPort fail: " + ret + " " + ErrCode.toString(ret) + "\n");
            }
        } catch (Exception e) {
            logMsg("controlPort fail: Exception\n");

        }
    }
    private void wbsGpioOn() {
        try {
            int port = Integer.parseInt(etPort.getText().toString());
            int ret = accessoryManager.controlPortSwitch(0, port);
            if (ret == ErrCode.ERR_SUCCESS) {
                logMsg("wbsGpioOn success\n");
            } else {
                logMsg("wbsGpioOn fail: " + ret + " " + ErrCode.toString(ret) + "\n");
            }
        } catch (Exception e) {
            logMsg("wbsGpioOn fail: Exception\n");

        }
    }
    private void wbsGpioOff() {
        try {
            int port = Integer.parseInt(etPort.getText().toString());
            int ret = accessoryManager.controlPortSwitch(1, port);
            if (ret == ErrCode.ERR_SUCCESS) {
                logMsg("wbsGpioOff success\n");
            } else {
                logMsg("wbsGpioOff fail: " + ret + " " + ErrCode.toString(ret) + "\n");
            }
        } catch (Exception e) {
            logMsg("wbsGpioOff fail: Exception\n");

        }
    }
}
