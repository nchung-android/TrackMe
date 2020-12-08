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
import com.nchungdev.data.util.TimeUtils
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
import com.nchungdev.trackme.ui.util.isMyServiceRunning
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
    private var binding: FragmentTrackingBinding? = null

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
        this.binding = binding
        mapView = binding.mapView
        binding.btnTracking.setOnClickListener(this)
        binding.btnStop.setOnClickListener(this)
        lifecycle.addObserver(MapViewLifecycleManager(mapView, savedInstanceState))
        subscribeToObservers()
        requestLocationPermissions()
        mapView.getMapAsync(this)
        viewModel.onInit(arguments,
            context?.isMyServiceRunning(LocationService::class.java) == true)
    }

    override fun onStop() {
        unregisterReceiver(stopWatchReceiver)
        super.onStop()
    }

    override fun onStart() {
        if (viewModel.trackingState.value == TrackingState.START) {
            registerReceiver(stopWatchReceiver, IntentFilter(Constant.TIMER_TICK_ACTION))
        }
        super.onStart()
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
            viewModel.currentLocation.observe(viewLifecycleOwner) {
                when (it) {
                    is Result.Success -> {
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                it.data.toLatLng(),
                                Constants.MAP_ZOOM
                            )
                        )
                    }
                    is Result.Error -> Unit
                    Result.Loading -> Unit
                }
            }
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

    private fun subscribeToObservers() {
        val binding = binding ?: return
        viewModel.session.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    when (viewModel.trackingState.value) {
                        TrackingState.READY -> onTrackingReady(it.data)
                        TrackingState.START -> onTracking(it.data)
                        else -> showSessionData(it.data)
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
                TrackingState.READY -> {
                    binding.btnTracking.setImageResource(R.drawable.ic_record)
                    binding.btnStop.isVisible = false
                    onTrackingReady(viewModel.session.value?.data ?: return@observe)
                    viewModel.onStartRequestLocationUpdates()
                }
                TrackingState.START -> {
                    binding.btnTracking.setImageResource(R.drawable.ic_pause)
                    binding.btnStop.isVisible = false
                    sendCommandToService(Constants.ACTION_START_SERVICE)
                    registerReceiver(stopWatchReceiver, IntentFilter(Constant.TIMER_TICK_ACTION))
                    viewModel.onStopRequestLocationUpdates()
                }
                TrackingState.PAUSE -> {
                    binding.btnTracking.setImageResource(R.drawable.ic_stop)
                    binding.btnStop.isVisible = true
                    sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
                    viewModel.onStartRequestLocationUpdates()
                }
                TrackingState.FINISH -> {
                    binding.btnTracking.setImageResource(R.drawable.ic_play)
                    binding.btnStop.isVisible = true
                    sendCommandToService(Constants.ACTION_STOP_SERVICE)
                    unregisterReceiver(stopWatchReceiver)
                    viewModel.onStopRequestLocationUpdates()
                }
                else -> Unit
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

    private fun showSessionData(session: SessionModel) {
        pathPoints = session.polylines.toMutableList()
        polylineHelper?.addLatestPolyline(pathPoints.last())
        val startLocation = session.startLocation.toLatLng()
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, Constants.MAP_ZOOM))
        val binding = binding ?: return
        binding.tvTotalTime.text = TimeUtils.getFormattedStopWatchTime(session.timeInMillis)
        binding.tvDistance.text = getString(R.string.distance_km_unit, session.distanceInKm)
        binding.tvSpeed.text = getString(R.string.speed_kmh_unit, session.speedInKmph)
    }

    private fun onTrackingReady(data: SessionModel) {
        val startLocation = data.startLocation.toLatLng()
        map?.addMarker(MarkerOptions().position(startLocation))
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, Constants.MAP_ZOOM))
    }

    private fun onTracking(data: SessionModel) {
        showSessionData(data)
        pathPoints = data.polylines.toMutableList()
        polylineHelper?.addLatestPolyline(pathPoints.last())
        moveCameraToUser()
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
        const val EXTRA_SESSION = "xSession"

        fun newInstance(sessionModel: SessionModel?) = TrackingFragment().apply {
            arguments = bundleOf(EXTRA_SESSION to sessionModel)
        }
    }
}
