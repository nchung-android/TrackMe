package com.nchungdev.trackme.ui.tracking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.nchungdev.data.db.mapper.toLatLng
import com.nchungdev.data.util.Constant
import com.nchungdev.domain.model.LocationModel
import com.nchungdev.domain.model.SessionModel
import com.nchungdev.domain.util.Result
import com.nchungdev.domain.util.data
import com.nchungdev.trackme.MainApp
import com.nchungdev.trackme.R
import com.nchungdev.trackme.databinding.FragmentTrackingBinding
import com.nchungdev.trackme.service.LocationService
import com.nchungdev.trackme.ui.base.PermissionRequestable
import com.nchungdev.trackme.ui.base.activity.BaseActivity
import com.nchungdev.trackme.ui.base.dialog.BaseDialogFragment
import com.nchungdev.trackme.ui.base.fragment.BaseVBFragment
import com.nchungdev.trackme.ui.helper.*
import com.nchungdev.trackme.ui.util.Constants
import com.nchungdev.trackme.ui.util.PermissionUtils
import kotlinx.coroutines.*


class TrackingFragment : BaseVBFragment<TrackingViewModel, FragmentTrackingBinding>(),
    OnMapReadyCallback,
    View.OnClickListener {

    private var currentZoomLevel: Float = Constants.MAP_ZOOM

    // init views
    private lateinit var mapView: MapView

    // declare variable
    private var map: GoogleMap? = null
    private var pathPoints = mutableListOf<MutableList<LocationModel>>()
    private val polylineOptions by lazy {
        PolylineOptions()
            .color(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            .width(resources.getDimension(R.dimen.polyline_width))
    }
    private var polylineHelper: PolylineHelper? = null

    private val stopWatchReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.onStopWatchReceived(intent)
        }
    }

    override fun getLayoutResId() = R.layout.fragment_tracking

    override fun injectDagger() {
        MainApp.getAppComponent().trackingComponent().create().inject(this)
    }

    override fun initViewBinding(view: View) = FragmentTrackingBinding.bind(view)

    override fun inits(binding: FragmentTrackingBinding, savedInstanceState: Bundle?) {
        mapView = binding.mapView
        binding.btnTracking.setOnClickListener(this)
        binding.btnStop.setOnClickListener(this)
        lifecycle.addObserver(MapViewLifecycleManager(mapView, savedInstanceState))
        subscribeToObservers(binding)
        requestLocationPermissions()
        mapView.getMapAsync(this)
        viewModel.onInit(arguments)
    }

    override fun onLowMemory() {
        mapView.onLowMemory()
        super.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_tracking -> {
                if (isLocationPermissionGranted())
                    viewModel.onTrackingClicked()
                else
                    requestLocationPermissions()
            }
            R.id.btnStop -> viewModel.onContinueClicked()
        }
    }

    private fun sendCommandToService(command: String) {
        val intent = Intent(requireContext(), LocationService::class.java).apply {
            action = command
        }
        startService(intent)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        if (isLocationPermissionGranted()) {
            MapConfig(map ?: return)
            polylineHelper = PolylineHelper(map ?: return, polylineOptions)
            polylineHelper?.addAllPolylines(pathPoints)
        }
    }

    private fun requestLocationPermissions() {
        if (PermissionUtils.isLocationPermissionGranted(requireContext())) {
            viewModel.onLocationPermissionGranted()
        } else {
            PermissionUtils.requestLocationPermissions(
                requireActivity() as BaseActivity,
                object : PermissionRequestable.Callback {
                    override fun onRequestPermissionsResult(
                        requestCode: Int,
                        permissions: Array<String>,
                        grantResults: IntArray,
                        showDialog: Boolean,
                    ) {
                        if (grantResults.isNotEmpty() && grantResults.first() == PermissionChecker.PERMISSION_GRANTED) {
                            viewModel.onLocationPermissionGranted()
                        }
                    }
                })
        }
    }

    private fun subscribeToObservers(binding: FragmentTrackingBinding) {
        viewModel.session.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    when (viewModel.trackingState.value) {
                        TrackingState.ON_CREATE -> onTrackingReady(it.data)
                        TrackingState.ON_RESUME,
                        TrackingState.ON_START -> onTracking(binding, it.data)
                    }
                }
                is Result.Error -> Unit
                Result.Loading -> Unit
            }
        }
        viewModel.stopWatchTime.observe(viewLifecycleOwner) {
            binding.tvTotalTime.text = it
        }
        viewModel.trackingState.observe(viewLifecycleOwner) {
            when (it) {
                TrackingState.ON_CREATE -> {
                    binding.btnTracking.setImageResource(R.drawable.ic_record)
                    binding.btnStop.isVisible = false
                    onTrackingReady(viewModel.session.value?.data ?: return@observe)
                }
                TrackingState.ON_RESUME,
                TrackingState.ON_START -> {
                    binding.btnTracking.setImageResource(R.drawable.ic_pause)
                    binding.btnStop.isVisible = false
                    sendCommandToService(Constants.ACTION_START_SERVICE)
                    registerReceiver(stopWatchReceiver, IntentFilter(Constant.TIMER_TICK_ACTION))
                }
                TrackingState.ON_PAUSE -> {
                    binding.btnTracking.setImageResource(R.drawable.ic_stop)
                    binding.btnStop.isVisible = true
                    sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
                    unregisterReceiver(stopWatchReceiver)
                }
                TrackingState.ON_STOP -> {
                    binding.btnTracking.setImageResource(R.drawable.ic_play)
                    binding.btnStop.isVisible = true
                    sendCommandToService(Constants.ACTION_STOP_SERVICE)
                    unregisterReceiver(stopWatchReceiver)
                }
            }
        }
        viewModel.action.observe(viewLifecycleOwner) {
            when (it ?: return@observe) {
                TrackingViewModel.Action.WARNING_EXIT_SESSION -> {
                    BaseDialogFragment.Builder().apply {
                        messageResId = R.string.msg_save_session_warning
                        negativeButtonResId = R.string.got_it
                    }
                        .show(childFragmentManager)
                }
                TrackingViewModel.Action.CONFIRM_SAVE_SESSION -> {
                    BaseDialogFragment.Builder().apply {
                        onClick = { event ->
                            if (event == BaseDialogFragment.Event.POSITIVE) {
                                map?.snapshot(viewModel::onSaveSession)
                            } else {
                                viewModel.onCancelSession()
                            }
                        }
                        titleResId = R.string.title_save_session_confirmation
                        messageResId = R.string.msg_save_session_confirmation
                        positiveButtonResId = R.string.finish
                        negativeButtonResId = R.string.cancel
                    }
                        .show(childFragmentManager)
                }
                TrackingViewModel.Action.CLOSE_SESSION -> requireActivity().finish()
            }
        }
    }

    private fun onTrackingReady(data: SessionModel) {
        val startLocation = data.startLocation.toLatLng()
        map?.addMarker(MarkerOptions().position(startLocation))
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, Constants.MAP_ZOOM))
    }

    private fun onTracking(binding: FragmentTrackingBinding, data: SessionModel) {
        binding.tvDistance.text = getString(R.string.distance_km_unit, data.distanceInKm)
        binding.tvSpeed.text = getString(R.string.speed_kmh_unit, data.speedInKmph)
        pathPoints = data.polylines.toMutableList()
        polylineHelper?.addLatestPolyline(pathPoints.last())
        if (viewModel.trackingState.value == TrackingState.ON_RESUME) {
            map?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    data.startLocation.toLatLng(),
                    Constants.MAP_ZOOM
                )
            )
        } else {
            moveCameraToUser()
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last().toLatLng(),
                    currentZoomLevel
                )
            )
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return PermissionUtils.isLocationPermissionGranted(requireActivity())
    }

    override fun onBackPressed(): Boolean {
        return viewModel.onBackPressed() || super.onBackPressed()
    }

    companion object {
        fun newInstance(isResume: Boolean) = TrackingFragment().apply {
            arguments = bundleOf("isResume" to isResume)
        }
    }
}
