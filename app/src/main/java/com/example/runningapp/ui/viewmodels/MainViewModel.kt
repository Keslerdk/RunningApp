package com.example.runningapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.runningapp.repositories.MainRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(val mainRepository: MainRepository) : ViewModel() {
}