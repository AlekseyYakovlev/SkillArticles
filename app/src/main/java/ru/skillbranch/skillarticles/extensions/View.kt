package ru.skillbranch.skillarticles.extensions

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.navigation.NavDestination
import com.google.android.material.bottomnavigation.BottomNavigationView

fun View.setMarginOptionally(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom
) {
    if (layoutParams is ViewGroup.LayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(left, top, right, bottom)
        requestLayout()
    }
}

fun View.setPaddingOptionally(
    left: Int = paddingLeft,
    right: Int = paddingRight,
    top: Int = paddingTop,
    bottom: Int = paddingBottom
) {
    setPadding(left, top, right, bottom)
}

fun View.hideKeyboard() {
    if (isFocused) {
        val inputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}

private fun View.selectDestination(destination: NavDestination) {
    if(this is BottomNavigationView){}

}