package com.example.swipekart.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.swipekart.R
import com.example.swipekart.Utils
import com.example.swipekart.databinding.FragmentBottomSheetBinding
import com.example.swipekart.viewmodel.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class BottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetBinding
    private val productViewModel: ProductViewModel by viewModel()
    private var selectedSpinnerItem: String? = null
    private var imageBitmap: Bitmap? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentBottomSheetBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //List of strings for populating in product type spinner
        productViewModel.distinctProductTypes.observe(viewLifecycleOwner) { productTypes ->
            val hintAddedList=listOf(getString(R.string.select_product_type)) + productTypes
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, hintAddedList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerProductType.adapter = adapter
        }

        //insert API response listener
        productViewModel.productsResponse.observe(viewLifecycleOwner){productResponse->
            if (productResponse.success){
                binding.progress.visibility=View.GONE
                binding.btnAdd.isEnabled=true
                Toast.makeText(requireContext(),productResponse.message,Toast.LENGTH_SHORT).show()
                dismiss()
            }else{
                binding.btnAdd.isEnabled=true
                binding.progress.visibility=View.GONE
                Toast.makeText(requireContext(),productResponse.message,Toast.LENGTH_SHORT).show()
            }
        }

        //fetching distinct product types
        productViewModel.loadDistinctProductTypes()

        //camera persmission listener
        productViewModel.cameraPermissionGranted.observe(viewLifecycleOwner) { granted ->
            if (granted) {
                launchCamera()
            } else {
                Toast.makeText(requireContext(), getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show()
                productViewModel.checkCameraPermission(requireContext())
            }
        }


        //capturing image using camera
        binding.btnUploadImage.setOnClickListener {
            productViewModel.checkCameraPermission(requireContext())
        }

        //option to upload image from gallery
        binding.btnUpload.setOnClickListener {
            openGallery()
        }

        //deleting the selected or captured image
        binding.btnDelete.setOnClickListener {
            binding.ivProduct.visibility=View.GONE
            imageBitmap=null
            binding.btnDelete.visibility=View.GONE
        }

        //Product type selection from spinner
        binding.spinnerProductType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    selectedSpinnerItem=null
                } else {
                    selectedSpinnerItem=parent?.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
               selectedSpinnerItem=null
            }
        }

        //uploading the entered data to API
        binding.btnAdd.setOnClickListener {
            binding.progress.visibility=View.VISIBLE
            binding.btnAdd.isEnabled=false
            if (validateFields()){
                productViewModel.addProduct(binding.etName.text.toString(),selectedSpinnerItem.toString(),binding.etPrice.text.toString(),binding.etTax.text.toString(),Utils().bitmapToFile(imageBitmap))
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // Check if the selected image is JPEG or PNG
                val mimeType = requireContext().contentResolver.getType(uri)
                if (mimeType == "image/jpeg" || mimeType == "image/png") {
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    imageBitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    binding.ivProduct.setImageBitmap(imageBitmap)
                    binding.ivProduct.visibility=View.VISIBLE
                    binding.btnDelete.visibility=View.VISIBLE
                } else {
                    binding.ivProduct.visibility=View.GONE
                    binding.btnDelete.visibility=View.GONE
                    Toast.makeText(requireContext(), getString(R.string.invalid_image_format), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.gallery_cancelled), Toast.LENGTH_SHORT).show()
        }
    }


    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(intent)
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageBitmap = data?.extras?.get("data") as Bitmap
            binding.ivProduct.setImageBitmap(imageBitmap)
            binding.ivProduct.visibility=View.VISIBLE
            binding.btnDelete.visibility=View.VISIBLE
        } else {
            binding.ivProduct.visibility=View.GONE
            binding.btnDelete.visibility=View.GONE
            Toast.makeText(requireContext(), getString(R.string.camera_capture_cancelled), Toast.LENGTH_SHORT).show()
        }
    }

    //validation for entered product details
    private fun validateFields(): Boolean {
        var isValid = true

        if (selectedSpinnerItem.isNullOrEmpty()) {
            Toast.makeText(context,getString(R.string.please_select_product_type),Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (binding.etName.text.toString().trim().isEmpty()) {
            binding.etName.error = getString(R.string.please_enter_product_name)
            isValid = false
        }

        if (binding.etTax.text.toString().trim().isEmpty()) {
            binding.etTax.error = getString(R.string.please_enter_tax_amount)
            isValid = false
        }

        if (binding.etPrice.text.toString().trim().isEmpty()) {
            binding.etPrice.error = getString(R.string.please_enter_price)
            isValid = false
        }

        return isValid
    }



}