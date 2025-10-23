package com.ftpos.ftappdemo;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.ftpos.library.smartpos.errcode.ErrCode;
import com.ftpos.library.smartpos.nfcreader.NfcReader;

import static com.ftpos.ftappdemo.util.Convection.hexString2Bytes;


public class NfcSimulationCardActivity extends BaseActivity {

    private final String TAG = NfcSimulationCardActivity.class.getSimpleName();

    private TextView mShowResultTv;
    private EditText etAtqa;
    private EditText etUidData;
    private EditText etSak;
    private EditText etBatsData;
    private EditText etMemorySize;
    private EditText etEmoryOffset;
    private EditText etEmoryData;

    private NfcReader nfcReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation_card);

        initView();

        this.nfcReader = MainActivity.nfcReader;
    }

    private void initView() {
        TextView mTitle = findViewById(R.id.ukey_navbar_title);
        mTitle.setText("SimulationCard View");
        findViewById(R.id.ukey_navbar_left_btn).setOnClickListener(this);
        findViewById(R.id.ukey_navbar_right_btn).setOnClickListener(this);
        findViewById(R.id.configCardInfo).setOnClickListener(this);
        findViewById(R.id.updateCardData).setOnClickListener(this);
        findViewById(R.id.simulateCardControlStart).setOnClickListener(this);
        findViewById(R.id.simulateCardControlStop).setOnClickListener(this);

        mShowResultTv = findViewById(R.id.function_return_result_tv);
        etAtqa = findViewById(R.id.etAtqa);
        etUidData = findViewById(R.id.etUidData);
        etSak = findViewById(R.id.etSak);
        etBatsData = findViewById(R.id.etBatsData);
        etMemorySize = findViewById(R.id.etMemorySize);
        etEmoryOffset = findViewById(R.id.etEmoryOffset);
        etEmoryData = findViewById(R.id.etEmoryData);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ukey_navbar_left_btn) {
            finish();
        } else if (id == R.id.ukey_navbar_right_btn) {
            mShowResultTv.setText("");
        } else if (id == R.id.configCardInfo) {
            configCardInfo();
        } else if (id == R.id.updateCardData) {
            updateCardData();
        } else if (id == R.id.simulateCardControlStart) {
            simulateCardControlStart();
        } else if (id == R.id.simulateCardControlStop) {
            simulateCardControlStop();
        }
    }

    public synchronized void logMsg(String msg) {
        runOnUiThread(() -> {
            String oldMsg = mShowResultTv.getText().toString();
            mShowResultTv.setText(oldMsg + "\n" + msg);
        });
    }


    private void configCardInfo() {
        try {
            int ret = nfcReader.configCardInfo(hexString2Bytes(etAtqa.getText().toString()),
                    hexString2Bytes(etUidData.getText().toString()),
                    hexString2Bytes(etSak.getText().toString()),
                    hexString2Bytes(etBatsData.getText().toString()),
                    hexString2Bytes(etMemorySize.getText().toString()));
            Log.e(TAG,"ATPA = " + hexString2Bytes(etAtqa.getText().toString()) + "   UidData = " + hexString2Bytes(etUidData.getText().toString()));
            if (ret == ErrCode.ERR_SUCCESS) {
                logMsg("configCardInfo success\n");
            } else {
                logMsg("configCardInfo fail: " + ret + " " + ErrCode.toString(ret) + "\n");
            }
        } catch (Exception e) {
            logMsg("configCardInfo fail: Exception\n");

        }
    }

    private void updateCardData() {
        try {
            int emoryOffset = Integer.parseInt(etEmoryOffset.getText().toString());
            int ret = nfcReader.updateCardData(emoryOffset, new NdefMessage(NdefRecord.createUri(etEmoryData.getText().toString()), new NdefRecord[0]).toByteArray());
            if (ret == ErrCode.ERR_SUCCESS) {
                logMsg("updateCardData success\n");
            } else {
                logMsg("updateCardData fail: " + ret + " " + ErrCode.toString(ret) + "\n");
            }
        } catch (Exception e) {
            logMsg("updateCardData fail: Exception\n");

        }
    }

    private void simulateCardControlStart() {
        try {
            int ret = nfcReader.simulateCardControlStart();
            if (ret == ErrCode.ERR_SUCCESS) {
                logMsg("simulateCardControlStart success\n");
            } else {
                logMsg("simulateCardControlStart fail: " + ret + " " + ErrCode.toString(ret) + "\n");
            }
        } catch (Exception e) {
            logMsg("simulateCardControlStart fail: Exception\n");

        }
    }

    private void simulateCardControlStop() {
        try {
            int ret = nfcReader.simulateCardControlStop();
            if (ret == ErrCode.ERR_SUCCESS) {
                logMsg("simulateCardControlStop success\n");
            } else {
                logMsg("simulateCardControlStop fail: " + ret + " " + ErrCode.toString(ret) + "\n");
            }
        } catch (Exception e) {
            logMsg("simulateCardControlStop fail: Exception\n");

        }
    }
}
