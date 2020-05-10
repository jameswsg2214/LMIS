package com.hmisdoctor.ui.emr_workflow.diet.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository

class DietViewModel(application: Application) : AndroidViewModel(application){

    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null

    init {
        userDetailsRoomRepository = UserDetailsRoomRepository(application)
    }


}