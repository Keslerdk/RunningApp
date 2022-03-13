package com.example.runningapp.ui

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentEntryBinding
import com.example.runningapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningapp.other.Constants.KEY_NAME
import com.example.runningapp.other.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EntryFragment : Fragment() {

    private var _binding: FragmentEntryBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    @set:Inject
    var isFirstTime : Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)

        binding.apply {
            fragment = this@EntryFragment
            lifecycleOwner = this@EntryFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isFirstTime) {
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.entryFragment, false).build()
            findNavController().navigate(R.id.runFragment, savedInstanceState, navOptions)
        }
    }


    fun navigateNext() {
        val success = writePersonalDataToSharedPreferences()

        if (success) {
            findNavController().navigate(R.id.runFragment)
        } else {
            Snackbar.make(requireView(), "Enter all fields!", Snackbar.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun writePersonalDataToSharedPreferences(): Boolean {
        val name = binding.inputName.editText?.text.toString()
        val weight = binding.inputName.editText?.text.toString()

        if (name.isEmpty() or weight.isEmpty()) return false

        sharedPrefs.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloatOrNull() ?: 56F)
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()

        return true
    }
}