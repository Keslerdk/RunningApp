package com.example.runningapp.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentTrackingBinding
import com.example.runningapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningapp.other.Constants.MAP_ZOOM
import com.example.runningapp.other.Constants.POLYLINE_WIDTH
import com.example.runningapp.other.TrackingUtility
import com.example.runningapp.services.Polyline
import com.example.runningapp.services.TrackingServices
import com.example.runningapp.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint

private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"

@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    private var map: GoogleMap? = null

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var curTimeInMillis = 0L

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)

        binding.fragment = this
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }

        binding.googleMap.onCreate(mapViewBundle)
        binding.googleMap.getMapAsync {
            map = it
            addAllPolyline()
        }

        subscribeToObservers()

    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() and (pathPoints.last().size > 1)) {
            val preLastLatLong = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLong = pathPoints.last().last()

            val polylineOptions =
                PolylineOptions()
                    .color(Color.RED)
                    .width(POLYLINE_WIDTH)
                    .add(preLastLatLong)
                    .add(lastLatLong)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun subscribeToObservers() {
        TrackingServices.isTracking.observe(viewLifecycleOwner, {
            updateTracking(it)
        })

        TrackingServices.pathPoints.observe(viewLifecycleOwner,  {
            this.pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingServices.timeRunInMillis.observe(viewLifecycleOwner, {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(it, true)
            binding.timer.text = formattedTime
        })
    }

    fun toggleRun() {
        if (!isTracking) sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        else sendCommandToService(ACTION_PAUSE_SERVICE)
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (isTracking) {
            binding.startButton.text = getString(R.string.stop)
            binding.finishButton.visibility = View.GONE
        } else {
            binding.startButton.text = getString(R.string.start)
            binding.finishButton.visibility = View.VISIBLE
        }
    }

    /**
     * always setting camera to current position
     */
    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() and pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM,
                )
            )
        }
    }

    private fun addAllPolyline() {
        for (polyline in pathPoints) {
            val polylineOptions =
                PolylineOptions()
                    .color(Color.RED)
                    .width(POLYLINE_WIDTH)
                    .addAll(polyline)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingServices::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onStart() {
        super.onStart()
        binding.googleMap.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.googleMap.onResume()
    }

    override fun onPause() {
        binding.googleMap.onPause()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.googleMap.onStop()
    }


    override fun onDestroyView() {
        binding.googleMap.onDestroy()
        super.onDestroyView()
        _binding = null
    }
}