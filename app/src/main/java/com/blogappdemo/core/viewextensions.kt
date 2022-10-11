package com.blogappdemo.core

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible

//ocultar view
fun View.hide() {
    this.isVisible = false
}

//mostrar view
fun View.show() {
    this.isVisible = true
}

//ocultar teclado
fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}