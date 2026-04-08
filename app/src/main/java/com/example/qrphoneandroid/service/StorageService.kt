package com.example.qrphoneandroid.service

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.qrphoneandroid.model.UserData

class StorageService(context: Context) {

    private val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        "qrphone_secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveUserData(data: UserData) {
        prefs.edit()
            .putString("firstName", data.firstName)
            .putString("lastName", data.lastName)
            .putString("phoneNumber", data.phoneNumber)
            .putString("email", data.email)
            .apply()
    }

    fun loadUserData(): UserData? {
        val firstName = prefs.getString("firstName", null) ?: return null
        val lastName = prefs.getString("lastName", null) ?: return null
        val phoneNumber = prefs.getString("phoneNumber", null) ?: return null
        val email = prefs.getString("email", null)
        return UserData(firstName, lastName, phoneNumber, email)
    }

    fun deleteUserData() {
        prefs.edit().clear().apply()
    }
}
