package com.example.runningapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.runningapp.repositories.MainRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


class RunDetailsViewModel @AssistedInject
constructor(private val mainRep: MainRepository, @Assisted private val id: Int) : ViewModel() {
    val run = mainRep.getRun(id)

    @AssistedFactory
    interface Factory {
        fun create(@Assisted id: Int): RunDetailsViewModel
    }

    companion object {
        fun provideFactory(assistedFactory: Factory, id: Int): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return assistedFactory.create(id) as T
                }
            }
    }
}