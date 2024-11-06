package com.ezdev.emptyviewsactivity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ezdev.emptyviewsactivity.databinding.FragmentBlankBinding

private const val ARG_DATA = "ARG_DATA"

class BlankFragment : Fragment() {
    private var _binding: FragmentBlankBinding? = null
    private val binding: FragmentBlankBinding get() = _binding!!

    // service
    private lateinit var appService: AppService
    private var appBound: Boolean = false
    private val appConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, iBinder: IBinder?) {
            val binder = iBinder as AppService.AppBinder
            appService = binder.getService()
            appBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            appBound = false
        }
    }

    // data
    private var data: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getString(ARG_DATA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindAppService()
        _binding = FragmentBlankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data?.let {
            binding.textViewData.text = it
        }
        binding.buttonAction.setOnClickListener {
            val myData = "I'm in call." + System.currentTimeMillis()
            interactAppService(myData)
            binding.textViewData.text = myData
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbindAppService()
        _binding = null
    }

    // service
    private fun bindAppService() {
        val intent = Intent(requireContext(), AppService::class.java)
        requireActivity().bindService(intent, appConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindAppService() {
        requireActivity().unbindService(appConnection)
        appBound = false
    }

    private fun interactAppService(data: String) {
        if (!appBound) {
            return
        }

        AppService.startService(requireContext(), data)
    }

    companion object {
        @JvmStatic
        fun newInstance(data: String) =
            BlankFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DATA, data)
                }
            }
    }
}