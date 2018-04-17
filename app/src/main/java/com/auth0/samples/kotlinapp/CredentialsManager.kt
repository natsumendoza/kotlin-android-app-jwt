package com.auth0.samples.kotlinapp

import android.content.Context
import com.auth0.android.result.Credentials

/**
 * Created by Roxel on 17/04/2018.
 */
object CredentialsManager {
    private val PREFERENCES_NAME = "auth0"
    private val ACCESS_TOKEN = "access_token"

    fun saveCredentials(context: Context, credentials: Credentials) {
        val sp = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        sp.edit().putString(ACCESS_TOKEN, credentials.accessToken)
                .apply()
    }

    fun getAccessToken(context: Context): String {
        val sp = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        return sp.getString(ACCESS_TOKEN, null)
    }
}