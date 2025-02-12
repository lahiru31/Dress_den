package com.example.dress_den.util

import android.content.Context
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

object NavigationUtils {

    fun findNavController(activity: FragmentActivity, @IdRes navHostId: Int): NavController {
        val navHostFragment = activity.supportFragmentManager
            .findFragmentById(navHostId) as NavHostFragment
        return navHostFragment.navController
    }

    fun Fragment.getNavigationResult(key: String = "result") =
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Any>(key)

    fun Fragment.setNavigationResult(result: Any, key: String = "result") {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
    }

    fun NavController.navigateSafely(@IdRes resId: Int, args: Bundle? = null) {
        try {
            navigate(resId, args)
        } catch (e: Exception) {
            LogUtils.e("Navigation", "Failed to navigate to $resId", e)
        }
    }

    fun NavController.navigateSafely(
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = null
    ) {
        try {
            navigate(resId, args, navOptions)
        } catch (e: Exception) {
            LogUtils.e("Navigation", "Failed to navigate to $resId", e)
        }
    }

    fun NavController.navigateSafely(
        navDirections: NavDirections,
        navOptions: NavOptions? = null
    ) {
        try {
            navigate(navDirections, navOptions)
        } catch (e: Exception) {
            LogUtils.e("Navigation", "Failed to navigate using directions", e)
        }
    }

    fun Fragment.safeNavigate(@IdRes resId: Int, args: Bundle? = null) {
        try {
            findNavController().navigate(resId, args)
        } catch (e: Exception) {
            LogUtils.e("Navigation", "Failed to navigate to $resId", e)
        }
    }

    fun Fragment.safeNavigate(directions: NavDirections) {
        try {
            findNavController().navigate(directions)
        } catch (e: Exception) {
            LogUtils.e("Navigation", "Failed to navigate using directions", e)
        }
    }

    fun Fragment.safePopBackStack(): Boolean {
        return try {
            findNavController().popBackStack()
        } catch (e: Exception) {
            LogUtils.e("Navigation", "Failed to pop back stack", e)
            false
        }
    }

    fun Fragment.safePopBackStack(@IdRes destinationId: Int, inclusive: Boolean): Boolean {
        return try {
            findNavController().popBackStack(destinationId, inclusive)
        } catch (e: Exception) {
            LogUtils.e("Navigation", "Failed to pop back stack to $destinationId", e)
            false
        }
    }

    fun NavController.clearBackStack(@IdRes destinationId: Int) {
        try {
            popBackStack(destinationId, false)
        } catch (e: Exception) {
            LogUtils.e("Navigation", "Failed to clear back stack", e)
        }
    }

    fun createNavOptions(
        @IdRes popUpTo: Int? = null,
        inclusive: Boolean = false,
        singleTop: Boolean = false,
        anim: NavAnim? = null
    ): NavOptions {
        return NavOptions.Builder().apply {
            popUpTo?.let { setPopUpTo(it, inclusive) }
            setLaunchSingleTop(singleTop)
            anim?.let {
                setEnterAnim(it.enter)
                setExitAnim(it.exit)
                setPopEnterAnim(it.popEnter)
                setPopExitAnim(it.popExit)
            }
        }.build()
    }

    data class NavAnim(
        val enter: Int,
        val exit: Int,
        val popEnter: Int,
        val popExit: Int
    )

    fun isDestinationInBackStack(navController: NavController, @IdRes destinationId: Int): Boolean {
        return try {
            navController.getBackStackEntry(destinationId)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentDestination(navController: NavController): NavDestination? {
        return navController.currentDestination
    }

    fun isCurrentDestination(navController: NavController, @IdRes destinationId: Int): Boolean {
        return navController.currentDestination?.id == destinationId
    }

    fun getDeepLinkRequest(context: Context, @IdRes destinationId: Int, args: Bundle? = null): NavDeepLinkRequest {
        val navController = findNavController(context as FragmentActivity, destinationId)
        val request = NavDeepLinkRequest.Builder
            .fromNavGraph(navController.graph, destinationId)
        args?.let {
            it.keySet().forEach { key ->
                request.addArgument(key, it.get(key))
            }
        }
        return request.build()
    }

    fun handleDeepLink(activity: FragmentActivity, @IdRes navHostId: Int) {
        val navController = findNavController(activity, navHostId)
        activity.intent?.let { intent ->
            navController.handleDeepLink(intent)
        }
    }

    sealed class NavigationEvent {
        data class ToDestination(@IdRes val destinationId: Int, val args: Bundle? = null) : NavigationEvent()
        data class ToDirection(val directions: NavDirections) : NavigationEvent()
        object Back : NavigationEvent()
        data class BackTo(@IdRes val destinationId: Int, val inclusive: Boolean = false) : NavigationEvent()
        object BackToRoot : NavigationEvent()
    }

    class NavigationException(message: String, cause: Throwable? = null) : Exception(message, cause)
}
