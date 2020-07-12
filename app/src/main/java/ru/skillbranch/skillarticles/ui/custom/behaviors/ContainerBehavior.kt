package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

class ContainerBehavior() : AppBarLayout.ScrollingViewBehavior() {
    constructor(context: Context, attributeSet: AttributeSet) : this()

    override fun onMeasureChild(
        parent: CoordinatorLayout,
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ): Boolean {
        //if child not scrolling measure manually
        if(child is FragmentContainerView && !child.children.first().isNestedScrollingEnabled){
            val appbar = parent.children.find { it is AppBarLayout }
            val ah = appbar?.measuredHeight ?: 0
            val bottombar = parent.children.find { it is BottomNavigationView }
            val bh = if(bottombar?.isVisible == true) bottombar.measuredHeight else 0
            val height = View.MeasureSpec.getSize(parentHeightMeasureSpec) - ah - bh
            parent.onMeasureChild(child,
                parentWidthMeasureSpec, widthUsed,
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY), heightUsed)
            return true
        }
        //if scrolling measure parent
        return super.onMeasureChild(
            parent,
            child,
            parentWidthMeasureSpec,
            widthUsed,
            parentHeightMeasureSpec,
            heightUsed
        )
    }
}