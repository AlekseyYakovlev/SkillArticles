package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginRight
import ru.skillbranch.skillarticles.ui.custom.ArticleSubmenu
import ru.skillbranch.skillarticles.ui.custom.Bottombar


class SubmenuBehavior() : CoordinatorLayout.Behavior<ArticleSubmenu>() {
    constructor(context: Context, attrs: AttributeSet) : this()

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: ArticleSubmenu,
        dependency: View
    ): Boolean {
        return dependency is Bottombar
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: ArticleSubmenu,
        dependency: View
    ): Boolean {

        return if (child.isOpen && dependency.translationY >= 0f) {
            animate(child, dependency)
            true
        } else false
    }

    private fun animate(child: View, dependency: View) {
        val fraction = dependency.translationY / dependency.height
        child.translationX = (child.width + child.marginRight) * fraction
    }
}