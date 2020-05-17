package ru.skillbranch.skillarticles.extensions

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Context.dpToPx(dp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics

    )
}

fun Context.dpToIntPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics
    ).toInt()
}


fun Context.attrValue(res: Int):Int{
    val tv = TypedValue()
    return if (theme.resolveAttribute(res,tv,true)) tv.data else -1
}

fun Context.hideKeyboard(view: View){
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken,0)
}

fun Context.showKeyboard(view: View){
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view,0)
}


//val Context.isNetworkAvailable: Boolean
//    get() {
//        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            cm.activeNetwork?.run {
//                val nc = cm.getNetworkCapabilities(this)
//                nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
//                    NetworkCapabilities.TRANSPORT_WIFI
//                )
//            } ?: false
//        } else {
//            cm.activeNetworkInfo?.run { isConnectedOrConnecting } ?: false
//        }
//    }