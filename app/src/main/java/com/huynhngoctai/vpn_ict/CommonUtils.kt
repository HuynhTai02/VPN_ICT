package com.huynhngoctai.vpn_ict

import android.content.Context
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.net.ntp.NTPUDPClient
import java.net.InetAddress
import java.util.Locale

object CommonUtils {

    //Save local Storage Use SharedPreferences
    private const val PREF_FILE = "pref_saving"

    fun savePref(key: String?, value: Boolean) {
        val pref: SharedPreferences =
            App.instance.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        pref.edit().putBoolean(key, value).apply()
    }

    fun savePref(key: String?, value: Int) {
        val pref: SharedPreferences =
            App.instance.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        pref.edit().putInt(key, value).apply()
    }

    fun savePref(key: String?, value: String) {
        val pref: SharedPreferences =
            App.instance.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        pref.edit().putString(key, value).apply()
    }

    fun getPrefBoolean(key: String?): Boolean {
        val pref: SharedPreferences =
            App.instance.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        return pref.getBoolean(key, false)
    }

    fun getPrefInt(key: String?): Int {
        val pref: SharedPreferences =
            App.instance.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        return pref.getInt(key, 0)
    }

    fun getPrefString(key: String?): String? {
        val pref: SharedPreferences =
            App.instance.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        return pref.getString(key, null)
    }

    fun clearPref(key: String?) {
        val pref: SharedPreferences =
            App.instance.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        pref.edit().remove(key).apply()
    }

    //Get time use lib commons-net:commons-net:3.6
    private const val TIME_SERVER = "time-a.nist.gov"
    private const val DATE_PATTERN = "yyyy-MM-dd"

    suspend fun getRealDay(): String = withContext(Dispatchers.IO) {
        val timeClient = NTPUDPClient()
        val inetAddress: InetAddress = InetAddress.getByName(TIME_SERVER)
        val timeInfo = timeClient.getTime(inetAddress)
        val returnTime = timeInfo.message.receiveTimeStamp.date
        val t = SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH)
        t.format(returnTime)
    }
}