package com.samduka.dukacred.app.navigation

/**
 * Every navigable destination in the app.

 */
sealed class AppRoute(val route: String) {

    // ── Auth graph ────────────────────────────────────────────────────────────


    data object RolePicker : AppRoute("auth/role_picker")


    data object MerchantSignIn : AppRoute("auth/merchant_sign_in")


    data object AdminSignIn : AppRoute("auth/admin_sign_in")

    // ── Post-auth stubs (built in later sprints) ──────────────────────────────


    data object MerchantHome : AppRoute("merchant/home")


    data object AdminQueue : AppRoute("admin/queue")
    data object SignUp : AppRoute("auth/sign_up")
}