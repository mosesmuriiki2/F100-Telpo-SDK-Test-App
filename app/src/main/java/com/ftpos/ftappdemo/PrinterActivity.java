package com.ftpos.ftappdemo;

import static java.lang.Math.ceil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ftpos.library.smartpos.dock.Dock;
import com.ftpos.library.smartpos.dock.OnLoadFondDataListener;
import com.ftpos.library.smartpos.errcode.ErrCode;
import com.ftpos.library.smartpos.printer.AlignStyle;
import com.ftpos.library.smartpos.printer.OnPrinterCallback;
import com.ftpos.library.smartpos.printer.PrintStatus;
import com.ftpos.library.smartpos.printer.Printer;
import com.ftpos.library.smartpos.printer.PrinterColumnInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PrinterActivity extends BaseActivity {

    private TextView mShowResultTv;
    private Printer printer;
    private Dock dock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);

        initView();

        this.printer = MainActivity.printer;
        this.dock = MainActivity.dock;
    }

    private void initView() {
        TextView mTitle = findViewById(R.id.ukey_navbar_title);
        mTitle.setText("Printer View");
        findViewById(R.id.ukey_navbar_left_btn).setOnClickListener(this);
        findViewById(R.id.ukey_navbar_right_btn).setOnClickListener(this);
        findViewById(R.id.print_demo).setOnClickListener(this);
        findViewById(R.id.print_picture_demo).setOnClickListener(this);
        findViewById(R.id.print_qr_code_demo).setOnClickListener(this);
        findViewById(R.id.set_font_demo).setOnClickListener(this);
        findViewById(R.id.print_customer_func).setOnClickListener(this);
        findViewById(R.id.print_load_font_data).setOnClickListener(this);
        findViewById(R.id.print_show_text).setOnClickListener(this);
        findViewById(R.id.print_show_qr).setOnClickListener(this);
        findViewById(R.id.print_set_logo_picture).setOnClickListener(this);
        findViewById(R.id.print_show_adjust).setOnClickListener(this);

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
            case R.id.print_demo: {
                printReceipt();
            }
            break;
            case R.id.print_picture_demo: {
                printPicture();
            }
            break;
            case R.id.print_qr_code_demo: {
                printQRCode();
            }
            break;
            case R.id.set_font_demo: {
                printSetFont();
            }
            break;
            case R.id.print_customer_func: {
                printCustomerFunc();
            }
            break;
            case R.id.print_load_font_data: {
                for (int i = 0; i < 3; i++) {
                    loadFontData(i);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case R.id.print_set_logo_picture: {
                setLogoPicture();
            }
            break;
            case R.id.print_show_qr: {
                showQR();
            }
            break;
            case R.id.print_show_text: {
                showText();
            }
            case R.id.print_show_adjust: {
                showAdjust();
            }
            break;
            default:
                break;
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

    /**
     * Print receipt demo, including gray setting, printing text, style setting, gray setting.
     * <p>
     * Function to demonstrate the call system interface and custom bitmap two ways to achieve
     * the function of printing receipts.
     * The advantage of calling the system interface is that it is easy to call without worrying
     * about the details of typesetting.
     * The advantage of the custom bitmap approach is that the display interface is completely
     * controlled by the user, with higher freedom.  If the receipt function is simple, the
     * first method is recommended to invoke the system interface. If the receipt content is
     * complex and the customized content is large, the second method is recommended.
     */
    void printReceipt() {
        try {

            int ret;
            ret = printer.open();
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("open failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            ret = printer.startCaching();
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("startCaching failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            ret = printer.setGray(3);
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("startCaching failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            PrintStatus printStatus = new PrintStatus();
            ret = printer.getStatus(printStatus);
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("getStatus failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            logMsg("Temperature = " + printStatus.getmTemperature() + "\n");
            logMsg("Gray = " + printStatus.getmGray() + "\n");
            if (!printStatus.getmIsHavePaper()) {
                logMsg("Printer out of paper\n");
                return;
            }

            logMsg("IsHavePaper = true\n");

            printer.setAlignStyle(AlignStyle.PRINT_STYLE_CENTER);
            printer.printStr("MICHAEL KORS\n");

            printer.setAlignStyle(AlignStyle.PRINT_STYLE_LEFT);
            printer.printStr("Please retain this receipt\n");
            printer.printStr("for your exchange.\n");
            printer.printStr("this gift was thoughtfully purchased\n");
            printer.printStr("for you at Michael Kors Chinook Centre.\n");

            ret = printer.getUsedPaperLenManage();
            if (ret < 0) {
                logMsg("getUsedPaperLenManage failed" + String.format(" errCode = 0x%x\n", ret));
            }

            logMsg("UsedPaperLenManage = " + ret + "mm \n");

            Bitmap bitmap = Bitmap.createBitmap(384, 400, Bitmap.Config.RGB_565);

            int k_CurX = 0;
            int k_CurY = 0;
            int k_TextSize = 24;
            paint = new Paint();
            paint.setTextSize(k_TextSize);
            paint.setColor(Color.BLACK);
            Canvas canvas = new Canvas(bitmap);
            bitmap.eraseColor(Color.parseColor("#FFFFFF"));

            Paint.FontMetrics fm = paint.getFontMetrics();
            int k_LineHeight = (int) ceil(fm.descent - fm.ascent);
            String displayStr = "MICHAEL KORS";
            int lineWidth = getTextWidth(displayStr);
            k_CurX = (384 - lineWidth) / 2;
            canvas.drawText(displayStr, k_CurX, k_CurY + k_TextSize, paint);
            k_CurY += k_LineHeight + 5;
            displayStr = "Please retain this receipt";
            k_CurX = 0;
            canvas.drawText(displayStr, k_CurX, k_CurY + k_TextSize, paint);
            k_CurY += k_LineHeight;
            displayStr = "for your exchange.";
            canvas.drawText(displayStr, k_CurX, k_CurY + k_TextSize, paint);
            k_CurY += k_LineHeight;

            displayStr = "this gift was thoughtfully purchased";
            canvas.drawText(displayStr, k_CurX, k_CurY + k_TextSize, paint);
            k_CurY += k_LineHeight;

            displayStr = "for you at Michael Kors Chinook ";
            canvas.drawText(displayStr, k_CurX, k_CurY + k_TextSize, paint);
            k_CurY += k_LineHeight;

            displayStr = "Centre.";
            canvas.drawText(displayStr, k_CurX, k_CurY + k_TextSize, paint);
            k_CurY += k_LineHeight;


            Bitmap newbitmap = Bitmap.createBitmap(bitmap, 0, 0, 384, k_CurY);

            ret = printer.printBmp(newbitmap);
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("printBmp failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            if (!bitmap.isRecycled()) {
                Bitmap mFreeBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
                canvas.setBitmap(mFreeBitmap);
                canvas = null;
                // canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                bitmap.recycle();
                bitmap = null;
                paint.setTypeface(null);
                paint = null;
            }
            if (newbitmap != null && !newbitmap.isRecycled()) {
                newbitmap.recycle();
                newbitmap = null;
            }
            printBlankLine();

            printer.print(new OnPrinterCallback() {
                @Override
                public void onSuccess() {
                    printer.feed(32);
                    logMsg("print success\n");
                }

                @Override
                public void onError(int i) {
                    logMsg("printBmp failed" + String.format(" errCode = 0x%x\n", i));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            logMsg("print failed" + e.toString() + "\n");
        }
    }

    private static int getTextWidth(String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) ceil(widths[j]);
            }
        }

        return iRet;
    }

    private static Paint paint = null;


    /**
     * Example of a call to print a BMP picture
     */
    void printPicture() {
        try {
            int ret;

            ret = printer.open();
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("open failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            ret = printer.startCaching();
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("startCaching failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            ret = printer.setGray(3);
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("startCaching failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            printer.setAlignStyle(AlignStyle.PRINT_STYLE_CENTER);
            Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.mipmap.feitian);
            ret = printer.printBmp(bmp);
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("printBmp failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            printBlankLine();

            printer.print(new OnPrinterCallback() {
                @Override
                public void onSuccess() {
                    printer.feed(32);
                    logMsg("print success\n");
                }

                @Override
                public void onError(int i) {
                    logMsg("printBmp failed" + String.format(" errCode = 0x%x\n", i));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            logMsg("print failed" + e.toString() + "\n");
        }
    }

    /**
     * Print the barcode.
     */
    void printQRCode() {
        try {

            int ret;
            ret = printer.open();
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("open failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            ret = printer.startCaching();
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("startCaching failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            ret = printer.setGray(3);
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("startCaching failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            printer.setAlignStyle(AlignStyle.PRINT_STYLE_CENTER);
            String QRCode = "Content:1234567890";
            Bundle bundle = new Bundle();

            bundle.putInt("mode", 0);
            bundle.putInt("height", 300);

            ret = printer.printQRCodeEx(QRCode.getBytes(), bundle);
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("printQRCode failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            bundle.putInt("mode", 1);
            bundle.putInt("height", 100);
            bundle.putInt("width", 363);
            ret = printer.printQRCodeEx(QRCode.getBytes(), bundle);
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("printQRCode failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }
            printBlankLine();
            printer.print(new OnPrinterCallback() {
                @Override
                public void onSuccess() {
                    printer.feed(32);
                    logMsg("print success\n");
                }

                @Override
                public void onError(int i) {
                    logMsg("printBmp failed" + String.format(" errCode = 0x%x\n", i));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            logMsg("print failed" + e.toString() + "\n");
        }
    }

    /**
     * Set the print font, print style and font size.
     * The function will demonstrate three ways to set the font:
     * 1. Set the default font.
     * 2. Set the font for the system font library.
     * 3. Set a customized font.
     */
    void printSetFont() {

        int ret;
        ret = printer.open();
        if (ret != ErrCode.ERR_SUCCESS) {
            logMsg("open failed" + String.format(" errCode = 0x%x\n", ret));
            return;
        }

        ret = printer.startCaching();
        if (ret != ErrCode.ERR_SUCCESS) {
            logMsg("startCaching failed" + String.format(" errCode = 0x%x\n", ret));
            return;
        }


        // 1.Set the default font.
        Bundle bundle1 = new Bundle();
        bundle1.putString("font", "DEFAULT");
        bundle1.putInt("format", Typeface.NORMAL);
        bundle1.putInt("style", 0);
        bundle1.putInt("size", 32);
        ret = printer.setFont(bundle1);
        if (ret != ErrCode.ERR_SUCCESS) {
            logMsg("Set default font fail " + String.format(" errCode = 0x%x\n", ret));
            return;
        }

        printer.printStr("DEFAULT\n");
        printer.printStr("Please retain this receipt\n");
        printer.printStr("for your exchange.\n");
        printer.printStr("this gift was thoughtfully purchased\n");
        printer.printStr("for you at Michael Kors Chinook Centre.\n");


        // 2.Set the font for the system font library.
        Bundle bundle2 = new Bundle();
        bundle2.putString("systemFont", "DroidSans-Bold.ttf");
        bundle2.putInt("format", Typeface.NORMAL);
        bundle2.putInt("style", 0);
        bundle2.putInt("size", 32);
        ret = printer.setFont(bundle2);
        if (ret != ErrCode.ERR_SUCCESS) {
            logMsg("Set system font fail " + String.format(" errCode = 0x%x\n", ret));
            return;
        }

        printer.printStr("DroidSans-Bold.ttf\n");
        printer.printStr("Please retain this receipt\n");
        printer.printStr("for your exchange.\n");
        printer.printStr("this gift was thoughtfully purchased\n");
        printer.printStr("for you at Michael Kors Chinook Centre.\n");
        // 3.Set a customized font.
        Bundle bundle3 = new Bundle();

        InputStream ttfFile = null;
        try {
            ttfFile = this.getAssets().open("stsong.ttf");
            String Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/stsong.ttf";
            Log.e("Path", "Path = " + Path);
            writeToLocal(Path, ttfFile);

            bundle3.putString("path", Path);
            bundle3.putInt("style", 0);
            bundle3.putInt("size", 32);
            ret = printer.setFont(bundle3);
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("Set font fail " + String.format(" errCode = 0x%x\n", ret));
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        printer.printStr("RAGE.TTF\n");
        printer.printStr("Please retain this receipt\n");
        printer.printStr("for your exchange.\n");
        printer.printStr("this gift was thoughtfully purchased\n");
        printer.printStr("for you at Michael Kors Chinook Centre.\n");
        printBlankLine();
        printer.print(new OnPrinterCallback() {
            @Override
            public void onSuccess() {
                printer.feed(32);
                logMsg("print success\n");
            }

            @Override
            public void onError(int i) {
                logMsg("printBmp failed" + String.format(" errCode = 0x%x\n", i));
            }
        });

    }

    //    void printSetFont() {
//        int ret;
//        ret = printer.open();
//        if (ret != ErrCode.ERR_SUCCESS) {
//            logMsg("open failed" + String.format(" errCode = 0x%x\n", ret));
//            return;
//        }
//
//        ret = printer.startCaching();
//        if (ret != ErrCode.ERR_SUCCESS) {
//            logMsg("startCaching failed" + String.format(" errCode = 0x%x\n", ret));
//            return;
//        }
//        Bundle bundle3 = new Bundle();
//
//        InputStream ttfFile = null;
//        try {
//            ttfFile = this.getAssets().open("OpenSans-SemiBold.ttf");
//            String Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OpenSans-SemiBold.ttf";
//            Log.e("Path", "Path = " + Path);
//            writeToLocal(Path, ttfFile);
//
//            bundle3.putString("path", Path);
//            bundle3.putInt("style", 0);
//            bundle3.putInt("size", 24);
//            ret = printer.setFont(bundle3);
//            if (ret != ErrCode.ERR_SUCCESS) {
//                logMsg("Set font fail " + String.format(" errCode = 0x%x\n", ret));
//                return;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        printer.setGray(1);
//        printer.setAlignStyle(AlignStyle.PRINT_STYLE_LEFT);
//        printer.printMultiStringInLine(getPrinterColumnInfo("TanggalTanggalTanggal   ", ":", "08/01/2024 08/01/2024"));
//        printer.printMultiStringInLine(getPrinterColumnInfo("TransaksiTransaksiTransaksi ", " ", "14:59 WIB"));
//        printer.printMultiStringInLine(getPrinterColumnInfo("Nama Agen ", ":", "Alifa Al-Farizi"));
//        printer.printMultiStringInLine(getPrinterColumnInfo("ID        ", ":", "100357170470071003571704700710035717047007"));
//        printer.print(new OnPrinterCallback() {
//            @Override
//            public void onSuccess() {
//                printer.feed(32);
//                logMsg("print success\n");
//            }
//
//            @Override
//            public void onError(int i) {
//                logMsg("printBmp failed" + String.format(" errCode = 0x%x\n", i));
//            }
//        });
//
//    }
    private List<PrinterColumnInfo> getPrinterColumnInfo(String str, String str1, String str2) {
        List<PrinterColumnInfo> list = new ArrayList<>();
        PrinterColumnInfo printerColumnInfo = new PrinterColumnInfo();
        printerColumnInfo.setColumnWidth(140);
        printerColumnInfo.setAlignMode(AlignStyle.PRINT_STYLE_LEFT);
        printerColumnInfo.setText(str);
        list.add(printerColumnInfo);
        PrinterColumnInfo printerColumnInfo1 = new PrinterColumnInfo();
        printerColumnInfo1.setColumnWidth(30);
        printerColumnInfo1.setAlignMode(AlignStyle.PRINT_STYLE_CENTER);
        printerColumnInfo1.setText(str1);
        list.add(printerColumnInfo1);
        PrinterColumnInfo printerColumnInfo2 = new PrinterColumnInfo();
        printerColumnInfo2.setColumnWidth(194);
        printerColumnInfo2.setAlignMode(AlignStyle.PRINT_STYLE_LEFT);
        printerColumnInfo2.setText(str2);
        list.add(printerColumnInfo2);

        return list;
    }

    /**
     * Demonstration of functions requested by the customer
     * 1.Print Arabic.
     * 2.Single line print left justified, right justified.
     */
    void printCustomerFunc() {
        try {

            int ret;
            ret = printer.open();
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("open failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            ret = printer.startCaching();
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("startCaching failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            ret = printer.setGray(3);
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("startCaching failed" + String.format(" errCode = 0x%x\n", ret));
                return;
            }

            //Print Arabic
            printer.setAlignStyle(AlignStyle.PRINT_STYLE_RIGHT);
            printer.printStr("هذا برنامج اختبار طباعة باللغة العربية");
            printer.printStr("\n\n");

            //Single line print left justified, right justified
            printer.setAlignStyle(AlignStyle.PRINT_STYLE_LEFT);
            printer.printStr("LEFT");

            printer.setAlignStyle(AlignStyle.PRINT_STYLE_RIGHT);
            printer.printStr("RIGHT");

            printer.setAlignStyle(AlignStyle.PRINT_STYLE_CENTER);
            printer.printStr("CENTER \n\n");
            printBlankLine(4);
            printer.print(new OnPrinterCallback() {
                @Override
                public void onSuccess() {
                    printer.feed(32);
                    logMsg("print success\n");
                }

                @Override
                public void onError(int i) {
                    logMsg("printBmp failed" + String.format(" errCode = 0x%x\n", i));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            logMsg("print failed" + e.toString() + "\n");
        }
    }

    private void printBlankLine() {
        printBlankLine(3);
    }

    private void printBlankLine(int num) {
        // fixed<0283358>
        if ("F360".equals(Build.MODEL)) {
            for (int i = 0; i < num; i++) {
                printer.printStr("\n");
            }
        }
    }

    private static void writeToLocal(String destination, InputStream input)
            throws IOException {

        byte[] bytes = new byte[input.available()];

        RandomAccessFile randomFile = null;
        input.read(bytes);
        try {
            randomFile = new RandomAccessFile(destination, "rw");
            randomFile.seek(0);
            randomFile.write(bytes);
            randomFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int level = 0;

    private void showAdjust() {
        int code = dock.showAdjust(level);
        if (code == ErrCode.ERR_SUCCESS) {
            logMsg("print showAdjust level = " + level + "\n");
        } else {
            logMsg("print showAdjust Error = " + code + "\n");
        }
        level++;
        if (level > 10) {
            level = 0;
        }
    }

    private void showQR() {
        dock.showQR("123456");
    }

    private void showText() {
        printer.showLineText(1, "1234", AlignStyle.PRINT_STYLE_CENTER);
        printer.showLineText(5, "12345", AlignStyle.PRINT_STYLE_CENTER);
        printer.showLineText(5, "55555", AlignStyle.PRINT_STYLE_CENTER);
        printer.showLineText(1, "", AlignStyle.PRINT_STYLE_CENTER);

    }

    private void loadFontData(int i) {
        Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.mipmap.aaaa);
        if (i == 1) {
            bmp = BitmapFactory.decodeResource(this.getResources(), R.mipmap.aaaaaa);
        } else if (i == 2) {
            bmp = BitmapFactory.decodeResource(this.getResources(), R.mipmap.feitian2);

        }

        if (bmp != null) {
            dock.showPicturePosition(0, 0, bmp, new OnLoadFondDataListener() {
                @Override
                public void onSuccess() {
                    logMsg("print showPicturePosition onSuccess\n");
                }

                @Override
                public void onError(int i) {
                    logMsg("print showPicturePosition" + i + "\n");

                }
            });
        }
    }

    private void setLogoPicture() {
        Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.mipmap.a);
        if (bmp != null) {
            dock.setLogoPicture(0, 0, bmp, new OnLoadFondDataListener() {
                @Override
                public void onSuccess() {
                    logMsg("print setLogoPicture onSuccess\n");
                }

                @Override
                public void onError(int i) {
                    logMsg("print setLogoPicture" + i + "\n");

                }
            });
        }
    }

    public byte[] bitmapToByteBuffer(Bitmap bitmap) {
        Bitmap rgb565Bitmap = bitmap.copy(Bitmap.Config.RGB_565, false);
        return bitmapToBytesUsingByteBuffer(rgb565Bitmap);
    }

    public byte[] bitmapToBytesUsingByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(byteBuffer);
        return byteBuffer.array();
    }
}
