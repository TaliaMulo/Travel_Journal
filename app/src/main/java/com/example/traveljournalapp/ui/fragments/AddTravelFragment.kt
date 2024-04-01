package com.example.traveljournalapp.ui.fragments

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.traveljournalapp.R
import com.example.traveljournalapp.databinding.AddTravelBinding
import com.example.traveljournalapp.data.model.Travel
import com.example.traveljournalapp.ui.TravelsViewModel
import java.io.ByteArrayOutputStream
import java.util.Calendar


class AddTravelFragment : Fragment() {

    private var _binding : AddTravelBinding? = null

    private val binding get() = _binding!!

    private val viewModel : TravelsViewModel by activityViewModels()

    private val locationRequestLauncher : ActivityResultLauncher<String>
    = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            getLocationUpdates()
        }
    }

    var date : String = ""

    var flag: Int? = null

    private var imageUri : String? = null

    val pickImageLauncher : ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            binding.resultImage.setImageURI(it)
            viewModel.setSelectedImageUri(it)
            requireActivity().contentResolver.takePersistableUriPermission(it!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            imageUri = it.toString()
        }

    val cameraThumbnailLauncher  =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
            binding.resultImage.setImageBitmap(it)

            val baos = ByteArrayOutputStream()
            it!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val imageData = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

            imageUri = "data:image/png;base64,$imageData"
            viewModel.setSelectedImageUri(imageUri.toString().toUri())

        }

    val recognizeSpeechLauncher:ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                if (flag == 1){
                    binding.countryName.setText(it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).toString())
                }
                else if (flag == 2){
                    binding.destinationName.setText(it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).toString())
                }
                else{
                    binding.destinationDescription.setText(it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).toString())
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = AddTravelBinding.inflate(inflater, container, false)


        binding.locationBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext() , android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
                getLocationUpdates()
            }
            else{
                locationRequestLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        binding.countryVoice.setOnClickListener {
            flag = 1
            recognizerLunch()
        }

        binding.destinationVoice.setOnClickListener {
            flag = 2
            recognizerLunch()
        }

        binding.descriptionVoice.setOnClickListener {
            flag = 3
            recognizerLunch()
        }

        binding.cameraBtn.setOnClickListener {
            cameraThumbnailLauncher.launch(null)
        }

        binding.imageBtn.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }


        binding.dateBtn.setOnClickListener {
            val Date = Calendar.getInstance()
            val dateListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val mon = month + 1
                    date = ("$dayOfMonth/$mon/$year")
                    binding.dateText.setText(date)
                    viewModel.setSelectedDate(date)
                }
            val dtd = DatePickerDialog(
                requireContext(), dateListener, Date.get(Calendar.YEAR), Date.get(
                    Calendar.MONTH
                ), Date.get(Calendar.DAY_OF_MONTH)
            )
            dtd.show()
        }

        viewModel.selectedImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                imageUri = it.toString()
                Glide.with(requireContext()).load(it).into(binding.resultImage)
            }
        }

        viewModel.selectedLocation.observe(viewLifecycleOwner) {
            binding.locationText.text = it
        }

        viewModel.selectedDate.observe(viewLifecycleOwner) {
            binding.dateText.text = it
        }


        binding.finishBtn.setOnClickListener {
            if (binding.countryName.text.toString().isEmpty() ||
                binding.destinationName.text.toString().isEmpty() ||
                imageUri == null || imageUri == ""||
                binding.destinationDescription.text.toString().isEmpty() ||
                binding.dateText.text.toString().isEmpty()){

                Toast.makeText(requireContext() ,
                    getString(R.string.fill_all_the_fields) , Toast.LENGTH_SHORT).show()

            }
            else{
                val travel = Travel(
                    binding.countryName.text.toString(),
                    binding.destinationName.text.toString(),
                    binding.destinationDescription.text.toString(),
                    imageUri,
                    binding.locationText.text.toString(),
                    binding.dateText.text.toString()
                )

                viewModel.addTravel(travel)

                Toast.makeText(requireContext(), getString(R.string.the_travel_is_added), Toast.LENGTH_SHORT).show()

                resetViewModel()

                findNavController().navigate(R.id.action_addTravelFragment_to_travelsScreenFragment)
            }
        }

        binding.cancelBtn.setOnClickListener {
            resetViewModel()
            findNavController().navigate(R.id.action_addTravelFragment_to_travelsScreenFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun resetViewModel(){
        viewModel.setSelectedImageUri(null)
        viewModel.setSelectedLocation(getString(R.string.no_location_selected))
        viewModel.setSelectedDate("")
    }

    private fun getLocationUpdates(){
        viewModel.location.observe(viewLifecycleOwner){
            binding.locationText.text = it
            viewModel.setSelectedLocation(it)
        }
    }

    private fun recognizerLunch(){
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1)
        }
        recognizeSpeechLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}