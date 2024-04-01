package com.example.traveljournalapp.ui.fragments

import android.app.Activity
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
import com.example.traveljournalapp.databinding.EditDetailsTravelBinding
import com.example.traveljournalapp.data.model.Travel
import com.example.traveljournalapp.ui.TravelsViewModel
import java.io.ByteArrayOutputStream
import java.util.Calendar

class EditDetailsTravelFragment : Fragment() {

    private var _binding : EditDetailsTravelBinding? = null

    private val binding get() = _binding!!

    val viewModel : TravelsViewModel by activityViewModels()

    var date : String = ""

    var flag: Int? = null

    private var imageUri : String? = null


    val pickImageLauncher : ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            binding.editResultImage.setImageURI(it)
            viewModel.editSetSelectedImageUri(it)
            requireActivity().contentResolver.takePersistableUriPermission( it!! , Intent.FLAG_GRANT_READ_URI_PERMISSION)
            imageUri = it.toString()
            Glide.with(requireContext()).load(imageUri).into(binding.editResultImage)
        }

    val cameraThumbnailLauncher  =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
            binding.editResultImage.setImageBitmap(it)

            val baos = ByteArrayOutputStream()
            it!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val imageData = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

            imageUri = "data:image/png;base64,$imageData"
            viewModel.editSetSelectedImageUri(imageUri.toString().toUri())
        }

    private val locationRequestLauncher : ActivityResultLauncher<String>
            = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            getLocationUpdates()
        }
    }

    val recognizeSpeechLauncher:ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){

                if (flag == 1){
                    binding.editCountryName.setText(it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).toString())
                }
                else if (flag == 2){
                    binding.editDestinationName.setText(it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).toString())
                }
                else{
                    binding.editDestinationDescription.setText(it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).toString())
                }
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = EditDetailsTravelBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.editSelectedImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                imageUri = it.toString()
                Glide.with(requireContext()).load(it).into(binding.editResultImage)
            }
        }

        viewModel.editSelectedLocation.observe(viewLifecycleOwner) { location ->
            binding.editLocationText.text = location
        }

        viewModel.editSelectedDate.observe(viewLifecycleOwner) {
            binding.editDateText.text = it.toString()
        }


        val countryName = arguments?.getString("countryName").toString()
        val destinationName = arguments?.getString("destinationName").toString()
        val destinationDescription = arguments?.getString("destinationDescription").toString()
        val location = arguments?.getString("location").toString()
        imageUri = arguments?.getString("image").toString()
        date = arguments?.getString("date").toString()
        val id = arguments?.getInt("id")


        //viewModel.setSelectedDate(dateNew)
        val editCountryNameTravel = binding.editCountryName
        val editDestinationNameTravel = binding.editDestinationName
        val editDescriptionTravel = binding.editDestinationDescription
        val editImageTravel = binding.editImageBtn
        val editDateTravel = binding.editDateBtn
        val editLocationDestination= binding.editLocationBtn
        val editLocationText = binding.editLocationText


        binding.editCountryVoice.setOnClickListener {
            flag = 1
            recognizerLunch()
        }

        binding.editDestinationVoice.setOnClickListener {
            flag = 2
            recognizerLunch()
        }

        binding.editDescriptionVoice.setOnClickListener {
            flag = 3
            recognizerLunch()
        }


        editImageTravel.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        binding.editCameraBtn.setOnClickListener {
            cameraThumbnailLauncher.launch(null)
        }

        editLocationDestination.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext() , android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
                getLocationUpdates()
            }
            else{
                locationRequestLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        editDateTravel.setOnClickListener {
            val Date = Calendar.getInstance()
            val dateListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val mon = month+1
                date = ("$dayOfMonth/$mon/$year")
                binding.editDateText.text = date
                viewModel.editSetSelectedDate(date)
            }
            val dtd = DatePickerDialog(requireContext() ,dateListener ,Date.get(Calendar.YEAR), Date.get(
                Calendar.MONTH), Date.get(Calendar.DAY_OF_MONTH))

            dtd.show()
        }



        editCountryNameTravel.setText(countryName)
        editDestinationNameTravel.setText(destinationName)
        editDescriptionTravel.setText(destinationDescription)
        binding.editLocationText.text = location
        binding.editDateText.text = date
        Glide.with(requireContext()).load(imageUri).into(binding.editResultImage)

        binding.updateBtn.setOnClickListener {

            val editedTravel = Travel(
                editCountryNameTravel.text.toString(),
                editDestinationNameTravel.text.toString(),
                editDescriptionTravel.text.toString(),
                imageUri,
                binding.editLocationText.text.toString(),
                binding.editDateText.text.toString()
            )
            editedTravel.id = id!!
            viewModel.updateTravel(editedTravel)

            viewModel.setTravel(editedTravel)
            Toast.makeText(requireContext(), getString(R.string.the_travel_updated), Toast.LENGTH_SHORT).show()

            resetViewModel()

            findNavController().navigate(R.id.action_editDetailsTravelFragment_to_detailsTravelFragment)
        }

        binding.editCancelBtn.setOnClickListener {
            resetViewModel()
            findNavController().navigate(R.id.action_editDetailsTravelFragment_to_detailsTravelFragment)
        }
    }

    private fun resetViewModel(){
        viewModel.editSetSelectedImageUri(null)
        viewModel.editSetSelectedLocation(getString(R.string.no_location_selected))
        viewModel.editSetSelectedDate("")
    }

    private fun getLocationUpdates(){
        viewModel.location.observe(viewLifecycleOwner){
            binding.editLocationText.text = it
            viewModel.editSetSelectedLocation(it)
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