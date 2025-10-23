package com.ftpos.ftappdemo;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ftpos.library.smartpos.buzzer.Buzzer;
import com.ftpos.library.smartpos.dock.Dock;

/**
 * 演示 F360_3 or F360_4 底座显示
 */
public class F360BaseActivity extends AppCompatActivity {
    private static final String TAG = F360BaseActivity.class.getSimpleName();

    private Dock dock;
    private Buzzer buzzer;

    private EditText etContentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f360_base);

        this.dock = MainActivity.dock;
        this.buzzer = MainActivity.buzzer;
        initView();
    }

    private void initView() {
        // title set
        Button mBack = findViewById(R.id.ukey_navbar_left_btn);
        mBack.setOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.ukey_navbar_title)).setText("Display");

        EditText content = findViewById(R.id.base_input_content);

        findViewById(R.id.base_show_qr).setOnClickListener(v -> dock.showQR(content.getText().toString()));
        findViewById(R.id.base_show_paying).setOnClickListener(v -> {
            String text = content.getText().toString();
            if (isNumeric(text)) {
                dock.showPaying(content.getText().toString());
            } else {
                showToast("Please enter the correct amount");
            }
        });
        findViewById(R.id.base_show_success).setOnClickListener(v -> {
            String text = content.getText().toString();
            if (isNumeric(text)) {
                dock.showPaySuc(content.getText().toString());
            } else {
                showToast("Please enter the correct amount");
            }
        });
        findViewById(R.id.base_show_logo).setOnClickListener(v -> dock.showLogo());
        findViewById(R.id.base_adjust_backlight).setOnClickListener(v -> {
            String text = content.getText().toString();
            if (isNumeric(text) && text.length() <= 2
                    && Integer.parseInt(text) <= 10 && Integer.parseInt(text) >= 1) {
                dock.showAdjust(Integer.parseInt(text));
            } else {
                showToast("Please enter the correct num(1-10)");
            }
        });

        etContentIndex = findViewById(R.id.base_voice_input_index);
        findViewById(R.id.base_voice_amount).setOnClickListener(v -> playAmount());
        findViewById(R.id.base_voice_content).setOnClickListener(v -> playContent());
    }

    private boolean isNumeric(String str) {
        return str.matches("\\d+");  // 整数
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void playAmount() {
        int i = buzzer.beepAmount(2, 12345, 2);
        if (i == 0) {
            showToast("Success");
        } else {
            showToast("Failed");
        }
    }

    private void playContent() {
        int ret = buzzer.beepContent(2, 2, "Dollar".getBytes());
        if (ret == 0) {
            showToast("Success");
        } else {
            showToast("Failed");
        }
    }
}
