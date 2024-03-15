package com.example.swipekart.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.swipekart.model.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    suspend fun getAllProducts():List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products:List<Product>)

    @Query("SELECT DISTINCT product_type FROM products")
    suspend fun getDistinctProductTypes(): List<String>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getCount(): Int

    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Query("SELECT * FROM products WHERE product_name LIKE '%' || :query || '%'")
    suspend fun searchProducts(query: String): List<Product>
}