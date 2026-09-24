package com.example.lab2counter

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var counter = 0
    private var step = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val counterText = findViewById<TextView>(R.id.counterText)
        val addButton = findViewById<Button>(R.id.addButton)
        val subtractButton = findViewById<Button>(R.id.subtractButton)
        val resetButton = findViewById<Button>(R.id.resetButton)
        val stepButton = findViewById<Button>(R.id.stepButton)

        // Add button
        addButton.setOnClickListener {
            counter += step
            counterText.text = counter.toString()
        }

        // Subtract button
        subtractButton.setOnClickListener {
            counter -= step
            counterText.text = counter.toString()
        }

        // Reset button
        resetButton.setOnClickListener {
            counter = 0
            step = 1
            counterText.text = counter.toString()
        }

        // Step button
        stepButton.setOnClickListener {
            step = 2
        }
    }
}