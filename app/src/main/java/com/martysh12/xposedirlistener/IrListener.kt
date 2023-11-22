package com.martysh12.xposedirlistener;

import android.hardware.ConsumerIrManager;
import android.os.Environment;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class IrListener implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedBridge.log("Hooking transmit(int, int[])...");

        XposedHelpers.findAndHookMethod(ConsumerIrManager.class, "transmit", int.class, int[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                int frequency = (int) param.args[0];
                int[] pattern = (int[]) param.args[1];

                String line = lpparam.packageName + " " + frequency + " " + Arrays.stream(pattern).mapToObj(String::valueOf).collect(Collectors.joining(",")) + "\n";

                try (FileWriter log = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/Documents/ir.txt", true)) {
                    log.append(line);
                } catch (IOException e) {
                    XposedBridge.log(e);
                }
            }
        });
    }
}
