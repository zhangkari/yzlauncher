package com.yz.books.utils;

import android.app.Instrumentation;

public class Instructions {
    public static void simulateKeystroke(final int KeyCode) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
