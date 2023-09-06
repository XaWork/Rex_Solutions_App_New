package com.ajayasija.rexsolutions.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale


fun gpsEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

    return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

}


fun getCurrentLocation(context: Context): Location? {
    // Check location permission
    val permissionList = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val accessCoarseLocationGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val accessFineLocationGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return if (accessCoarseLocationGranted && accessFineLocationGranted && gpsEnabled(context)) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        val location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val latitude = location?.latitude
        val longitude = location?.longitude
        location
        /* if (!video) "Lat: ${
             String.format(
                 "%.7f",
                 latitude
             )
         }, Lng: ${String.format("%.7f", longitude)}"
         else "Lat\\: ${
             String.format(
                 "%.7f",
                 latitude
             )
         }, Lng//: ${String.format("%.7f", longitude)}"*/
    } else {
        Log.e("location permission", "GPS disabled")
        null
    }
}


fun currentLocation(
    onGetLocation: (lat: String, lng: String) -> Unit,
    context: Context
) {
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        if (gpsEnabled(context)) {
            var location: Location?
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                location = task.result
                val geocoder = Geocoder(context, Locale.getDefault())
                Log.e(
                    "location", "complete to get location ${
                        location!!.longitude
                    }\n${location?.latitude}"
                )
                if (location != null) {
                    onGetLocation(location?.latitude.toString(), location?.longitude.toString())
                    //onGetLocation("location?.latitude.toString()", "location?.longitude.toString()")
                }
            }
                .addOnSuccessListener {
                    location = it
                    Log.e("location", "success to get location $location")
                    if (location != null) {
                        onGetLocation(location?.latitude.toString(), location?.longitude.toString())
                        //onGetLocation("location?.latitude.toString()", "location?.longitude.toString()")
                    }
                }
                .addOnFailureListener {
                    Log.e("location", "Failed to get location $it")
                }
        }
    }
}