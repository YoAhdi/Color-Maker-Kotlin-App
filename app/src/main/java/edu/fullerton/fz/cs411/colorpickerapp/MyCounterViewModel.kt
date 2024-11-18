package edu.fullerton.fz.cs411.colorpickerapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyCounterViewModel : ViewModel() {
    val redValue = MutableLiveData(255)
    val greenValue = MutableLiveData(0)
    val blueValue = MutableLiveData(0)

    fun resetValues() {
        redValue.value = 0
        greenValue.value = 0
        blueValue.value = 0
    }
}

