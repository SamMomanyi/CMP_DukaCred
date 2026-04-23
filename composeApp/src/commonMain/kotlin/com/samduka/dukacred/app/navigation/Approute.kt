import kotlinx.serialization.Serializable

sealed class AppRoute {

    @Serializable
    data object AuthGraph

    @Serializable
    data object MainGraph

    @Serializable
    data object RolePicker : AppRoute()

    @Serializable
    data object MerchantSignIn : AppRoute()

    @Serializable
    data object AdminSignIn : AppRoute()

    @Serializable
    data object SignUp : AppRoute()

    @Serializable
    data object MerchantHome : AppRoute()

    @Serializable
    data object AdminQueue : AppRoute()
}