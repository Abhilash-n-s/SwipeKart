package com.example.swipekart.network

import com.example.swipekart.model.Product
import com.example.swipekart.model.ProductResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProductService {
    @GET("get")
    suspend fun getProducts(): List<Product>

    @Multipart
    @POST("add")
    suspend fun addProduct(
        @Part("product_name") productName:RequestBody,
        @Part("product_type") productType:RequestBody,
        @Part("price") price:RequestBody,
        @Part("tax") tax:RequestBody,
        @Part files:List<MultipartBody.Part>
    ): ProductResponse
}