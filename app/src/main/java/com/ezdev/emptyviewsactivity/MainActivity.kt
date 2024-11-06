package com.ezdev.emptyviewsactivity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ezdev.emptyviewsactivity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // permissions
    private var canPushNotifications: Boolean = false
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            canPushNotifications = isGranted
        }

    // service
    private lateinit var appService: AppService
    private var appBound: Boolean = false
    private val appConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, iBinder: IBinder?) {
            val binder = iBinder as AppService.AppBinder
            appService = binder.getService()
            appBound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            appBound = false
        }
    }

    // data
    private var data: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCall.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestRuntimePermission(PERMISSION)
            }

            if (!canPushNotifications) {
                return@setOnClickListener
            }

            val myData = "I'm calling..." + System.currentTimeMillis()
            interactAppService(myData)
            replaceFragment(BlankFragment.newInstance(myData))
        }

        receiveServiceData()
        restoreUi()
    }

    override fun onStart() {
        super.onStart()
        bindAppService()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindAppService()
    }

    // permissions
    private fun requestRuntimePermission(permission: String) {
        when {
            checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED -> {
                canPushNotifications = true
            }

            shouldShowRequestPermissionRationale(permission) -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }

            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    // service
    private fun bindAppService() {
        val intent = Intent(this, AppService::class.java)
        bindService(intent, appConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindAppService() {
        unbindService(appConnection)
        appBound = false
    }

    private fun interactAppService(data: String) {
        if (!appBound) {
            return
        }

        AppService.startService(this, data)
    }

    // data
    private fun receiveServiceData() {
        data = intent.getStringExtra(EXTRA_DATA)
    }

    private fun restoreUi() {
        data?.let {
            replaceFragment(BlankFragment.newInstance(it))
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment, fragment::class.java.canonicalName)
            .commit()
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val PERMISSION = android.Manifest.permission.POST_NOTIFICATIONS
    }

}