package com.example.traveljournalapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.traveljournalapp.R
import com.example.traveljournalapp.databinding.AddTravelBinding
import com.example.traveljournalapp.databinding.OpeningScreenBinding

class OpeningScreenFragment : Fragment() {

    private var _binding : OpeningScreenBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = OpeningScreenBinding.inflate(inflater, container, false)

        val topToBottom = AnimationUtils.loadAnimation(requireContext() , R.anim.top_to_bottom)
        val btnToTop = AnimationUtils.loadAnimation(requireContext() , R.anim.btn_to_top)
        val btnToTopTwo = AnimationUtils.loadAnimation(requireContext() , R.anim.btn_to_top_two)

        binding.title.startAnimation(topToBottom)
        binding.subTitle.startAnimation(btnToTop)
        binding.getStartedBtn.startAnimation(btnToTopTwo)

        binding.getStartedBtn.setOnClickListener {
            findNavController().navigate(R.id.action_openingScreenFragment_to_travelsScreenFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}