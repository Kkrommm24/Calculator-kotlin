package com.example.calculator

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import java.util.Stack

class MainActivity : AppCompatActivity() {
    private lateinit var textViewResult: TextView
    private var currentInput: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewResult = findViewById(R.id.text_result)

        val buttons = listOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3,
            R.id.button4, R.id.button5, R.id.button6, R.id.button7,
            R.id.button8, R.id.button9, R.id.button_dot,
            R.id.button_add, R.id.button_subtract, R.id.button_multiply, R.id.button_divide,
            R.id.button_equal, R.id.button_clear, R.id.button_ce, R.id.button_bs
        )

        buttons.forEach { id ->
            findViewById<Button>(id).setOnClickListener { onButtonClick(it) }
        }
    }

    private fun onButtonClick(view: View) {
        when (view.id) {
            R.id.button0 -> addDigit("0")
            R.id.button1 -> addDigit("1")
            R.id.button2 -> addDigit("2")
            R.id.button3 -> addDigit("3")
            R.id.button4 -> addDigit("4")
            R.id.button5 -> addDigit("5")
            R.id.button6 -> addDigit("6")
            R.id.button7 -> addDigit("7")
            R.id.button8 -> addDigit("8")
            R.id.button9 -> addDigit("9")
            R.id.button_dot -> addDot()
            R.id.button_add -> addOperator("+")
            R.id.button_subtract -> addOperator("-")
            R.id.button_multiply -> addOperator("x")
            R.id.button_divide -> addOperator("/")
            R.id.button_equal -> calculateResult()
            R.id.button_clear -> clearAll()
            R.id.button_ce -> clearEntry()
            R.id.button_bs -> backspace()
        }
    }

    private fun addDigit(digit: String) {
        currentInput += digit
        updateResult()
    }

    private fun addDot() {
        if (!currentInput.endsWith(".")) {
            currentInput += "."
            updateResult()
        }
    }

    private fun addOperator(operator: String) {
        if (currentInput.isNotEmpty() && !currentInput.endsWith("+") && !currentInput.endsWith("-")
            && !currentInput.endsWith("x") && !currentInput.endsWith("/")) {
            currentInput += operator
            updateResult()
        }
    }

    private fun calculateResult() {
        try {
            val result = eval(currentInput)
            currentInput = if (result % 1.0 == 0.0) {
                result.toInt().toString()
            } else {
                result.toString()
            }
            updateResult()
        } catch (e: Exception) {
            textViewResult.text = "Error"
        }
    }

    private fun eval(expression: String): Double {
        val tokens = expression.split("(?<=[-+x/])|(?=[-+x/])".toRegex()).filter { it.isNotEmpty() }
        val values = mutableListOf<Double>()
        val operators = mutableListOf<Char>()

        fun applyOperator(operator: Char) {
            val right = values.removeAt(values.size - 1)
            val left = values.removeAt(values.size - 1)
            values.add(when (operator) {
                '+' -> left + right
                '-' -> left - right
                'x' -> left * right
                '/' -> if (right != 0.0) left / right else throw ArithmeticException("Cannot divide by zero")
                else -> 0.0
            })
        }

        for (token in tokens) {
            when {
                token[0].isDigit() -> values.add(token.toDouble())
                token[0] in "+-x/" -> {
                    while (operators.isNotEmpty() && precedence(operators.last()) >= precedence(token[0])) {
                        applyOperator(operators.removeAt(operators.size - 1))
                    }
                    operators.add(token[0])
                }
            }
        }

        while (operators.isNotEmpty()) {
            applyOperator(operators.removeAt(operators.size - 1))
        }

        return values[0]
    }

    private fun precedence(operator: Char): Int {
        return when (operator) {
            '+', '-' -> 1
            'x', '/' -> 2
            else -> 0
        }
    }


    private fun clearAll() {
        currentInput = ""
        updateResult()
    }

    private fun clearEntry() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            updateResult()
        }
    }

    private fun backspace() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            updateResult()
        }
    }

    private fun updateResult() {
        textViewResult.text = currentInput
    }
}


