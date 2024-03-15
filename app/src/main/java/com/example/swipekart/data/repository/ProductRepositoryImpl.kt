package com.example.swipekart.repository

import com.example.swipekart.db.ProductDao
import com.example.swipekart.model.Product
import com.example.swipekart.model.ProductResponse
import com.example.swipekart.network.ProductServiceImpl
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProductRepositoryImpl(private val productDao: ProductDao) : ProductRepository {


    override suspend fun getProducts(): List<Product> {
        return productDao.getAllProducts()
    }

    override suspend fun insertProducts(productList: List<Product>) {
        productDao.insertProducts(productList)
    }

    override suspend fun getProductFromRemote(): List<Product> {
        val products= ProductServiceImpl().getProducts()
        if (productDao.getCount()>0){
            productDao.deleteAll()
        }
        productDao.insertProducts(products)
        return productDao.getAllProducts()
    }

    override suspend fun getDistinctProductTypes(): List<String> {
        return productDao.getDistinctProductTypes()
    }

    override suspend fun addNewProduct(
        productNamePart: RequestBody,
        productTypePart: RequestBody,
        pricePart: RequestBody,
        taxPart: RequestBody,
        filesParts: List<MultipartBody.Part>
    ): ProductResponse {
        return ProductServiceImpl().addProduct(productNamePart,productTypePart,pricePart,taxPart,filesParts)
    }

    override suspend fun searchProducts(query: String): List<Product> {
        return productDao.searchProducts(query)
    }
}