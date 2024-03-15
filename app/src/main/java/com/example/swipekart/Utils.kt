package com.example.swipekart

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.example.swipekart.db.AppDatabase
import com.example.swipekart.network.ProductService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class Utils {
    val BASEURL="https://app.getswipe.in/api/public/"
    val DBNAME="app_database"
    val CAMERA_PERMISSION_REQUEST_CODE = 1001

    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission(activity: FragmentActivity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

        //Internet connectivity check in Android Q
        val networks = connectivityManager.allNetworks
        var hasInternet = false
        if (networks.isNotEmpty()) {
            for (network in networks) {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                if (networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) hasInternet = true
            }
        }
        return hasInternet
    }

    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://app.getswipe.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun provideApiService(retrofit: Retrofit): ProductService {
        return retrofit.create(ProductService::class.java)
    }

    fun bitmapToFile(bitmap: Bitmap?): List<File> {
        if (bitmap == null) {
            return emptyList()
        }

        val tempFile = try {
            File.createTempFile("image", ".jpg")
        } catch (e: IOException) {
            e.printStackTrace()
            return emptyList()
        }

        try {
            val outputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return emptyList()
        }

        return listOf(tempFile)
    }

}