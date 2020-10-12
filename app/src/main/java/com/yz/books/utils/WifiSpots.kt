package com.yz.books.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.widget.Toast
import com.yz.books.AppApplication.Companion.mContext
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


object WifiSpots {
    private const val TAG = "WifiSpots"

    fun enableWifiSpot(context: Context): Boolean {
        closeWifi(context)
        return createAp(context, "Hello-China", "666888")
    }

    private fun closeWifi(context: Context) {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val status: Int = wifiManager.wifiState
        if (status == WifiManager.WIFI_STATE_ENABLED) {
            //wifi打开状态则关闭
            wifiManager.isWifiEnabled = false
            Toast.makeText(context, "wifi已关闭", Toast.LENGTH_SHORT).show()
        } else {
            //关闭状态则打开
            wifiManager.isWifiEnabled = true
            Toast.makeText(context, "wifi已打开", Toast.LENGTH_SHORT).show()
        }
    }

    fun createAp(context: Context, apName: String, password: String): Boolean {
        //如果wifi已经打开则先关闭wifi
        closeWifi(context)
        //  Method method1=null;
        try {
            val netConfig = WifiConfiguration()
            netConfig.SSID = apName
            netConfig.preSharedKey = password
            Log.d(
                TAG, ("WifiPresenter：createAp----->netConfig.SSID:"
                        + netConfig.SSID) + ",netConfig.preSharedKey:" + netConfig.preSharedKey.toString()
            )
            netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
//            if (isOpen) {
//                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
//            } else {
            netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
//            }
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)

            val mWifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {

                //保存ap设置
                val methodsave: Method = mWifiManager.javaClass.getMethod(
                    "setWifiApConfiguration",
                    WifiConfiguration::class.java
                )
                methodsave.invoke(mWifiManager, netConfig)
                //获取ConnectivityManager对象，便于后面使用
                val connManager: ConnectivityManager =
                    mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val field: Field = connManager.javaClass.getDeclaredField("TETHERING_WIFI")
                field.isAccessible = true
                val mTETHERING_WIFI = field.get(connManager) as Int
                val iConnMgrField: Field = connManager.javaClass.getDeclaredField("mService")
                iConnMgrField.isAccessible = true
                val iConnMgr: Any = iConnMgrField.get(connManager)
                val iConnMgrClass =
                    Class.forName(iConnMgr.javaClass.name)
                val startTethering: Method = iConnMgrClass.getMethod(
                    "startTethering",
                    Int::class.javaPrimitiveType,
                    ResultReceiver::class.java,
                    Boolean::class.javaPrimitiveType
                )
                startTethering.invoke(
                    iConnMgr,
                    mTETHERING_WIFI,
                    object : ResultReceiver(Handler()) {
                        protected override fun onReceiveResult(
                            resultCode: Int,
                            resultData: Bundle?
                        ) {
                            super.onReceiveResult(resultCode, resultData)
                        }
                    },
                    true
                )
                true
            } else {
                val method: Method = mWifiManager.javaClass.getMethod(
                    "setWifiApEnabled",
                    WifiConfiguration::class.java, java.lang.Boolean.TYPE
                )
                method.invoke(mWifiManager, netConfig, true)
                true
            }
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        return false
    }
}