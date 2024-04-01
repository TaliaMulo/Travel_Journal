package com.example.traveljournalapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.traveljournalapp.R
import com.example.traveljournalapp.databinding.DetailTravelBinding
import com.example.traveljournalapp.data.model.Travel
import com.example.traveljournalapp.ui.TravelsViewModel

class DetailsTravelFragment : Fragment() {

    private var _binding : DetailTravelBinding? = null

    private val binding get() = _binding!!

    val viewModel : TravelsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DetailTravelBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_detailsTravelFragment_to_travelsScreenFragment)
        }

        viewModel.chosenTravel.observe(viewLifecycleOwner) {travel ->
            updateUI(travel)
        }


        viewModel.chosenTravel.observe(viewLifecycleOwner){
            binding.countryTitle.text = it.countryName
            binding.destinationTitle.text = it.destinationName
            binding.destinationDesc.text = it.destinationDescription
            binding.dateTV.text = it.date
            binding.locationTV.text = it.location
            Glide.with(requireContext()).load(it.photo).into(binding.destinationImage)

            viewModel.editSetSelectedDate(it.date)
            viewModel.editSetSelectedLocation(it.location)
            viewModel.editSetSelectedImageUri(it.photo.toString().toUri())

            val bundle = bundleOf(
                "countryName" to it.countryName,
                "destinationName" to it.destinationName,
                "destinationDescription" to it.destinationDescription,
                "location" to it.location,
                "image" to it.photo,
                "date" to it.date,
                "id" to it.id
            )
            binding.editTravelDetailsBtn.setOnClickListener {
                findNavController().navigate(R.id.action_detailsTravelFragment_to_editDetailsTravelFragment , bundle )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()


        _binding = null
    }

    private fun updateUI(travel: Travel) {
        binding.destinationTitle.text = travel.countryName
        binding.destinationTitle.text = travel.destinationName
        binding.destinationDesc.text = travel.destinationDescription
        binding.dateTV.text = travel.date
        binding.locationTV.text = travel.location
        Glide.with(requireContext()).load(travel.photo).into(binding.destinationImage)

    }

}