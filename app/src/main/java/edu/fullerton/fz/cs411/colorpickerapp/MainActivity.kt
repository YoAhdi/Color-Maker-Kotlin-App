package edu.fullerton.fz.cs411.colorpickerapp

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MyCounterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(MyCounterViewModel::class.java)

        // View bindings
        val colorBox: View = findViewById(R.id.color_box)
        val resetButton: Button = findViewById(R.id.reset_button)

        val checkboxRed: CheckBox = findViewById(R.id.checkbox_red)
        val seekBarRed: SeekBar = findViewById(R.id.seekbar_red)
        val editTextRed: EditText = findViewById(R.id.edittext_red)

        val checkboxGreen: CheckBox = findViewById(R.id.checkbox_green)
        val seekBarGreen: SeekBar = findViewById(R.id.seekbar_green)
        val editTextGreen: EditText = findViewById(R.id.edittext_green)

        val checkboxBlue: CheckBox = findViewById(R.id.checkbox_blue)
        val seekBarBlue: SeekBar = findViewById(R.id.seekbar_blue)
        val editTextBlue: EditText = findViewById(R.id.edittext_blue)

        // Restore persistent data
        restoreFromPreferences()

        // Observe LiveData changes and update the color box
        viewModel.redValue.observe(this) { updateColorBox(colorBox) }
        viewModel.greenValue.observe(this) { updateColorBox(colorBox) }
        viewModel.blueValue.observe(this) { updateColorBox(colorBox) }

        // Setup controls for red
        setupColorControl(
            checkboxRed, seekBarRed, editTextRed,
            { viewModel.redValue.value ?: 0 },
            { viewModel.redValue.value = it }
        )

        // Setup controls for green
        setupColorControl(
            checkboxGreen, seekBarGreen, editTextGreen,
            { viewModel.greenValue.value ?: 0 },
            { viewModel.greenValue.value = it }
        )

        // Setup controls for blue
        setupColorControl(
            checkboxBlue, seekBarBlue, editTextBlue,
            { viewModel.blueValue.value ?: 0 },
            { viewModel.blueValue.value = it }
        )

        // Reset button functionality
        resetButton.setOnClickListener {
            viewModel.resetValues()
            resetUI(editTextRed, editTextGreen, editTextBlue, checkboxRed, checkboxGreen, checkboxBlue)
        }
    }

    private fun setupColorControl(
        checkbox: CheckBox,
        seekBar: SeekBar,
        editText: EditText,
        getValue: () -> Int,
        setValue: (Int) -> Unit
    ) {
        // Sync SeekBar with EditText
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    setValue(progress)
                    editText.setText(progress.toString())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Sync EditText with SeekBar
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s?.toString()?.toIntOrNull() ?: 0
                val clampedValue = input.coerceIn(0, 255)
                setValue(clampedValue)
                seekBar.progress = clampedValue
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Enable/disable controls based on checkbox state
        checkbox.setOnCheckedChangeListener { _, isChecked ->
            seekBar.isEnabled = isChecked
            editText.isEnabled = isChecked

            if (!isChecked) {
                setValue(0)
                seekBar.progress = 0
                editText.setText("0")
            } else {
                seekBar.progress = getValue()
                editText.setText(getValue().toString())
            }
        }
    }

    private fun resetUI(
        editTextRed: EditText,
        editTextGreen: EditText,
        editTextBlue: EditText,
        checkboxRed: CheckBox,
        checkboxGreen: CheckBox,
        checkboxBlue: CheckBox
    ) {
        editTextRed.setText("0")
        editTextGreen.setText("0")
        editTextBlue.setText("0")
        checkboxRed.isChecked = false
        checkboxGreen.isChecked = false
        checkboxBlue.isChecked = false
    }

    private fun updateColorBox(colorBox: View) {
        val red = viewModel.redValue.value ?: 0
        val green = viewModel.greenValue.value ?: 0
        val blue = viewModel.blueValue.value ?: 0
        colorBox.setBackgroundColor(Color.rgb(red, green, blue))
    }

    private fun saveToPreferences() {
        val sharedPreferences = getSharedPreferences("ColorPickerApp", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Save RGB values
        editor.putInt("redValue", viewModel.redValue.value ?: 0)
        editor.putInt("greenValue", viewModel.greenValue.value ?: 0)
        editor.putInt("blueValue", viewModel.blueValue.value ?: 0)

        // Save checkbox states
        editor.putBoolean("redChecked", findViewById<CheckBox>(R.id.checkbox_red).isChecked)
        editor.putBoolean("greenChecked", findViewById<CheckBox>(R.id.checkbox_green).isChecked)
        editor.putBoolean("blueChecked", findViewById<CheckBox>(R.id.checkbox_blue).isChecked)

        editor.apply()
    }

    private fun restoreFromPreferences() {
        val sharedPreferences = getSharedPreferences("ColorPickerApp", MODE_PRIVATE)

        // Restore RGB values
        viewModel.redValue.value = sharedPreferences.getInt("redValue", 0)
        viewModel.greenValue.value = sharedPreferences.getInt("greenValue", 0)
        viewModel.blueValue.value = sharedPreferences.getInt("blueValue", 0)

        // Restore checkbox states
        findViewById<CheckBox>(R.id.checkbox_red).isChecked =
            sharedPreferences.getBoolean("redChecked", false)
        findViewById<CheckBox>(R.id.checkbox_green).isChecked =
            sharedPreferences.getBoolean("greenChecked", false)
        findViewById<CheckBox>(R.id.checkbox_blue).isChecked =
            sharedPreferences.getBoolean("blueChecked", false)
    }

    override fun onPause() {
        super.onPause()
        saveToPreferences() // Save all data when the activity goes to the background
    }
}


