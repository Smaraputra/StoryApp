package id.smaraputra.storyapp.view.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import id.smaraputra.storyapp.R
import id.smaraputra.storyapp.data.local.datastore.LoginPreferences
import id.smaraputra.storyapp.data.local.datastore.PreferencesViewModel
import id.smaraputra.storyapp.data.local.datastore.PreferencesViewModelFactory
import id.smaraputra.storyapp.databinding.CustomAlertLogoutBinding
import id.smaraputra.storyapp.databinding.FragmentSettingBinding
import id.smaraputra.storyapp.view.login.LoginActivity

class SettingFragment : Fragment() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userSession")
    private lateinit var preferencesViewModel: PreferencesViewModel
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView(){
        binding.logoutButton.setOnClickListener{
            showDialogLogout()
        }
        binding.changeLanguage.setOnClickListener{
            requireActivity().startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun setupViewModel(){
        val pref = LoginPreferences.getInstance(requireContext().dataStore)
        preferencesViewModel = ViewModelProvider(this, PreferencesViewModelFactory(pref)).get(
            PreferencesViewModel::class.java
        )

        preferencesViewModel.getNameUser().observe(viewLifecycleOwner) {
            if(!it.isNullOrEmpty() && !it.equals("DEFAULT_VALUE")){
                binding.namaUser.text=it.toString()
            }else{
                binding.namaUser.text=getString(R.string.no_data)
            }
        }
    }

    private fun showDialogLogout() {
        val builder = AlertDialog.Builder(requireContext()).create()
        val bindAlert: CustomAlertLogoutBinding = CustomAlertLogoutBinding.inflate(LayoutInflater.from(requireContext()))
        builder.setView(bindAlert.root)
        bindAlert.logoutConfirm.setOnClickListener {
            builder.dismiss()
            preferencesViewModel.saveNameUser("DEFAULT_VALUE")
            preferencesViewModel.saveTokenUser("DEFAULT_VALUE")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        }
        bindAlert.cancelButton.setOnClickListener {
            builder.dismiss()
        }
        builder.show()
    }
}