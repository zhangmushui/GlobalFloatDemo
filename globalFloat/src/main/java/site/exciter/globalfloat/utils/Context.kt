package site.exciter.globalfloat.utils

import android.annotation.SuppressLint
import android.app.Application
import kotlin.Exception

@SuppressLint("PrivateApi")
object Context {

    private var INSTANCE: Application? = null

    init {
        var application: Application? = null
        try {
            application = Class.forName("android.app.AppGlobals")
                .getMethod("getInitialApplication").invoke(null) as Application
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                application = Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication").invoke(null) as Application
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } finally {
            INSTANCE = application
        }
    }

    fun get(): Application? {
        return INSTANCE
    }

}