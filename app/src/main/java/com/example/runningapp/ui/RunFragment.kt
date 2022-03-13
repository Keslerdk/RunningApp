package com.example.runningapp.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.adapters.RunsRecyclerViewAdapter
import com.example.runningapp.databinding.FragmentRunBinding
import com.example.runningapp.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.runningapp.other.SortType
import com.example.runningapp.other.TrackingUtility
import com.example.runningapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

@AndroidEntryPoint
class RunFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentRunBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRunBinding.inflate(inflater, container, false)

        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()

        setUpRecyclerView()

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> viewModel.sortRuns(SortType.DATA)
                    1 -> viewModel.sortRuns(SortType.AVG_SPEED)
                    2 -> viewModel.sortRuns(SortType.DISTANCE)
                    3 -> viewModel.sortRuns(SortType.TIME)
                    4 -> viewModel.sortRuns(SortType.BURNED_CALORIES)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

    }

    fun navigateToTracker() {
        findNavController().navigate(R.id.trackingFragment)
    }

    private fun setUpRecyclerView() {
        val adapter = RunsRecyclerViewAdapter { onRunCardClick() }
        binding.runList.adapter = adapter

        viewModel.runs.observe(this, {
            adapter.submitData(it)
        })
    }

    fun setTypeSelection() =
        when (viewModel.sortType) {
            SortType.DATA -> 0
            SortType.AVG_SPEED -> 1
            SortType.DISTANCE -> 2
            SortType.TIME -> 3
            SortType.BURNED_CALORIES -> 4
        }

    private fun onRunCardClick() {
        Timber.d("card clicked")
        findNavController().navigate(R.id.runDetailsFragment)
    }

    private fun requestPermissions() {
        if (!TrackingUtility.hasLocationPermission(requireContext())) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                EasyPermissions.requestPermissions(
                    this@RunFragment,
                    resources.getString(R.string.permission_request),
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } else {
                EasyPermissions.requestPermissions(
                    this@RunFragment,
                    resources.getString(R.string.permission_request),
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this@RunFragment, perms)) {
            AppSettingsDialog.Builder(this)
                .build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}