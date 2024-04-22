package com.sunueric.tableauapp.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

fun saveCredentials(email: String, password: String, context: Context) {
    val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "SecurePreferences",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    with(sharedPreferences.edit()) {
        putString("email", email)
        putString("password", password)
        apply()
    }
}
