package ru.skillbranch.skillarticles.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.activity_root.view.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify

abstract class BaseActivity<T : BaseViewModel<out IViewModelState>> : AppCompatActivity() {
    protected abstract val viewModel: T
    protected abstract val layout: Int
    lateinit var navController: NavController

    val toolbarBuilder = ToolbarBuilder()
    val bottombarBuilder = BottombarBuilder()

    //set listeners, tuning views
    abstract fun subscribeOnState(state: IViewModelState)

    abstract fun renderNotification(notify: Notify)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        setSupportActionBar(toolbar)
        viewModel.observeState(this) { subscribeOnState(it) }
        viewModel.observeNotifications(this) { renderNotification(it) }
        viewModel.observeNavigation(this) { subscribeOnNavigation(it) }

        navController = findNavController(R.id.nav_host_fragment)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveState()
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel.restoreState()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun subscribeOnNavigation(command: NavigationCommand) {
        when (command) {
            is NavigationCommand.To -> {
                navController.navigate(
                    command.destination,
                    command.args,
                    command.options,
                    command.extras
                )
            }

            is NavigationCommand.FinishLogin -> {
                navController.navigate(R.id.finish_login)
                if (command.privateDestination != null) navController.navigate(command.privateDestination)
            }

            is NavigationCommand.StartLogin -> {
                navController.navigate(
                    R.id.start_login,
                    bundleOf("private_destination" to (command.privateDestination ?: -1))
                )
            }
        }
    }
}

class ToolbarBuilder() {
    var title: String? = null
    var subtitle: String? = null
    var logo: String? = null
    var visibility: Boolean = true
    val items: MutableList<MenuItemHolder> = mutableListOf()

    fun setTitle(title: String): ToolbarBuilder {
        this.title = title
        return this
    }

    fun setSubtitle(subtitle: String): ToolbarBuilder {
        this.subtitle = subtitle
        return this
    }

    fun setLogo(logo: String): ToolbarBuilder {
        this.logo = logo
        return this
    }

    fun addMenuItem(item: MenuItemHolder): ToolbarBuilder {
        this.items.add(item)
        return this
    }

    fun invalidate(): ToolbarBuilder {
        this.title = null
        this.subtitle = null
        this.logo = null
        this.visibility = true
        this.items.clear()
        return this
    }

    fun prepare(prepareFn: (ToolbarBuilder.() -> Unit)?): ToolbarBuilder {
        prepareFn?.invoke(this)
        return this
    }

    fun build(context: FragmentActivity) {

        //show appbar if hidden due to scroll behavior
        context.appbar.setExpanded(true, true)

        with(context.toolbar) {
            if (this@ToolbarBuilder.title != null) title = this@ToolbarBuilder.title
            subtitle = this@ToolbarBuilder.subtitle
            if (this@ToolbarBuilder.logo != null) {
                val logoSize = context.dpToIntPx(40)
                val logoMargin = context.dpToIntPx(16)
                val logoPlaceholder = getDrawable(context, R.drawable.logo_placeholder)

                logo = logoPlaceholder
                toolbar.logoDescription = "logo"
                toolbar.doOnNextLayout {
                    val logo =children.filter { it.contentDescription == "logo" }.first() as ImageView
                    logo.scaleType = ImageView.ScaleType.CENTER_CROP
                    (logo.layoutParams as? Toolbar.LayoutParams)?.let {
                        it.width = logoSize
                        it.height = logoSize
                        it.marginEnd = logoMargin
                        logo.layoutParams = it
                    }

                    Glide.with(context)
                        .load(this@ToolbarBuilder.logo)
                        .apply(circleCropTransform())
                        .override(logoSize)
                        .into(logo)
                }
            } else {
                logo = null
            }
        }
    }
}

data class MenuItemHolder(
    val title: String,
    val menuId: Int,
    val icon: Int,
    val actionViewLayout: Int? = null,
    val clickListener: ((MenuItem) -> Unit)? = null
)

class BottombarBuilder() {
    private var visible: Boolean = true
    private val views = mutableListOf<Int>()
    private val tempViews = mutableListOf<Int>()

    fun addView(layoutId: Int): BottombarBuilder {
        views.add(layoutId)
        return this
    }

    fun setVisibility(isVisible: Boolean): BottombarBuilder {
        visible = isVisible
        return this
    }

    fun prepare(prepareFn: (BottombarBuilder.() -> Unit)?): BottombarBuilder {
        prepareFn?.invoke(this)
        return this
    }

    fun invalidate(): BottombarBuilder {
        visible = true
        views.clear()
        return this
    }

    fun build(context: FragmentActivity) {

        //remove temp views
        if (tempViews.isNotEmpty()) {
            tempViews.forEach {
                val view = context.container.findViewById<View>(it)
                context.container.removeView(view)
            }

            tempViews.clear()
//            context.clearFindViewByIdCache()
        }

        //add new bottom bar views
        if (views.isNotEmpty()) {
            val inflater = LayoutInflater.from(context)
            views.forEach {
                val view = inflater.inflate(it, context.container, false)
                context.container.addView(view)
                tempViews.add(view.id)
            }
        }

        with(context.nav_view) {
            isVisible = visible
            //show bottombar if hidden due to scroll behavior
            ((layoutParams as CoordinatorLayout.LayoutParams).behavior as HideBottomViewOnScrollBehavior)
                .slideUp(this)
        }

    }

}