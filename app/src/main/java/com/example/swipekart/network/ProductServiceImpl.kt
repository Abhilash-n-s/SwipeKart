package com.example.swipekart.network

import com.example.swipekart.Utils
import com.example.swipekart.model.Product
import com.example.swipekart.model.ProductResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductServiceImpl:ProductService {
    private val productService: ProductService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Utils().BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        productService = retrofit.create(ProductService::class.java)
    }

    override suspend fun getProducts(): List<Product> {
        return productService.getProducts()
    }

    override suspend fun addProduct(
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody,
        files: List<MultipartBody.Part>
    ): ProductResponse {
        return productService.addProduct(productName, productType, price, tax, files)
    }
}