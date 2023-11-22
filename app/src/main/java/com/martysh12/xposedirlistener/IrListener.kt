package com.martysh12.xposedirlistener

import android.hardware.ConsumerIrManager
import android.os.Environment
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.io.FileWriter
import java.io.IOException
import java.util.Arrays
import java.util.stream.Collectors
import kotlin.Int
import kotlin.IntArray

class IrListener : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        XposedBridge.log("Hooking transmit(int, int[])...")

        XposedHelpers.findAndHookMethod(
            ConsumerIrManager::class.java,
            "transmit",
            Int::class.javaPrimitiveType,
            IntArray::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val frequency = param.args[0] as Int
                    val pattern = param.args[1] as IntArray

                    val patternString =
                        Arrays.stream(pattern).mapToObj { i: Int -> i.toString() }
                            .collect(Collectors.joining(","))

                    val line = "${lpparam.packageName} $frequency $patternString\n"
                    
                    try {
                        FileWriter(
                            Environment.getExternalStorageDirectory().path + "/Documents/ir.txt",
                            true
                        ).use { log -> log.append(line) }
                    } catch (e: IOException) {
                        XposedBridge.log(e)
                    }
                }
            })
    }
}