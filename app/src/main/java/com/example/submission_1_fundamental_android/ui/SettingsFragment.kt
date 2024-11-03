package com.example.submission_1_fundamental_android.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.submission_1_fundamental_android.R
import com.example.submission_1_fundamental_android.databinding.FragmentSettingsBinding
import com.example.submission_1_fundamental_android.notification.Worker
import com.example.submission_1_fundamental_android.viewModel.FactoryViewModel
import com.example.submission_1_fundamental_android.viewModel.MainViewModel
import java.util.concurrent.TimeUnit

class SettingsFragment : PreferenceFragmentCompat() {
    private var _binding: FragmentSettingsBinding? = null
    private lateinit var viewModel : MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory : FactoryViewModel = FactoryViewModel.getInstance(requireContext())
        viewModel = ViewModelProvider(requireActivity(),factory)[MainViewModel::class.java]

        val themeButton = findPreference<SwitchPreference>("keyTheme")
        val notifButton = findPreference<SwitchPreference>("keyNotif")

        viewModel.getThemeSetting().observe(viewLifecycleOwner){ isDark->
            themeButton?.isChecked = isDark
        }

        viewModel.getNotifSetting().observe(viewLifecycleOwner){isActive ->
            notifButton ?.isChecked = isActive
        }

        themeButton?.setOnPreferenceChangeListener{ _, newValue ->
            val isDark = newValue as Boolean
            viewModel.setThemeSetting(isDark)
            setTheme(isDark)
            true
        }

        notifButton?.setOnPreferenceChangeListener { _, newValue ->
            val isEnable = newValue as Boolean
            viewModel.setNotifSetting(isEnable)
            if(isEnable){
                startPriodic()
            }else{
                WorkManager.getInstance(requireContext()).cancelUniqueWork(Worker.NOTIFICATION_ID.toString())
            }
            true
        }
    }

    private fun startPriodic() {
        val workRequest: WorkRequest = PeriodicWorkRequestBuilder<Worker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(requireContext()).enqueue(workRequest)
    }

    private fun setTheme(isDark: Boolean) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference)
    }


}