package ru.skillbranch.skillarticles.viewmodels.base

import android.os.Bundle

interface IViewModelState {
    fun save(outState: Bundle)
    fun restore(savedState:Bundle) : IViewModelState
}