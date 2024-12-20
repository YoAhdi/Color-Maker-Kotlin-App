package edu.fullerton.fz.cs411.colorpickerapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyCounterViewModel : ViewModel() {
    // Default values adjusted to the range 0-100
    val redValue = MutableLiveData(100) // Default to maximum red intensity (100)
    val greenValue = MutableLiveData(0) // Default to no green intensity
    val blueValue = MutableLiveData(0) // Default to no blue intensity

    // Resets all values to 0
    fun resetValues() {
        redValue.value = 0
        greenValue.value = 0
        blueValue.value = 0
    }
}

