import kotlinx.serialization.Serializable

sealed class AppRoute {

    @Serializable
    data object Splash : AppRoute()

    @Serializable
    data object AuthGraph

    // Replaced MainGraph with specific role graphs
    @Serializable
    data object MerchantGraph

    @Serializable
    data object AdminGraph

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

    @Serializable
    data object InvoiceCapture : AppRoute()

    // The new screen that will handle sending bytes to AWS
    @Serializable

    @Serializable
    data object InvoiceProcessing : AppRoute()
    data object InvoiceProcessing : AppRoute()
}

