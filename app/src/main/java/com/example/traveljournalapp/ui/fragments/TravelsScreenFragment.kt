package com.example.traveljournalapp.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.traveljournalapp.R
import com.example.traveljournalapp.databinding.TravelsScreenBinding
import com.example.traveljournalapp.data.model.Travel
import com.example.traveljournalapp.ui.TravelAdapter
import com.example.traveljournalapp.ui.TravelsViewModel

class TravelsScreenFragment : Fragment() {

    private var _binding : TravelsScreenBinding? = null

    private val binding get() = _binding!!

    private val filteredTravels = mutableListOf<Travel>()

    private val viewModel : TravelsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = TravelsScreenBinding.inflate(inflater, container, false)

        val topToBottom = AnimationUtils.loadAnimation(requireContext() , R.anim.top_to_bottom)

        binding.myTravels.startAnimation(topToBottom)
        binding.search.startAnimation(topToBottom)
        binding.deleteAllBtn.startAnimation(topToBottom)

        binding.addTravelBtn.setOnClickListener {
            findNavController().navigate(R.id.action_travelsScreenFragment_to_addTravelFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("title")?.let {
            Toast.makeText(requireActivity(), it , Toast.LENGTH_SHORT).show()
        }

        binding.exitBtn.setOnClickListener {
            val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.apply {
                setTitle(getString(R.string.exit_title))
                setMessage(getString(R.string.exit))
                setCancelable(false)
                setIcon(R.drawable.baseline_exit_to_app_24)
                setPositiveButton(getString(R.string.yes)) { dialog, which ->
                    finishAffinity(requireActivity())
                }
                setNegativeButton(getString(R.string.no)){ dialog, which ->
                }
                val dialogN = builder.create()
                dialogN.show()
            }
        }

        viewModel.travels?.observe(viewLifecycleOwner){

            binding.travelRecyclerView.adapter = TravelAdapter(it , object : TravelAdapter.ItemListener {
                override fun onItemClicked(index: Int) {
                    viewModel.setTravel(it[index])
                    findNavController().navigate(R.id.action_travelsScreenFragment_to_detailsTravelFragment)
                }
                override fun onDeleteClicked(travel: Travel) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                    builder.apply {
                        setTitle(getString(R.string.confirm_delete_title))
                        setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_travel))
                        setCancelable(false)
                        setIcon(R.drawable.baseline_delete_24)
                        setPositiveButton(getString(R.string.yes)) { dialog, which ->
                            viewModel.deleteTravel(travel)
                            Toast.makeText(requireContext(), getString(R.string.travel_deleted_message), Toast.LENGTH_SHORT).show()
                        }
                        setNegativeButton(getString(R.string.no)) { dialog, which ->
                        }
                        val dialog = builder.create()
                        dialog.show()
                    }
                }
            })
            binding.travelRecyclerView.layoutManager = GridLayoutManager(requireContext() , 1)
        }

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                viewModel.searchQuery.observe(viewLifecycleOwner) {
                    filterTravels(it)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.updateSearchQuery(s.toString())
                filterTravels(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.deleteAllBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.apply {
                setTitle(getString(R.string.confirm_delete_title))
                setMessage(getString(R.string.delete_all_message))
                setCancelable(false)
                setIcon(R.drawable.baseline_delete_24)
                setPositiveButton(getString(R.string.yes)) { dialog, which ->
                    viewModel.deleteAll()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.travel_deleted_message), Toast.LENGTH_SHORT
                    ).show()
                }
                setNegativeButton(getString(R.string.no)) { dialog, which ->
                }
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun filterTravels(query: String) {
        filteredTravels.clear()
        if (query.isEmpty()) {
            filteredTravels.addAll(viewModel.travels?.value ?: emptyList())
        } else {
            viewModel.travels?.value?.filter {
                it.countryName.contains(query, ignoreCase = true) ||
                        it.destinationName.contains(query, ignoreCase = true) ||
                        it.date.contains(query, ignoreCase = true)
            }?.let { filteredTravels.addAll(it) }
        }
        binding.travelRecyclerView.adapter?.let {
            (it as TravelAdapter).filterList(filteredTravels)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

