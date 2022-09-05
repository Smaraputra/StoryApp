package id.smaraputra.storyapp.view.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import id.smaraputra.storyapp.R
import id.smaraputra.storyapp.data.Result
import id.smaraputra.storyapp.data.local.datastore.LoginPreferences
import id.smaraputra.storyapp.data.local.datastore.PreferencesViewModel
import id.smaraputra.storyapp.data.local.datastore.PreferencesViewModelFactory
import id.smaraputra.storyapp.databinding.CustomAlertApiBinding
import id.smaraputra.storyapp.databinding.FragmentMapBinding
import id.smaraputra.storyapp.view.addstory.AddStoryActivity
import id.smaraputra.storyapp.view.liststory.ListViewModel
import id.smaraputra.storyapp.view.liststory.ViewModelFactory
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MapFragment : Fragment() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userSession")
    private lateinit var preferencesViewModel: PreferencesViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var locationRequest: LocationRequest
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        createLocationRequest()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        setupView()
        setupViewModel()
    }

    private fun setupToolbar(){
        binding.myToolbar.inflateMenu(R.menu.map_options)
        binding.myToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.normal_type -> {
                    mMap.setMapStyle(null)
                    mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                    true
                }
                R.id.satellite_type -> {
                    mMap.setMapStyle(null)
                    mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    true
                }
                R.id.terrain_type -> {
                    mMap.setMapStyle(null)
                    mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                    true
                }
                R.id.hybrid_type -> {
                    mMap.setMapStyle(null)
                    mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                    true
                }
                R.id.custom_type -> {
                    mMap.setMapStyle(null)
                    mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                    setMapStyle()
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
    }

    private fun setupViewModel(){
        val pref = LoginPreferences.getInstance(requireActivity().dataStore)
        preferencesViewModel = ViewModelProvider(this, PreferencesViewModelFactory(pref)).get(
            PreferencesViewModel::class.java
        )
        preferencesViewModel.getTokenUser().observe(viewLifecycleOwner) { token ->
            val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity(), token)
            val listViewModel: ListViewModel by viewModels {
                factory
            }
            listViewModel.listStoryLocation().observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    binding.noData.visibility = View.GONE
                    when (result) {
                        is Result.Loading -> {
                            binding.loadingList.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.loadingList.visibility = View.GONE
                            val mapData = result.data
                            lifecycle.coroutineScope.launch {
                                for (d in mapData){
                                    val mapCord = LatLng(d.lat, d.lon)
                                    mMap.addMarker(MarkerOptions()
                                        .position(mapCord)
                                        .title(d.name)
                                        .snippet(d.description))
                                }
                            }
                        }
                        is Result.Error -> {
                            binding.loadingList.visibility = View.GONE
                            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_error_24)
                                ?.let { showDialog(result.error, it) }
                        }
                    }
                }else{
                    binding.noData.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupView(){
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(requireActivity(), AddStoryActivity::class.java)
            requireActivity().startActivity(intent)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style))
            if (!success) {
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_error_24)
                    ?.let { showDialog(getString(R.string.parsing_error), it) }
            }
        } catch (exception: Resources.NotFoundException) {
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_error_24)
                ?.let { showDialog(getString(R.string.style_not_found), it) }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {

                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        lifecycle.coroutineScope.launch {
            if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            ){
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                Activity.RESULT_CANCELED -> ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_error_24)
                    ?.let { showDialog(getString(R.string.confirm_turn_on_gps), it) }
            }
        }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireActivity())
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
                mMap.uiSettings.isMyLocationButtonEnabled = true
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(requireActivity(), sendEx.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun showDialog(text: String, icon: Drawable) {
        val builder = AlertDialog.Builder(requireActivity()).create()
        val bindAlert: CustomAlertApiBinding = CustomAlertApiBinding.inflate(LayoutInflater.from(requireActivity()))
        builder.setView(bindAlert.root)
        bindAlert.infoDialog.text = text
        bindAlert.imageView5.setImageDrawable(icon)
        bindAlert.closeButton.setOnClickListener {
            builder.dismiss()
        }
        builder.show()
    }
}