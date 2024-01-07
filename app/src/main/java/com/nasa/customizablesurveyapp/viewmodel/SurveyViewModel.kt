package com.nasa.customizablesurveyapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.nasa.customizablesurveyapp.data.model.SurveyData
import com.nasa.customizablesurveyapp.utils.ReadJSONFromAssets

class SurveyViewModel() : ViewModel() {

    var mutableApiData:  MutableLiveData<SurveyData?>?= MutableLiveData<SurveyData?>()
    var observableApiData:  LiveData<SurveyData?>?


    init {

        observableApiData=mutableApiData
    }



    fun updateApiData(data: SurveyData?) {
        mutableApiData?.value = (data)
    }

    override fun onCleared() {
        super.onCleared()
        mutableApiData?.value =null

    }

    fun callApi(context: Context){

        //this function will call the repository function for api call but as we dont have it here so this function reads the
        //data from the json file
        val jsonString=ReadJSONFromAssets(context,"SurveyData.json")
        val data= Gson().fromJson(jsonString,SurveyData::class.java)

        updateApiData(data)
    }

}