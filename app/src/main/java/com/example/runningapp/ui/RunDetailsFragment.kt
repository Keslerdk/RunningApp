package com.example.runningapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.example.runningapp.databinding.FragmentRunDetailsBinding
import com.example.runningapp.ui.viewmodels.RunDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class RunDetailsFragment : Fragment() {

    private var _binding: FragmentRunDetailsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelAssistedFactory: RunDetailsViewModel.Factory

    private val args: RunDetailsFragmentArgs by navArgs()
    private val viewModel: RunDetailsViewModel by viewModels {
        RunDetailsViewModel.provideFactory(viewModelAssistedFactory, args.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentRunDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).setupActionBarWithNavController(findNavController())


        viewModel.run.observe(this, {
            Glide.with(binding.root).load(it.img).centerCrop().into(binding.runPreview)

            val dateFormat = SimpleDateFormat("dd.MM.yy hh:mm a", Locale.getDefault())
            (requireActivity() as AppCompatActivity).supportActionBar?.title =
                dateFormat.format(it.timestamp)

            val km = it.distanceInMeters / 1000f
            val totalDistance = (km * 100f).roundToInt() / 100f
            binding.distance.text = "$totalDistance km"

            val totalTimeRun = TimeUnit.MILLISECONDS.toHours(it.timeInMillis)
            binding.time.text = "$totalTimeRun h"

            binding.speed.text = "${it.avgSpeedKIH} km/h"

            val kcal = it.caloriesBurned / 1000f
            binding.calories.text = "${(kcal * 100f).roundToInt() / 100f}kcal"

        })
    }

}