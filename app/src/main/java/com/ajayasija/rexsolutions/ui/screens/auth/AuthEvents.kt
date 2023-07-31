package a2a.logistic.app.presentation.login

sealed class AuthEvents {
    data class Login(val username: String, val password: String) : AuthEvents()
}
