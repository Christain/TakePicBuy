package com.unionbigdata.takepicbuy.utils;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.unionbigdata.takepicbuy.TakePicBuyApplication;
import com.unionbigdata.takepicbuy.constant.Constant;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 系统错误处理
 */
public class ExceptionManage implements UncaughtExceptionHandler {
    private UncaughtExceptionHandler mDefaultHandler;
    private static ExceptionManage instance;

    public static void startInstance() {
        if (instance == null) {
            synchronized (ExceptionManage.class) {
                if (instance == null) {
                    instance = new ExceptionManage();
                }
                instance.init();
            }
        }
    }

    public void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else { // 如果自己处理了异常，则不会弹出错误对话框，则需要手动退出app
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AppManage.getInstance().clearEverything(TakePicBuyApplication.getInstance());
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @return true代表处理该异常，不再向上抛异常，
     * false代表不处理该异常(可以将该log信息存储起来)然后交给上层(这里就到了系统的异常处理)去处理，
     * 简单来说就是true不会弹出那个错误提示框，false就会弹出
     */

    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                saveExceptionLog(ex);
                Looper.prepare();
                Toast.makeText(TakePicBuyApplication.getInstance().getApplicationContext(), "运行异常，此问题会尽快修复", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        return true;
    }

    private void saveExceptionLog(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String logInfo = writer.toString();
//        if (PhoneManager.isSdCardExit()) {
//            File dir = new File(Constant.UPLOAD_FILES_DIR_PATH);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            StringBuilder sb = new StringBuilder();
//            String date = System.currentTimeMillis() +"";
//            sb.append("crash_");
//            PackageInfo info = PhoneManager.getVersionInfo();
//            if (info != null) {
//                sb.append("V");
//                sb.append(info.versionName);
//                sb.append("_");
//            }
//            sb.append(date);
//            sb.append(".txt");
//            File logFile = new File(dir, sb.toString());
//            try {
//                FileOutputStream fos = new FileOutputStream(logFile);
//                fos.write(logInfo.getBytes());
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        if (!Constant.SHOW_LOG) {
            return;
        } else {
            Log.e("ERROR_MSG", logInfo);
            logInfo = null;
        }
    }
}
