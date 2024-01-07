package com.nasa.customizablesurveyapp.data.model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class DataMap(
    @SerializedName("options")
    val options: List<String>
) : Parcelable