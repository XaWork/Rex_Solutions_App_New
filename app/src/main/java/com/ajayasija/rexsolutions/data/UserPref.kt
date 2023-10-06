package com.ajayasija.rexsolutions.data

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ajayasija.rexsolutions.domain.model.LoginModel
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class UserPref @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val USER_KEY = stringPreferencesKey("user")
        val USER_LOGIN = booleanPreferencesKey("user_login")
    }


    //--------------------------------------- Share Preferences ------------------------------------
    private val _myPrefName = "sharePrefName"
    fun saveUser(user: LoginModel) {
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(_myPrefName, Context.MODE_PRIVATE).edit()
        editor.putString("user", Gson().toJson(user, LoginModel::class.java))
        Log.d("user", "User saved and user is -> \n$user")
        editor.apply()
    }

    fun getUser(): LoginModel? {
        val prefs = context.getSharedPreferences(_myPrefName, Context.MODE_PRIVATE)
        val userGson = prefs.getString("user", "")
        Log.d("user", "get user is -> \n$userGson")
        return if (TextUtils.isEmpty(userGson))
            null
        else
            Gson().fromJson(userGson, LoginModel::class.java)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(_myPrefName, Context.MODE_PRIVATE).edit()
        editor.putBoolean("login", isLoggedIn)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        val prefs = context.getSharedPreferences(_myPrefName, Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("login", false)
        Log.d("user login", " user login -> \n$isLoggedIn")
        return isLoggedIn
    }

    fun logOut() {
        val editor = context.getSharedPreferences(_myPrefName, Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()
    }

    fun saveToken(token: String) {
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(_myPrefName, Context.MODE_PRIVATE).edit()
        editor.putString("token", token)
        editor.apply()
        getToken()
    }

    fun getToken(): String {
        val prefs = context.getSharedPreferences(_myPrefName, Context.MODE_PRIVATE)
        return prefs.getString("token", "") ?: ""
    }
}