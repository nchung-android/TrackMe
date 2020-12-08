package com.nchungdev.trackme.ui.detail

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.nchungdev.data.db.mapper.toLatLng
import com.nchungdev.domain.model.LocationModel
import com.nchungdev.domain.model.SessionModel
import com.nchungdev.trackme.MainApp
import com.nchungdev.trackme.R
import com.nchungdev.trackme.databinding.FragmentDetailBinding
import com.nchungdev.trackme.ui.base.fragment.BaseVBFragment
import com.nchungdev.trackme.ui.util.MapConfig
import com.nchungdev.trackme.ui.util.MapViewLifecycleManager
import com.nchungdev.trackme.ui.util.PolylineHelper

class DetailFragment : BaseVBFragment<DetailViewModel, FragmentDetailBinding>(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private var map: GoogleMap? = null

    private var pathPoints = mutableListOf<MutableList<LocationModel>>()
    private val polylineOptions by lazy {
        PolylineOptions()
            .color(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            .width(resources.getDimension(R.dimen.polyline_width))
    }

    private var polylineHelper: PolylineHelper? = null

    override fun initViewBinding(view: View): FragmentDetailBinding {
        return FragmentDetailBinding.bind(view)
    }

    override fun injectDagger() {
        MainApp.getAppComponent().detailComponent().create().inject(this)
    }

    override fun getLayoutResId(): Int = R.layout.fragment_detail

    override fun inits(binding: FragmentDetailBinding, savedInstanceState: Bundle?) {
        super.inits(binding, savedInstanceState)
        mapView = binding.mapView
        lifecycle.addObserver(MapViewLifecycleManager(binding.mapView, savedInstanceState))
        mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val map = googleMap ?: return
        MapConfig(map)
        polylineHelper = PolylineHelper(map, polylineOptions)
        polylineHelper?.addAllPolylines(pathPoints)
        this.map = map
        viewModel.onReceiveIntent(arguments)
        viewModel.session.observe(viewLifecycleOwner) {
            if (it != null) {
                val startLocation = it.startLocation.toLatLng()
                map.addMarker(MarkerOptions().position(startLocation))
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        startLocation,
                        MapConfig.DEFAULT_ZOOM
                    )
                )
                pathPoints = it.polylines.toMutableList()
                polylineHelper?.addAllPolylines(pathPoints)
                moveCameraToUser()
            }
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last().toLatLng(),
                    MapConfig.DEFAULT_ZOOM
                )
            )
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

    companion object {

        const val EXTRA_SESSION = "session"

        fun newInstance(sessionModel: SessionModel?) = DetailFragment().apply {
            sessionModel ?: return@apply
            arguments = bundleOf(EXTRA_SESSION to sessionModel)
        }
    }
}