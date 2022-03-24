package com.example.runningapp.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentTrackingBinding
import com.example.runningapp.db.Run
import com.example.runningapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningapp.other.Constants.ACTION_STOP_SERVICE
import com.example.runningapp.other.Constants.MAP_ZOOM
import com.example.runningapp.other.Constants.POLYLINE_WIDTH
import com.example.runningapp.other.TrackingUtility
import com.example.runningapp.services.Polyline
import com.example.runningapp.services.TrackingServices
import com.example.runningapp.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.round

private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"

@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    private var map: GoogleMap? = null
    private lateinit var menu: Menu

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

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        (requireActivity() as AppCompatActivity).setupActionBarWithNavController(findNavController())
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.tracking)



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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (curTimeInMillis > 0L) {
            menu.getItem(0)?.isVisible = true
        }
    }

    private fun showCloseDialog() {
        val dialog = MaterialAlertDialogBuilder(
            requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog
        ).setTitle("Cancel the Run?")
            .setMessage("Are you sure to cancel the current run and delete all its data?")
            .setPositiveButton("Yes") { _, _ ->
                stopRun()
            }.setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }.create()
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.close -> showCloseDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigateUp()
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty()) {
            if ((pathPoints.last().size > 1)) {
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
    }

    private fun subscribeToObservers() {
        TrackingServices.isTracking.observe(viewLifecycleOwner, {
            updateTracking(it)
        })

        TrackingServices.pathPoints.observe(viewLifecycleOwner, {
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
        if (!isTracking) {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        } else {
            menu.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }
    }

    fun finishBtnClick() {
        zoomToSeeWholeTracker()
        endRunAndSaveToDb()
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
        if (pathPoints.isNotEmpty()) {
            if (pathPoints.last().isNotEmpty())
                map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        pathPoints.last().last(),
                        MAP_ZOOM,
                    )
                )
        }
    }

    private fun zoomToSeeWholeTracker() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (pos in polyline) bounds.include(pos)
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.googleMap.width,
                binding.googleMap.height,
                (binding.googleMap.height * 0.05f).toInt(),
            )
        )
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

    private fun endRunAndSaveToDb() {
        map?.snapshot { bmp ->
            var distanceInMiters = 0
            for (polyline in pathPoints) {
                distanceInMiters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed =
                round((distanceInMiters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f

            val dateTimeStamp = Calendar.getInstance().timeInMillis
            // todo set real weight instead of 56
            val caloriesBurned = ((distanceInMiters / 1000f) * 56).toInt()
            val run = Run(
                img = bmp,
                timestamp = dateTimeStamp,
                avgSpeedKIH = avgSpeed,
                distanceInMeters = distanceInMiters,
                timeInMillis = curTimeInMillis,
                caloriesBurned = caloriesBurned,
            )

            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.root_view),
                "Run saved successfully",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
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