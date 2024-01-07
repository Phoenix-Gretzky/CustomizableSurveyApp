package com.nasa.customizablesurveyapp.data.model


import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class SurveyDataItem(
    @SerializedName("dataMap")
    val dataMap: DataMap,
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    var exists: Bitmap?=null


) : Parcelable