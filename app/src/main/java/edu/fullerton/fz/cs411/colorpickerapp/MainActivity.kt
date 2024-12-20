package edu.fullerton.fz.cs411.colorpickerapp

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MyCounterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ViewModel
        viewModel = ViewModelProvider(this).get(MyCounterViewModel::class.java)

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

        // Set SeekBar range to 0-100
        seekBarRed.max = 100
        seekBarGreen.max = 100
        seekBarBlue.max = 100

        // Adjust layout for current orientation
        adjustLayoutForOrientation()

        viewModel.redValue.observe(this) { updateColorBox(colorBox) }
        viewModel.greenValue.observe(this) { updateColorBox(colorBox) }
        viewModel.blueValue.observe(this) { updateColorBox(colorBox) }

        setupColorControl(
            checkboxRed, seekBarRed, editTextRed,
            { viewModel.redValue.value ?: 0 },
            { viewModel.redValue.value = it }
        )

        setupColorControl(
            checkboxGreen, seekBarGreen, editTextGreen,
            { viewModel.greenValue.value ?: 0 },
            { viewModel.greenValue.value = it }
        )

        setupColorControl(
            checkboxBlue, seekBarBlue, editTextBlue,
            { viewModel.blueValue.value ?: 0 },
            { viewModel.blueValue.value = it }
        )

        resetButton.setOnClickListener {
            viewModel.resetValues()
            resetUI(editTextRed, editTextGreen, editTextBlue, checkboxRed, checkboxGreen, checkboxBlue)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adjustLayoutForOrientation()
    }

    private fun adjustLayoutForOrientation() {
        val constraintLayout = findViewById<ConstraintLayout>(R.id.main)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape: Color box occupies 1/3 of the screen width
            constraintSet.clear(R.id.color_box, ConstraintSet.TOP)
            constraintSet.connect(R.id.color_box, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            constraintSet.connect(R.id.color_box, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.connect(R.id.color_box, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            constraintSet.constrainPercentWidth(R.id.color_box, 0.33f)

            constraintSet.clear(R.id.controls_layout, ConstraintSet.TOP)
            constraintSet.connect(R.id.controls_layout, ConstraintSet.START, R.id.color_box, ConstraintSet.END)
            constraintSet.connect(R.id.controls_layout, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            constraintSet.connect(R.id.controls_layout, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.connect(R.id.controls_layout, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        } else {
            // Portrait: Color box at the top, occupying 30% of the screen height
            constraintSet.clear(R.id.color_box, ConstraintSet.START)
            constraintSet.connect(R.id.color_box, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.connect(R.id.color_box, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            constraintSet.connect(R.id.color_box, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            constraintSet.constrainPercentHeight(R.id.color_box, 0.3f)

            constraintSet.clear(R.id.controls_layout, ConstraintSet.START)
            constraintSet.connect(R.id.controls_layout, ConstraintSet.TOP, R.id.color_box, ConstraintSet.BOTTOM)
            constraintSet.connect(R.id.controls_layout, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            constraintSet.connect(R.id.controls_layout, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            constraintSet.connect(R.id.controls_layout, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        }

        constraintSet.applyTo(constraintLayout)
    }

    private fun setupColorControl(
        checkbox: CheckBox,
        seekBar: SeekBar,
        editText: EditText,
        getValue: () -> Int,
        setValue: (Int) -> Unit
    ) {
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

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s?.toString()?.toIntOrNull() ?: 0
                val clampedValue = input.coerceIn(0, 100)
                setValue(clampedValue)
                seekBar.progress = clampedValue
            }

            override fun afterTextChanged(s: Editable?) {}
        })

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
        val red = (viewModel.redValue.value ?: 0) * 255 / 100
        val green = (viewModel.greenValue.value ?: 0) * 255 / 100
        val blue = (viewModel.blueValue.value ?: 0) * 255 / 100
        colorBox.setBackgroundColor(Color.rgb(red, green, blue))
    }
}

