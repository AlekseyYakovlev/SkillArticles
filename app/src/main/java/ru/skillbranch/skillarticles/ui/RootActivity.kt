package ru.skillbranch.skillarticles.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavDestination
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.base.BaseActivity
import ru.skillbranch.skillarticles.ui.custom.Bottombar
import ru.skillbranch.skillarticles.viewmodels.RootViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify

class RootActivity : BaseActivity<RootViewModel>() {

    override val layout = R.layout.activity_root
    public override val viewModel: RootViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //top level destination
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_articles,
                R.id.nav_bookmarks,
                R.id.nav_transcriptions,
                R.id.nav_profile
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setOnNavigationItemSelectedListener {
            viewModel.navigate(NavigationCommand.To(it.itemId))
            true
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            nav_view.selectDestination(destination)

            //if (destination.id == R.id.nav_auth) nav_view.selectItem(arguments?.get("private_destination") as Int?)

            if (viewModel.currentState.isAuth && destination.id == R.id.nav_auth) {
                controller.popBackStack()
                val privateDestination = arguments?.get("private_destination") as Int?
                privateDestination?.let { controller.navigate(it) }
            }

        }
    }

    override fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(container, notify.message, Snackbar.LENGTH_LONG)
        snackbar.anchorView = findViewById<Bottombar>(R.id.bottombar) ?: nav_view

        when (notify) {
            is Notify.ActionMessage -> {
                snackbar.setActionTextColor(getColor(R.color.color_accent_dark))
                snackbar.setAction(notify.actionLabel) { notify.actionHandler.invoke() }
            }
            is Notify.ErrorMessage -> {
                with(snackbar) {
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel) { notify.errHandler?.invoke() }
                }
            }
        }

        snackbar.show()
    }

    override fun subscribeOnState(state: IViewModelState) {
        // viewModel.
    }


}

fun BottomNavigationView.selectDestination(destination: NavDestination) {
    menu.findItem(destination.id)?.isChecked = true
}

//private fun BottomNavigationView.selectDestination(destination: NavDestination) {
//    val menu: Menu = this.menu
//    var h = 0
//    val size = menu.size()
//    while (h < size) {
//        val item = menu.getItem(h)
//        var currentDestination: NavDestination? = destination
//        while (currentDestination!!.id != item.itemId && currentDestination.parent != null) {
//            currentDestination = currentDestination.parent
//        }
//        if (currentDestination.id == item.itemId) {
//            item.isChecked = true
//        }
//        h++
//    }
//}

