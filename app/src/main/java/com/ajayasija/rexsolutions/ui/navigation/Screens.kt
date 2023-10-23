package com.ajayasija.rexsolutions.ui.navigation

sealed class Screens(val route: String){
    data object SplashScreen: Screens("splash")
    data object LoginScreen: Screens("login")
    data object LoginAsScreen: Screens("loginas")
    data object RegisterScreen: Screens("register")
    data object HomeScreen: Screens("home")
    data object ProfileScreen: Screens("profile")
    data object InspectionLeadScreen: Screens("inspection")
    data object UploadVehicleDataScreen: Screens("uploaddata")

    fun withArgs(vararg args: String):String{
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}