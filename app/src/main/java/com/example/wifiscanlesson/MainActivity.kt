package com.example.wifiscanlesson
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
val TAG = "myTag"
/*
* https://www.youtube.com/watch?v=NebPBFOtsqE
* 配合最簡單的arrayAdapter 不用再創建xml 之simple_list_item_1
* 每次都會問？ 是否開啟wifi (Bug)
 */
class MainActivity : AppCompatActivity() {
    val arrayList = ArrayList<String>()
    lateinit var wifiManager: WifiManager
    var myadapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager.isWifiEnabled == false) {
            Toast.makeText(this, "Wifi Diabled", Toast.LENGTH_SHORT).show()
            wifiManager.isWifiEnabled = true
        }

        myadapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        wifiList.adapter = myadapter
        scanWifi()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0)
            //do something if have the permissions
        } else {
            //do something, permission was previously granted; or legacy device
            scanWifi()
        }

        scanBtn.setOnClickListener {
            scanWifi()
        }


    }  //onCreate


    fun scanWifi() {
        arrayList.clear()
        registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        wifiManager.startScan()
        Toast.makeText(this, "Wifi Scanning", Toast.LENGTH_SHORT).show()
    }

    val wifiReceiver = object : BroadcastReceiver() {

        override fun onReceive(p0: Context?, p1: Intent?) {
            val results = wifiManager.scanResults
            unregisterReceiver(this)
            Log.d(TAG, "onReceive: $results")
            for (scanResult in results!!) {
                var wifi_ssid = ""
                wifi_ssid = scanResult.SSID
                Log.d("WIFIScannerActivity", "WIFI SSID: $wifi_ssid")

                var wifi_ssid_first_nine_characters = ""

                if (wifi_ssid.length > 8) {
                    wifi_ssid_first_nine_characters = wifi_ssid.substring(0, 9)
                } else {
                    wifi_ssid_first_nine_characters = wifi_ssid
                }
                Log.d("WIFIScannerActivity", "WIFI SSID 9: $wifi_ssid_first_nine_characters")

                // Display only WIFI that matched "WIFI_NAME"
//                if (wifi_ssid_first_nine_characters == "WIFI_NAME") {
                Log.d(
                    "WIFIScannerActivity",
                    "scanResult.SSID: " + scanResult.SSID + ", scanResult.capabilities: " + scanResult.capabilities
                )
                myadapter?.add(scanResult.SSID + " - " + scanResult.capabilities)
            }
        }
    }

}  //MainActivity
