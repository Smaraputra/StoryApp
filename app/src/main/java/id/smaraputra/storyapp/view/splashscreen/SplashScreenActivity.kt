package id.smaraputra.storyapp.view.splashscreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import id.smaraputra.storyapp.data.local.datastore.LoginPreferences
import id.smaraputra.storyapp.data.local.datastore.PreferencesViewModel
import id.smaraputra.storyapp.data.local.datastore.PreferencesViewModelFactory
import id.smaraputra.storyapp.databinding.ActivitySplashScreenBinding
import id.smaraputra.storyapp.view.HomeActivity
import id.smaraputra.storyapp.view.login.LoginActivity
import id.smaraputra.storyapp.view.onboarding.OnBoardingActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userSession")
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var preferencesViewModel: PreferencesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        setupViewModel()
    }

    private fun setupViewModel(){
        val pref = LoginPreferences.getInstance(dataStore)
        preferencesViewModel = ViewModelProvider(this, PreferencesViewModelFactory(pref)).get(
            PreferencesViewModel::class.java
        )

        preferencesViewModel.getStatusOnBoard().observe(this){ statusOnBoard ->
            if(statusOnBoard){
                preferencesViewModel.getTokenUser().observe(this){ token ->
                    if(!token.isNullOrEmpty() && !token.equals("DEFAULT_VALUE")){
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, splashDuration)
                    }else{
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, splashDuration)
                    }
                }
            }else{
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, OnBoardingActivity::class.java)
                    startActivity(intent)
                    finish()
                }, splashDuration)
            }
        }
    }

    companion object {
        const val splashDuration: Long = 3000
    }
}