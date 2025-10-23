package com.ftpos.ftappdemo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ftpos.ftappdemo.util.Convection;
import com.ftpos.library.smartpos.dock.Dock;
import com.ftpos.library.smartpos.errcode.ErrCode;
import com.ftpos.library.smartpos.icreader.IcReader;
import com.ftpos.library.smartpos.icreader.OnIcReaderCallback;
import com.ftpos.library.smartpos.util.BytesUtils;


public class DockActivity extends BaseActivity {

    private TextView mShowResultTv;

    Dock dock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dock);

        initView();

        this.dock = MainActivity.dock;
    }

    private void initView() {
        TextView mTitle = findViewById(R.id.ukey_navbar_title);
        mTitle.setText("Dock View");
        findViewById(R.id.ukey_navbar_left_btn).setOnClickListener(this);
        findViewById(R.id.ukey_navbar_right_btn).setOnClickListener(this);
        findViewById(R.id.start_scan_demo).setOnClickListener(this);
        mShowResultTv = findViewById(R.id.function_return_result_tv);

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
            case R.id.start_scan_demo: {
                scanStarDemo();
            }
            break;
            default:
                break;
        }
    }

    public synchronized void logMsg(String msg) {
        runOnUiThread(() -> {
            String oldMsg = mShowResultTv.getText().toString();
            mShowResultTv.setText(String.format("%s %n %s", oldMsg, msg));
        });
    }

    /**
     * Start scanning code
     */
    void scanStarDemo() {
        try {
            ProgressDialog pgd;
            pgd = new ProgressDialog(this);
            pgd.setCancelable(false);
            pgd.setMessage("Please insert the QR...");
            if (!pgd.isShowing()) {
                pgd.show();
            }
            int ret = dock.scanStart(0);
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("scanStart failed" + String.format(" errCode = 0x%x\n", ret));
                pgd.dismiss();
                return;
            }
            int i = 100;
            byte[] recvData = new byte[512];
            int[] len = new int[1];
            while (i > 0) {
                ret = dock.getScanData(0, recvData, len);
                if (ret == ErrCode.ERR_SUCCESS) {
                    logMsg("getScanData success\n");
                    logMsg("ScanData:" + Convection.ByteToString(recvData) + "\n");
                    break;
                }
                Thread.sleep(50);
                i--;
            }
            ret = dock.scanStop(0);
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("scanStop failed" + String.format(" errCode = 0x%x\n", ret));
                pgd.dismiss();
                return;
            }
            logMsg("scanStop success\n");
            pgd.dismiss();
        } catch (Exception e) {
            Log.e("scanStarDemo", e.toString(), e);
        }


    }
}
