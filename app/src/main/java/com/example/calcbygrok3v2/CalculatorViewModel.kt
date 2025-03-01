package com.example.calcbygrok3v2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Stack

class CalculatorViewModel : ViewModel() {

    private val _display = MutableLiveData("0")
    val display: LiveData<String> get() = _display

    private var currentInput = StringBuilder()
    private var lastResult = 0.0

    fun onButtonClick(input: String) {
        when (input) {
            "CA" -> resetAll()
            "C" -> clearCurrent()
            "<--" -> backspace()
            "=" -> evaluate()
            "%" -> applyPercentage()
            "1/x" -> inverse()
            "+", "-", "×", "/" -> addOperator(input)
            "(", ")" -> addParentheses(input)
            "," -> addDecimal()
            else -> addDigit(input)
        }
        _display.value = currentInput.toString().ifEmpty { "0" }
    }

    private fun resetAll() {
        currentInput.clear()
        lastResult = 0.0
    }

    private fun clearCurrent() {
        currentInput.clear()
    }

    private fun backspace() {
        if (currentInput.isNotEmpty()) currentInput.deleteCharAt(currentInput.length - 1)
    }

    private fun addDigit(digit: String) {
        if (currentInput.toString() == "0") currentInput.clear()
        currentInput.append(digit)
    }

    private fun addDecimal() {
        // Vérifier si le nombre courant contient déjà une virgule
        val parts = currentInput.toString().split(" ")
        if (parts.isEmpty()) {
            currentInput.append("0,")
        } else {
            val lastPart = parts.last()
            if (!lastPart.contains(",")) {
                currentInput.append(",")
            }
        }
    }

    private fun addOperator(op: String) {
        if (currentInput.isEmpty() && op == "-") {
            // Permettre un nombre négatif au début
            currentInput.append(op)
        } else if (currentInput.isNotEmpty()) {
            val lastChar = currentInput.last().toString()
            // Si le dernier caractère est déjà un opérateur, le remplacer
            if (lastChar.isBasicOperator()) {
                currentInput.deleteCharAt(currentInput.length - 1)
                currentInput.append(op)
            } else if (!lastChar.isOperator()) {
                currentInput.append(" $op ")
            }
        }
    }

    private fun addParentheses(par: String) {
        // Pour "(" on peut toujours l'ajouter
        if (par == "(") {
            currentInput.append(par)
        }
        // Pour ")" on vérifie qu'il y a au moins un "(" non fermé
        else if (par == ")") {
            val openCount = currentInput.count { it == '(' }
            val closeCount = currentInput.count { it == ')' }
            if (openCount > closeCount) {
                currentInput.append(par)
            }
        }
    }

    private fun applyPercentage() {
        try {
            val currentValue = currentInput.toString().replace(",", ".").toDoubleOrNull()
            if (currentValue != null) {
                val result = currentValue / 100
                currentInput = StringBuilder(result.toString().replace(".", ","))
            }
        } catch (e: Exception) {
            currentInput = StringBuilder("Erreur")
        }
    }

    private fun inverse() {
        val num = currentInput.toString().replace(",", ".").toDoubleOrNull() ?: return
        if (num == 0.0) {
            currentInput = StringBuilder("Erreur")
            return
        }
        currentInput = StringBuilder((1 / num).toString().replace(".", ","))
    }

    private fun evaluate() {
        try {
            val result = evaluateExpression(currentInput.toString().replace(",", "."))
            lastResult = result
            currentInput = StringBuilder(result.toString().replace(".", ","))
        } catch (e: Exception) {
            currentInput = StringBuilder("Erreur")
        }
    }

    private fun evaluateExpression(expression: String): Double {
        val tokens = expression.split(" ").filter { it.isNotEmpty() }.toMutableList()
        val stack = Stack<Double>()
        val operators = Stack<String>()

        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            when {
                token.isNumeric() -> stack.push(token.toDouble())
                token.isOpenParenthesis() -> operators.push(token)
                token.isCloseParenthesis() -> {
                    while (operators.isNotEmpty() && !operators.peek().isOpenParenthesis()) {
                        applyOperator(stack, operators.pop())
                    }
                    if (operators.isNotEmpty() && operators.peek().isOpenParenthesis()) {
                        operators.pop() // Retirer "("
                    }
                }
                token.isBasicOperator() -> {
                    while (operators.isNotEmpty() &&
                        !operators.peek().isOpenParenthesis() &&
                        hasPrecedence(operators.peek(), token)) {
                        applyOperator(stack, operators.pop())
                    }
                    operators.push(token)
                }
            }
            i++
        }

        while (operators.isNotEmpty()) {
            applyOperator(stack, operators.pop())
        }

        return stack.pop()
    }

    private fun applyOperator(stack: Stack<Double>, op: String) {
        val b = stack.pop()
        val a = stack.pop()
        stack.push(
            when (op) {
                "+" -> a + b
                "-" -> a - b
                "×" -> a * b
                "/" -> if (b == 0.0) throw ArithmeticException() else a / b
                else -> 0.0
            }
        )
    }

    private fun hasPrecedence(op1: String, op2: String): Boolean {
        return (op1 in listOf("×", "/") && op2 in listOf("+", "-"))
    }

    private fun String.isNumeric() = this.toDoubleOrNull() != null

    private fun String.isBasicOperator() = this in listOf("+", "-", "×", "/")

    private fun String.isOperator() = this.isBasicOperator() || this in listOf("(", ")")

    private fun String.isOpenParenthesis() = this == "("

    private fun String.isCloseParenthesis() = this == ")"
}