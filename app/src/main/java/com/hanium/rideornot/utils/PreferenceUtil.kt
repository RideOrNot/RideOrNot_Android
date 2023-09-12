package com.hanium.rideornot.utils

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "mainActivity"

const val FIRST_RUN_KEY = "firstRun"
const val JWT_KEY = "jwt"

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String) {
        prefs.edit().putString(key, str).apply()
    }

    fun getJwt() : String {
        return prefs.getString(JWT_KEY, "").toString()
    }

    fun setJwt(token: String) {
        prefs.edit().putString(JWT_KEY, token).apply()
    }
}