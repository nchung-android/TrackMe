package com.nchungdev.trackme.ui.tracking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.nchungdev.data.db.mapper.toLatLng
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
import com.nchungdev.trackme.ui.base.dialog.ConfirmationDialogFragment
import com.nchungdev.trackme.ui.base.fragment.BaseVMFragment
import com.nchungdev.trackme.util.*
import com.nchungdev.trackme.util.maps.MapConfig
import com.nchungdev.trackme.util.maps.MapViewLifecycleManager
import kotlinx.coroutines.*

class TrackingFragment : BaseVMFragment<TrackingViewModel, FragmentTrackingBinding>(),
    OnMapReadyCallback,
    View.OnClickListener {

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

    override fun injectDagger() {
        MainApp.getAppComponent().trackingComponent().create().inject(this)
    }

    override fun initViewBinding(inflater: LayoutInflater) =
        FragmentTrackingBinding.inflate(inflater)

    override fun onBindView(binding: FragmentTrackingBinding, savedInstanceState: Bundle?) {
        this.binding = binding
        mapView = binding.mapView
        binding.btnTracking.fab.setOnClickListener(this)
        binding.btnStop.setOnClickListener(this)
        lifecycle.addObserver(MapViewLifecycleManager(mapView, savedInstanceState))
        setDefaultData()
        subscribeToObservers()
        requestLocationPermissions()
        viewModel.onInit(arguments, requireContext().isServiceRunning(LocationService::class.java))
    }

    private fun setDefaultData() {
        (binding ?: return).apply {
            tvTotalTime.text = TimeUtils.getFormattedStopWatchTime(0L)
            tvDistance.text = getString(R.string.distance_km_unit, 0F)
            tvSpeed.text = getString(R.string.speed_kmh_unit, 0F)
        }
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
        MapConfig(map ?: return)
        polylineHelper = PolylineHelper(map ?: return, polylineOptions)
        polylineHelper?.addAllPolylines(pathPoints)
        viewModel.currentLocation.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    map?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            it.data.toLatLng(),
                            MapConfig.DEFAULT_ZOOM
                        )
                    )
                }
                is Result.Error -> Unit
                Result.Loading -> Unit
            }
        }
    }

    override fun onStart() {
        super.onStart()
        polylineHelper?.addAllPolylines(pathPoints)
    }

    private fun setupMapView() {
        mapView.getMapAsync(this)
    }

    private fun requestLocationPermissions() {
        if (PermissionUtils.isLocationPermissionGranted(requireContext())) {
            setupMapView()
            viewModel.onLocationPermissionGranted()
        } else {
            PermissionUtils.requestLocationPermissions(
                requireActivity() as BaseActivity<*>,
                object : PermissionRequestable.Callback {
                    override fun onRequestPermissionsResult(
                        requestCode: Int,
                        permissions: Array<String>,
                        grantResults: IntArray,
                        showDialog: Boolean,
                    ) {
                        if (grantResults.isNotEmpty() && grantResults.first() == PermissionChecker.PERMISSION_GRANTED) {
                            setupMapView()
                            viewModel.onLocationPermissionGranted()
                        } else {
                            showSnackBar()
                        }
                    }
                })
        }
    }

    private fun showSnackBar() {
        val view = view ?: return
        Snackbar.make(view, R.string.msg_permission_denied, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.open_setting) {
                requireContext().openDetailsSetting()
            }
    }

    private fun subscribeToObservers() {
        val binding = binding ?: return
        viewModel.session.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    when (viewModel.trackingState.value) {
                        TrackingState.START -> onTrackingReady(it.data)
                        TrackingState.RUNNING -> onTracking(it.data)
                        else -> onRestoreTracking(it.data)
                    }
                }
                is Result.Error -> Unit
                Result.Loading -> Unit
            }
        }
        viewModel.trackingState.observe(viewLifecycleOwner) {
            when (it) {
                TrackingState.START -> {
                    binding.btnTracking.fab.setImageResource(R.drawable.ic_record)
                    binding.btnStop.isVisible = false
                    viewModel.onStartRequestLocationUpdates()
                    onTrackingReady(viewModel.session.value?.data ?: return@observe)
                }
                TrackingState.RUNNING -> {
                    binding.btnTracking.fab.setImageResource(R.drawable.ic_pause)
                    binding.btnStop.isVisible = false
                    sendCommandToService(Constants.ACTION_START_SERVICE)
                    viewModel.onStopRequestLocationUpdates()
                }
                TrackingState.PAUSE -> {
                    binding.btnTracking.fab.setImageResource(R.drawable.ic_stop)
                    binding.btnStop.isVisible = true
                    sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
                    viewModel.onStartRequestLocationUpdates()
                }
                TrackingState.FINISH -> {
                    binding.btnTracking.fab.setImageResource(R.drawable.ic_play)
                    binding.btnStop.isVisible = true
                    sendCommandToService(Constants.ACTION_STOP_SERVICE)
                    viewModel.onStopRequestLocationUpdates()
                }
                else -> Unit
            }
        }
        viewModel.event.observe(viewLifecycleOwner) {
            when (it ?: return@observe) {
                TrackingViewModel.Event.WARNING_CLOSE_SESSION -> {
                    val builder = BaseDialogFragment.Builder().apply {
                        messageResId = R.string.msg_save_session_warning
                        negativeButtonResId = R.string.got_it
                    }
                    ConfirmationDialogFragment.newInstance(builder)
                        .show(childFragmentManager)
                }
                TrackingViewModel.Event.CONFIRM_CLOSE_SESSION -> {
                    val builder = BaseDialogFragment.Builder().apply {
                        onClick = { event ->
                            if (event == BaseDialogFragment.POSITIVE) {
                                map?.snapshot(viewModel::onSaveSession)
                            } else {
                                viewModel.onCloseSession()
                            }
                        }
                        titleResId = R.string.title_save_session_confirmation
                        messageResId = R.string.msg_save_session_confirmation
                        positiveButtonResId = R.string.save_and_close_session
                        negativeButtonResId = R.string.close_without_save_session
                    }
                    ConfirmationDialogFragment.newInstance(builder)
                        .show(childFragmentManager)
                }
                TrackingViewModel.Event.SAVE_AND_CLOSE -> requireActivity().apply {
                    if (isTaskRoot) {
                        Navigator.openMainActivity(this)
                    }
                    finish()
                }
                TrackingViewModel.Event.CLOSE_WITHOUT_SAVE -> requireActivity().apply {
                    if (isTaskRoot) {
                        Navigator.openMainActivity(this)
                    }
                    finish()
                }
            }
        }
    }

    private fun showSessionData(session: SessionModel) {
        (binding ?: return).apply {
            tvTotalTime.text = TimeUtils.getFormattedStopWatchTime(session.timeInMillis)
            tvDistance.text = getString(R.string.distance_km_unit, session.distanceInKm)
            tvSpeed.text = getString(R.string.speed_kmh_unit, session.speedInKmph)
            tvTotalTime.text = TimeUtils.getFormattedStopWatchTime(session.timeInMillis)
        }
    }

    private fun onTrackingReady(data: SessionModel) {
        showSessionData(data)
        val startLocation = data.startLocation.toLatLng()
        map?.addMarker(MarkerOptions().position(startLocation))
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, MapConfig.DEFAULT_ZOOM))
    }

    private fun onTracking(data: SessionModel) {
        showSessionData(data)
        pathPoints = data.polylines.toMutableList()
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            polylineHelper?.addLatestPolyline(pathPoints.last())
            map?.animateCamera(CameraUpdateFactory.newLatLng(pathPoints.last().last().toLatLng()))
        }
    }

    private fun onRestoreTracking(session: SessionModel) {
        showSessionData(session)
        pathPoints = session.polylines.toMutableList()
        polylineHelper?.addAllPolylines(pathPoints)
        val startLocation = session.startLocation.toLatLng()
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, MapConfig.DEFAULT_ZOOM))
    }

    private fun isLocationPermissionGranted(): Boolean {
        return PermissionUtils.isLocationPermissionGranted(requireActivity())
    }

    override fun onBackPressed(): Boolean {
        return viewModel.onBackPressed() || super.onBackPressed()
    }

    companion object {
        const val EXTRA_OPEN_FROM_NOTIF = "xOpenFromNotif"
        const val EXTRA_OPEN_FROM_SPLASH = "xOpenFromSplashScreen"
    }
}
