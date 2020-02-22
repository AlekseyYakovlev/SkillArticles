package ru.skillbranch.skillarticles.extensions

import android.content.Context
import android.util.TypedValue

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