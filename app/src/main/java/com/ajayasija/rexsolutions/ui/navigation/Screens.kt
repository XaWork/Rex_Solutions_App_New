package com.ajayasija.rexsolutions.ui.navigation

sealed class Screens(val route: String){
    object SplashScreen: Screens("splash")
    object LoginScreen: Screens("login")
    object LoginAsScreen: Screens("loginas")
    object RegisterScreen: Screens("register")
    object HomeScreen: Screens("home")
    object ProfileScreen: Screens("profile")
    object InspectionLeadScreen: Screens("inspection")
    object UploadVehicleDataScreen: Screens("uploaddata")

    fun withArgs(vararg args: String):String{
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}