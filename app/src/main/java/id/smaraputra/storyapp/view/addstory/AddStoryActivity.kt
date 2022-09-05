package id.smaraputra.storyapp.view.addstory

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import id.smaraputra.storyapp.R
import id.smaraputra.storyapp.data.Result
import id.smaraputra.storyapp.data.local.datastore.LoginPreferences
import id.smaraputra.storyapp.data.local.datastore.PreferencesViewModel
import id.smaraputra.storyapp.data.local.datastore.PreferencesViewModelFactory
import id.smaraputra.storyapp.databinding.ActivityAddStoryBinding
import id.smaraputra.storyapp.databinding.CustomAlertApiBinding
import id.smaraputra.storyapp.utils.reduceFileImage
import id.smaraputra.storyapp.utils.rotateBitmap
import id.smaraputra.storyapp.utils.uriToFile
import id.smaraputra.storyapp.view.HomeActivity
import id.smaraputra.storyapp.view.mycamera.CameraActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.concurrent.TimeUnit

class AddStoryActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userSession")
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var preferencesViewModel: PreferencesViewModel
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private var description : String = ""
    private var status : Boolean = false
    private lateinit var lat : RequestBody
    private lateinit var lon : RequestBody
    private var getFile: File? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_error_24)
                    ?.let { showDialogPermission(getString(R.string.permission_negative), it) }
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        title = getString(R.string.add_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0.0F
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupPermissions()
        setupView()
        setupViewModel()
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )
            getFile = myFile
            binding.imageNewPost.setImageBitmap(result)
            stateButton()
        }else{
            stateButton()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)

            getFile = myFile
            binding.imageNewPost.setImageURI(selectedImg)
            stateButton()
        }else{
            stateButton()
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val descriptions = description.toRequestBody("text/plain".toMediaType())
            if(status){
                lat = currentLocation.latitude.toString().toRequestBody("text/plain".toMediaType())
                lon = currentLocation.longitude.toString().toRequestBody("text/plain".toMediaType())
            }else{
                val zeroLatLon = 0.0
                lat = zeroLatLon.toString().toRequestBody("text/plain".toMediaType())
                lon = zeroLatLon.toString().toRequestBody("text/plain".toMediaType())
            }
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val liveData = addStoryViewModel.addStory(imageMultipart, descriptions,lat,lon)
            liveData.observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.loadingList.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.loadingList.visibility = View.GONE
                            ContextCompat.getDrawable(this, R.drawable.ic_baseline_check_circle_24)
                                ?.let { showDialog(result.data.message, it, true) }
                            liveData.removeObservers(this)
                        }
                        is Result.Error -> {
                            binding.loadingList.visibility = View.GONE
                            ContextCompat.getDrawable(this, R.drawable.ic_baseline_error_24)
                                ?.let { showDialog(result.error, it, false) }
                            liveData.removeObservers(this)
                        }
                    }
                }
            }
        }
    }

    private fun setupPermissions() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
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
                    binding.checkBoxGPS.isChecked = false
                    ContextCompat.getDrawable(this, R.drawable.ic_baseline_error_24)
                        ?.let { showDialogPermission(getString(R.string.permission_location), it) }
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                    status = true
                } else {
                    ContextCompat.getDrawable(this, R.drawable.ic_baseline_error_24)
                        ?.let { showDialogPermission(getString(R.string.not_found_location), it) }
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if(result.resultCode == Activity.RESULT_CANCELED) {
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_error_24)
                    ?.let { showDialogPermission(getString(R.string.confirm_turn_on_gps), it) }
                binding.checkBoxGPS.isChecked = false
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
        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(this, sendEx.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun setupView() {
        binding.checkBoxGPS.setOnClickListener{
            if(binding.checkBoxGPS.isChecked){
                createLocationRequest()
            }
        }
        binding.descriptionNew.addTextChangedListener(textWatcher)
        binding.cameraXButton.setOnClickListener {
            if(checkPermission(REQUIRED_PERMISSIONS[0])){
                startCameraX()
            }else{
                setupPermissions()
            }
        }
        binding.galleryButton.setOnClickListener {
            startGallery()
        }
        binding.uploadButton.setOnClickListener{
            uploadImage()
        }
    }

    private fun setupViewModel() {
        val pref = LoginPreferences.getInstance(dataStore)
        preferencesViewModel = ViewModelProvider(this, PreferencesViewModelFactory(pref)).get(
            PreferencesViewModel::class.java
        )
        preferencesViewModel.getTokenUser().observe(this) { token ->
            val factory: AddStoryViewModelFactory = AddStoryViewModelFactory.getInstance(this, token)
            val addStoryViewModel: AddStoryViewModel by viewModels {
                factory
            }
            this.addStoryViewModel=addStoryViewModel
        }
    }

    private val textWatcher: TextWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            description = binding.descriptionNew.text.toString()
            stateButton()
        }
        override fun afterTextChanged(s: Editable) {

        }
    }

    private fun stateButton() {
        binding.uploadButton.isEnabled =
            description.isNotEmpty() && (getFile != null)
    }

    private fun showDialogPermission(text: String, icon: Drawable) {
        val builder = AlertDialog.Builder(this).create()
        val bindAlert: CustomAlertApiBinding = CustomAlertApiBinding.inflate(LayoutInflater.from(this))
        builder.setView(bindAlert.root)
        bindAlert.infoDialog.text = text
        bindAlert.closeButton.text = getString(R.string.close)
        bindAlert.imageView5.setImageDrawable(icon)
        bindAlert.closeButton.setOnClickListener {
            builder.dismiss()
        }
        builder.show()
    }

    private fun showDialog(text: String, icon: Drawable, status: Boolean) {
        val builder = AlertDialog.Builder(this).create()
        val bindAlert: CustomAlertApiBinding = CustomAlertApiBinding.inflate(LayoutInflater.from(this))
        builder.setView(bindAlert.root)
        bindAlert.infoDialog.text = text
        bindAlert.imageView5.setImageDrawable(icon)
        if(status){
            bindAlert.closeButton.setOnClickListener {
                builder.dismiss()
                val intent = Intent(this@AddStoryActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            builder.setCancelable(false)
        }else{
            bindAlert.closeButton.setOnClickListener {
                builder.dismiss()
            }
        }
        builder.show()
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}