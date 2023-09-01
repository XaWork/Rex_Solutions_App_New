package com.ajayasija.rexsolutions.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


fun gpsEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

    return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

}


fun getCurrentLocation(context: Context, video: Boolean = false): String? {
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
        if (!video) "Lat: ${
            String.format(
                "%.7f",
                latitude
            )
        }, Lng: ${String.format("%.7f", longitude)}" else "Lat\\: ${
            String.format(
                "%.7f",
                latitude
            )
        }, Lng//: ${String.format("%.7f", longitude)}"
    } else {
        Log.e("location permission", "GPS disabled")
        ""
    }
}


fun currentLocation(
    onLocationReceived: (latitude: Double, longitude: Double) -> Unit,
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
            fusedLocationClient.lastLocation.addOnSuccessListener(
                context as ComponentActivity
            ) { location ->
                location?.let {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    onLocationReceived(latitude, longitude)
                }
            }
        }
    }
}