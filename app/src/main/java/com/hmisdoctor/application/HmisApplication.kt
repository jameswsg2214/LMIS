package com.hmisdoctor.application

import android.R
import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.google.firebase.analytics.FirebaseAnalytics
import com.hmisdoctor.api_service.APIService



class HmisApplication : Application() {
    private var mRetrofitService: APIService? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate() {
        super.onCreate()

        // Obtain the FirebaseAnalytics instance.
        // need to remove in production
        Stetho.initializeWithDefaults(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

    }
    fun getRetrofitService(): APIService? {
        if (mRetrofitService == null) {
            mRetrofitService = APIService.Factory.create(applicationContext)
        }
        return mRetrofitService
    }
    companion object {
         fun get(context: Context): HmisApplication {
            return context.applicationContext as HmisApplication
        }
    }
}