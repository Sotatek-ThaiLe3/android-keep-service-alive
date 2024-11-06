package com.ezdev.emptyviewsactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ezdev.emptyviewsactivity.databinding.FragmentBlankBinding

private const val ARG_DATA = "ARG_DATA"

class BlankFragment : Fragment() {
    private var _binding: FragmentBlankBinding? = null
    private val binding: FragmentBlankBinding get() = _binding!!

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
        _binding = FragmentBlankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data?.let {
            binding.textViewData.text = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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