package com.example.swipekart.repository

import com.example.swipekart.model.Product
import com.example.swipekart.model.ProductResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ProductRepository {
    suspend fun getProducts(): List<Product>
    suspend fun insertProducts(productList:List<Product>)
    suspend fun getProductFromRemote():List<Product>
    suspend fun getDistinctProductTypes(): List<String>
    suspend fun addNewProduct(
        productNamePart: RequestBody,
        productTypePart: RequestBody,
        pricePart: RequestBody,
        taxPart: RequestBody,
        filesParts: List<MultipartBody.Part>
    ): ProductResponse
    suspend fun searchProducts(query: String): List<Product>
}