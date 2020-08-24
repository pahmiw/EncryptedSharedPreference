package com.example.encryptedsharedpreference

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private val PREF_NAME = "mypertamina"
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initEncrypted.setOnCheckedChangeListener { _, checked -> initSharedPreferences(checked) }
        saveButton.setOnClickListener { saveValue() }
        readButton.setOnClickListener { readValue() }

        initEncrypted.isChecked = true
    }

    private fun readValue() {
       val startTs = System.currentTimeMillis()

        val value = sharedPreferences.getString("DATA", "")
        readText.setText(value)

        val endTs = System.currentTimeMillis()
        readTimestamp.visibility = View.VISIBLE
        readTimestamp.text = getString(R.string.timestamp).format(endTs - startTs)

        hideKeyboard()
    }

    private fun saveValue() {
        val startTs = System.currentTimeMillis()
        sharedPreferences.edit()
            .putString("DATA", saveText.text.toString())
            .apply()

        val endTs = System.currentTimeMillis()
        saveTimestamp.visibility = View.VISIBLE
        saveTimestamp.text = getString(R.string.timestamp).format(endTs - startTs)

        hideKeyboard()
        showRawFile()
    }

    private fun showRawFile() {
        val preferencesFile = File("${applicationInfo.dataDir}/shared_prefs/$PREF_NAME.xml")
        if (preferencesFile.exists()) {
            fileText.text = preferencesFile.readText().highlight()
        } else {
            fileText.text = ""
        }
    }

    private fun initSharedPreferences(checked: Boolean) {
        resetSharedPreferences()

        if (checked) {
            initEncryptedSharedPreference()
        } else {
            initClearTextSharedPreference()
        }
    }

    private fun resetSharedPreferences() {
        getSharedPreferences(PREF_NAME, MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }

    private fun initClearTextSharedPreference() {
       val starTs = System.currentTimeMillis()
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        val endTs = System.currentTimeMillis()
        initTimestamp.visibility = View.VISIBLE
        initTimestamp.text = getString(R.string.timestamp).format(endTs - starTs)
    }

    private fun initEncryptedSharedPreference() {
        val startTs = System.currentTimeMillis()

        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        sharedPreferences = EncryptedSharedPreferences.create(
            PREF_NAME,
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val endTs = System.currentTimeMillis()
        initTimestamp.visibility = View.VISIBLE
        initTimestamp.text = getString(R.string.timestamp).format(endTs - startTs)
    }


}