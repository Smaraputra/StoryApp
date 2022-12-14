package id.smaraputra.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import id.smaraputra.storyapp.R
import id.smaraputra.storyapp.data.Result
import id.smaraputra.storyapp.data.local.datastore.LoginPreferences
import id.smaraputra.storyapp.data.local.datastore.PreferencesViewModel
import id.smaraputra.storyapp.data.local.datastore.PreferencesViewModelFactory
import id.smaraputra.storyapp.databinding.ActivityLoginBinding
import id.smaraputra.storyapp.databinding.CustomAlertApiBinding
import id.smaraputra.storyapp.utils.wrapEspressoIdlingResource
import id.smaraputra.storyapp.view.HomeActivity
import id.smaraputra.storyapp.view.register.RegisterActivity
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userSession")
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var preferencesViewModel: PreferencesViewModel
    private lateinit var password : String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        wrapEspressoIdlingResource {
            setupView()
            setupViewModel()
            playAnimation()
        }
    }

    private fun setupView() {
        binding.emailEditText.addTextChangedListener(textWatcher)
        binding.passwordEditText.addTextChangedListener(textWatcher)
        binding.loginButton.setOnClickListener {
            sendData()
        }

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.languageLogin.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun setupViewModel() {
        val pref = LoginPreferences.getInstance(dataStore)
        preferencesViewModel = ViewModelProvider(this, PreferencesViewModelFactory(pref)).get(
            PreferencesViewModel::class.java
        )
        val factory: LoginViewModelFactory = LoginViewModelFactory.getInstance(this, "")
        val loginViewModel: LoginViewModel by viewModels {
            factory
        }
        this.loginViewModel=loginViewModel
    }

    private fun playAnimation() {
        val titleEmail = ObjectAnimator.ofFloat(binding.emailTitle, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
        val titlePass = ObjectAnimator.ofFloat(binding.passwordTitle, View.ALPHA, 1f).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)

        val togetherEmail = AnimatorSet().apply {
            playTogether(titleEmail, email)
        }

        val togetherPass = AnimatorSet().apply {
            playTogether(titlePass, pass)
        }

        val togetherButton = AnimatorSet().apply {
            playTogether(login, register)
        }

        AnimatorSet().apply {
            playSequentially(togetherEmail, togetherPass, togetherButton)
            start()
        }
    }

    private fun sendData(){
        val loginMap: HashMap<String, String> = HashMap()
        loginMap["email"] = email
        loginMap["password"] = password
        val liveData = loginViewModel.loginUser(loginMap)
        liveData.observe(this){ result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.loadingList.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.loadingList.visibility = View.GONE
                        result.data.loginResult?.let { preferencesViewModel.saveTokenUser(it.token) }
                        result.data.loginResult?.let { preferencesViewModel.saveNameUser(it.name) }
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                        liveData.removeObservers(this)
                    }
                    is Result.Error -> {
                        binding.loadingList.visibility = View.GONE
                        ContextCompat.getDrawable(this, R.drawable.ic_baseline_error_24)
                            ?.let { showDialog(result.error, it) }
                        liveData.removeObservers(this)
                    }
                }
            }
        }
    }

    private val textWatcher: TextWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            email = binding.emailEditText.text.toString()
            password = binding.passwordEditText.text.toString()
            val pattern: Pattern = Pattern.compile(REGEX_EMAIL)
            val matcher: Matcher = pattern.matcher(email)
            binding.loginButton.isEnabled =
                !(!matcher.matches() || email.isEmpty() || password.length<6 || password.isEmpty())
        }
        override fun afterTextChanged(s: Editable) {

        }
    }

    private fun showDialog(text: String, icon: Drawable) {
        val builder = AlertDialog.Builder(this).create()
        val bindAlert: CustomAlertApiBinding = CustomAlertApiBinding.inflate(LayoutInflater.from(this))
        builder.setView(bindAlert.root)
        bindAlert.infoDialog.text = text
        bindAlert.imageView5.setImageDrawable(icon)
        bindAlert.closeButton.setOnClickListener {
            builder.dismiss()
        }
        builder.show()
    }

    companion object {
        const val REGEX_EMAIL = "^[\\w!#\$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#\$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}\$"
    }
}