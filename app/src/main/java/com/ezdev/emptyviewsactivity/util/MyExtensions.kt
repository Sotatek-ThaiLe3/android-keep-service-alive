package com.ezdev.emptyviewsactivity.util

import android.util.Log

fun Any.log(message: String) {
    Log.d(this::class.java.simpleName, "########## $message")
}