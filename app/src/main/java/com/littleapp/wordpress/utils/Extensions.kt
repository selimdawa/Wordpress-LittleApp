package com.littleapp.wordpress.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.preference.PreferenceManager
import com.littleapp.wordpress.R

fun Context.launchActivity(c: Class<*>?) {
    val intent = Intent(this, c)
    this.startActivity(intent)
}

fun Context.applyAppTheme() {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
    if (sharedPreferences.getString("color_option", "BASIC") == "BASIC") {
        this.setTheme(R.style.Base_Theme_MainApp)
    }
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
}