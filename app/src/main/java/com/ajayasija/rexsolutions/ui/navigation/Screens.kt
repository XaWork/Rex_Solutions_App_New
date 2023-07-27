package com.ajayasija.rexsolutions.ui.navigation

sealed class Screens(val route: String){
    object SplashScreen: Screens("splash")
    object LoginScreen: Screens("login")
    object RegisterScreen: Screens("register")
    object HomeScreen: Screens("home")
    object ProfileScreen: Screens("profile")
    object InspectionLeadScreen: Screens("inspection")
    object UploadVehicleDataScreen: Screens("uploaddata")
}