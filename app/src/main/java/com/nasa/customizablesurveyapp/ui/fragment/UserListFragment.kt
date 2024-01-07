package com.nasa.customizablesurveyapp.ui.fragment

import android.R.attr
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.nasa.customizablesurveyapp.data.model.SurveyDataItem
import com.nasa.customizablesurveyapp.databinding.FragmentUserListBinding
import com.nasa.customizablesurveyapp.databinding.TypeImageBinding
import com.nasa.customizablesurveyapp.ui.activity.MainActivity
import com.nasa.customizablesurveyapp.ui.adapter.RecyclerViewAdapter
import com.nasa.customizablesurveyapp.viewmodel.SurveyViewModel
import java.io.IOException


/**
 * A fragment representing a list of Items.
 */
class UserListFragment(val surveyDataItem: SurveyDataItem?=null, val viewBinding: ViewBinding?=null) : Fragment() {




    private lateinit var binding: FragmentUserListBinding
    private val surveyViewModel: SurveyViewModel by viewModels();


    constructor() : this(null,null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserListBinding.inflate(layoutInflater, container, false)


        return binding.root
    }


}