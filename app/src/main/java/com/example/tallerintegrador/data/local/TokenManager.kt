package com.example.tallerintegrador.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "access_token"
        private const val USER_ID_KEY = "user_id"
        private const val USER_NAME_KEY = "user_name"
        private const val USER_EMAIL_KEY = "user_email"
    }

    fun saveAuthData(token: String, userId: Int, userName: String, userEmail: String) {
        prefs.edit {
            putString(TOKEN_KEY, token)
            putInt(USER_ID_KEY, userId)
            putString(USER_NAME_KEY, userName)
            putString(USER_EMAIL_KEY, userEmail)
        }
    }

    fun getToken(): String? = prefs.getString(TOKEN_KEY, null)

    fun getUserId(): Int = prefs.getInt(USER_ID_KEY, -1)

    fun getUserName(): String? = prefs.getString(USER_NAME_KEY, null)

    fun getUserEmail(): String? = prefs.getString(USER_EMAIL_KEY, null)

    fun isLoggedIn(): Boolean = getToken() != null

    fun clearSession() {
        prefs.edit { clear() }
    }
}
