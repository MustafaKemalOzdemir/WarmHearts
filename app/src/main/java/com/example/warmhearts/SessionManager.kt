package com.example.warmhearts

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor


private const val sharedPDbKey = "SharedPreferenceKey"
class SessionManager() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor : Editor

    constructor(context: Context) : this() {
        sharedPreferences = context.getSharedPreferences(sharedPDbKey, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.apply()
    }

    fun setLoginData(email: String?, password: String?) {
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    fun getEmail(): String {
        sharedPreferences.getString("email", "")?.let {
            return  it
        }
        return ""
    }

    fun getPassword(): String {
        sharedPreferences.getString("password", "")?.let {
            return  it
        }
        return ""
    }

    fun logout() {
        editor.clear()
        editor.apply()
    }

}