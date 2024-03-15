package com.example.swipekart.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipekart.Utils
import com.example.swipekart.model.Product
import com.example.swipekart.model.ProductResponse
import com.example.swipekart.repository.ProductRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products
    private val _distinctProductTypes = MutableLiveData<List<String>>()
    val distinctProductTypes: LiveData<List<String>> = _distinctProductTypes
    private val _cameraPermissionGranted = MutableLiveData<Boolean>()
    val cameraPermissionGranted: LiveData<Boolean> = _cameraPermissionGranted
    private val _productsResponse = MutableLiveData<ProductResponse>()
    val productsResponse: LiveData<ProductResponse> get() = _productsResponse
    private val _searchResults = MutableLiveData<List<Product>>()
    val searchResults: LiveData<List<Product>> = _searchResults

    fun loadProducts() {
        viewModelScope.launch {
            _products.value = productRepository.getProductFromRemote()
        }
    }

    fun loadOfflineData() {
        viewModelScope.launch {
            _products.value = productRepository.getProducts()
        }
    }


    fun loadDistinctProductTypes() {
        viewModelScope.launch {
            _distinctProductTypes.value = productRepository.getDistinctProductTypes()
        }
    }

    fun addProduct(productName: String, productType: String, price: String, tax: String, files: List<File>) {
        viewModelScope.launch {
            val productNamePart = productName.toRequestBody("text/plain".toMediaTypeOrNull())
            val productTypePart = productType.toRequestBody("text/plain".toMediaTypeOrNull())
            val pricePart = price.toRequestBody("text/plain".toMediaTypeOrNull())
            val taxPart = tax.toRequestBody("text/plain".toMediaTypeOrNull())
            val filesParts = files.map {
                val fileReqBody = it.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("files[]", it.name, fileReqBody)
            }

            viewModelScope.launch {
                _productsResponse.value = productRepository.addNewProduct(productNamePart,productTypePart,pricePart,taxPart,filesParts)
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _searchResults.value = productRepository.searchProducts(query)
        }
    }

    fun checkCameraPermission(context: Context) {
        if (Utils().hasCameraPermission(context)) {
            _cameraPermissionGranted.value = true
        } else {
            Utils().requestCameraPermission(context as FragmentActivity)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == Utils().CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                _cameraPermissionGranted.value = true
            } else {
                _cameraPermissionGranted.value = false
            }
        }
    }
}