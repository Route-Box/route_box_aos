package com.example.routebox.presentation.ui.route.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.routebox.databinding.FragmentRouteEditBinding

class RouteEditFragment : Fragment() {
    private lateinit var binding: FragmentRouteEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRouteEditBinding.inflate(inflater, container, false)

        return binding.root
    }
}