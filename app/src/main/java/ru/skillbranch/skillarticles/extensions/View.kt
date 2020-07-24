package ru.skillbranch.skillarticles.extensions

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.core.view.*
import androidx.navigation.NavDestination
import com.google.android.material.bottomnavigation.BottomNavigationView

fun View.setPaddingOptionally(
    left: Int = paddingLeft,
    top: Int = paddingTop,
    right: Int = paddingRight,
    bottom: Int = paddingBottom
) {
    setPadding(left, top, right, bottom)
}

fun BottomNavigationView.selectDestination(destination: NavDestination) {
    for (item in menu.iterator()) {
        if (matchDestination(destination, item.itemId)) {
            item.isChecked = true
        }
    }
}

fun View.setMarginOptionally(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom
) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.run {
        leftMargin = left
        rightMargin = right
        topMargin = top
        bottomMargin = bottom
    }
}

fun BottomNavigationView.selectItem(itemId: Int?) {
    itemId ?: return
    for (item in menu.iterator()) {
        if (item.itemId == itemId) {
            item.isChecked = true
            break
        }
    }
}

fun View.hideKeyboard() {
    if (isFocused) {
        val inputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}

fun View.showKeyboard() {
    if (isFocused) {
        val inputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, 0)
    }
}

fun matchDestination(destination: NavDestination, @IdRes destId: Int): Boolean {
    var currentDestination: NavDestination? = destination
    while (currentDestination!!.id != destId && currentDestination.parent != null) {
        currentDestination = currentDestination.parent
    }
    return currentDestination.id == destId
}