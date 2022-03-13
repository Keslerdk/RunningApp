package com.example.runningapp.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.runningapp.databinding.FragmentSeetingsBinding
import com.example.runningapp.other.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSeetingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    @set:Inject
    var name = ""

    @set:Inject
    var weight = 56F

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeetingsBinding.inflate(inflater, container, false)

        binding.apply {
            fragment = this@SettingsFragment
            lifecycleOwner = this@SettingsFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCurrentPersonalDataToFields()

    }

    fun applyChanges() {
        val success = updatePersonalDataSharedPrefs()
        if (success) Snackbar.make(requireView(), "Changes applied!", Snackbar.LENGTH_SHORT).show()
    }

    private fun setCurrentPersonalDataToFields() {
        binding.inputName.editText?.setText(name)
        binding.inputWeight.editText?.setText(weight.toString())
    }

    private fun updatePersonalDataSharedPrefs(): Boolean {
        val name = binding.inputName.editText?.text.toString()
        val weight = binding.inputName.editText?.text.toString()

        if (name.isEmpty() or weight.isEmpty()) return false

        sharedPrefs.edit()
            .putString(Constants.KEY_NAME, name)
            .putFloat(Constants.KEY_WEIGHT, weight.toFloatOrNull() ?: 56F)
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
            .apply()

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}