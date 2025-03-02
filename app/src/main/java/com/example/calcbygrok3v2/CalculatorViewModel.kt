package com.example.calcbygrok3v2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Stack
import android.util.Log

class CalculatorViewModel : ViewModel() {

    private val _display = MutableLiveData("0")
    val display: LiveData<String> get() = _display

    private var currentInput = StringBuilder()
    private var lastResult = 0.0

    fun onButtonClick(input: String) {
        try {
            when (input) {
                "CA" -> resetAll()
                "C" -> clearCurrent()
                "<--" -> backspace()
                "=" -> evaluate()
                "%" -> applyPercentage()
                "1/x" -> inverse()
                "+", "-", "×", "/" -> addOperator(input)
                "(", ")" -> addParentheses(input)
                "." -> addDecimal()
                else -> addDigit(input)
            }
            _display.value = currentInput.toString().ifEmpty { "0" }
        } catch (e: Exception) {
            // Log l'erreur et affiche un message d'erreur simple
            Log.e("CalculatorViewModel", "Erreur: ${e.message}", e)
            currentInput = StringBuilder("Erreur")
            _display.value = "Erreur"
        }
    }

    private fun addOperator(op: String) {
        if (currentInput.isEmpty() && op == "-") {
            // Permettre un nombre négatif au début
            currentInput.append(op)
        } else if (currentInput.isNotEmpty()) {
            val lastChar = currentInput.last()

            // Si le dernier caractère est un opérateur, le remplacer
            if (lastChar in "+-×/") {
                currentInput.deleteCharAt(currentInput.length - 1)
                currentInput.append(op)
            } else if (lastChar != '(' || op == "-") {
                // Après une parenthèse ouvrante, on accepte '-' pour les nombres négatifs
                currentInput.append(op)
            }
        }
    }

    private fun addParentheses(par: String) {
        if (par == "(") {
            // Avant d'ajouter une parenthèse ouvrante après un chiffre, ajouter un ×
            if (currentInput.isNotEmpty() && (currentInput.last().isDigit() || currentInput.last() == ')')) {
                currentInput.append("×")
            }
            currentInput.append(par)
        } else if (par == ")") {
            // Pour ")" on vérifie qu'il y a au moins un "(" non fermé
            val openCount = currentInput.count { it == '(' }
            val closeCount = currentInput.count { it == ')' }
            if (openCount > closeCount && currentInput.isNotEmpty() && currentInput.last() != '(') {
                currentInput.append(par)
            }
        }
    }

    private fun addDigit(digit: String) {
        // Si l'entrée est juste "0", le remplacer par le nouveau chiffre
        if (currentInput.toString() == "0") {
            currentInput.clear()
        }

        // Si le dernier caractère est une parenthèse fermante, ajouter un × avant
        if (currentInput.isNotEmpty() && currentInput.last() == ')') {
            currentInput.append("×")
        }

        currentInput.append(digit)
    }

    private fun addDecimal() {
        // Si l'entrée est vide ou se termine par un opérateur ou parenthèse ouvrante, ajouter "0."
        if (currentInput.isEmpty() ||
            currentInput.last() in "+-×/(" ||
            currentInput.last() == ' ') {
            currentInput.append("0.")
            return
        }

        // Trouver le début du dernier nombre
        var i = currentInput.length - 1
        while (i >= 0 && (currentInput[i].isDigit() || currentInput[i] == '.')) {
            if (currentInput[i] == '.') {
                // Déjà un point décimal dans ce nombre
                return
            }
            i--
        }

        currentInput.append(".")
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

    private fun applyPercentage() {
        try {
            // Si nous sommes déjà en train d'évaluer une expression, calculons-la d'abord
            if (currentInput.any { it in "+-×/(" }) {
                evaluate()
            }

            // Maintenant, convertir le résultat en pourcentage
            val currentValue = currentInput.toString().toDoubleOrNull()
            if (currentValue != null) {
                val result = currentValue / 100

                // Formatage pour éviter les décimales inutiles
                val formattedResult = if (result == result.toLong().toDouble()) {
                    result.toLong().toString()
                } else {
                    result.toString()
                }

                currentInput = StringBuilder(formattedResult)
            }
        } catch (e: Exception) {
            currentInput = StringBuilder("Erreur")
            Log.e("CalculatorViewModel", "Erreur lors du calcul du pourcentage: ${e.message}", e)
        }
    }

    private fun inverse() {
        val num = currentInput.toString().toDoubleOrNull() ?: return
        if (num == 0.0) {
            currentInput = StringBuilder("Erreur")
            return
        }
        currentInput = StringBuilder((1 / num).toString())
    }

    private fun evaluate() {
        try {
            val result = evaluateExpression(currentInput.toString())
            lastResult = result

            // Formatter le résultat pour éviter les décimales inutiles
            val formattedResult = if (result == result.toLong().toDouble()) {
                result.toLong().toString()
            } else {
                result.toString()
            }

            currentInput = StringBuilder(formattedResult)
        } catch (e: Exception) {
            currentInput = StringBuilder("Erreur")
        }
    }

    private fun evaluateExpression(expression: String): Double {
        // Convertir l'expression en tokens
        val tokens = tokenizeExpression(expression)

        // Convertir en notation polonaise inverse (RPN) avec l'algorithme Shunting Yard
        val rpnTokens = shuntingYard(tokens)

        // Évaluer l'expression en RPN
        return evaluateRPN(rpnTokens)
    }

    private fun tokenizeExpression(expression: String): List<String> {
        val result = mutableListOf<String>()
        var i = 0

        while (i < expression.length) {
            val c = expression[i]

            when {
                c.isDigit() || c == '.' -> {
                    // Extraire un nombre complet (avec décimales)
                    val start = i
                    while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                        i++
                    }
                    result.add(expression.substring(start, i))
                    continue // i est déjà incrémenté
                }
                c == '-' && (i == 0 || (i > 0 && expression[i-1] == '(') ||
                        (i > 0 && expression[i-1] in "+-×/")) -> {
                    // C'est un nombre négatif (opérateur unaire), pas une soustraction
                    result.add("-") // Ajouter le signe négatif comme token séparé
                    i++
                }
                c == '(' || c == ')' || c == '+' || c == '-' || c == '×' || c == '/' -> {
                    result.add(c.toString())
                    i++
                }
                c == ' ' -> {
                    // Ignorer les espaces
                    i++
                }
                else -> {
                    throw IllegalArgumentException("Caractère non reconnu: $c")
                }
            }
        }

        // Second passage pour combiner les signes négatifs avec les nombres
        val finalResult = mutableListOf<String>()
        var j = 0
        while (j < result.size) {
            if (result[j] == "-" && j + 1 < result.size && result[j + 1].toDoubleOrNull() != null) {
                // Combinaison d'un signe négatif et d'un nombre
                finalResult.add("-" + result[j + 1])
                j += 2
            } else {
                finalResult.add(result[j])
                j++
            }
        }

        return finalResult
    }
    private fun shuntingYard(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val operators = Stack<String>()

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> {
                    // Si c'est un nombre, l'ajouter à la sortie
                    output.add(token)
                }
                token == "(" -> {
                    // Si c'est une parenthèse ouvrante, la pousser sur la pile
                    operators.push(token)
                }
                token == ")" -> {
                    // Si c'est une parenthèse fermante, dépiler jusqu'à trouver la parenthèse ouvrante
                    while (operators.isNotEmpty() && operators.peek() != "(") {
                        output.add(operators.pop())
                    }
                    // Enlever la parenthèse ouvrante
                    if (operators.isNotEmpty() && operators.peek() == "(") {
                        operators.pop()
                    }
                }
                token in listOf("+", "-", "×", "/") -> {
                    // Si c'est un opérateur
                    while (operators.isNotEmpty() && operators.peek() != "(" &&
                        hasPrecedence(operators.peek(), token)) {
                        output.add(operators.pop())
                    }
                    operators.push(token)
                }
            }
        }

        // Vider la pile d'opérateurs
        while (operators.isNotEmpty()) {
            output.add(operators.pop())
        }

        return output
    }

    private fun evaluateRPN(tokens: List<String>): Double {
        val stack = Stack<Double>()

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> {
                    stack.push(token.toDouble())
                }
                token in listOf("+", "-", "×", "/") -> {
                    if (stack.size < 2) throw IllegalArgumentException("Expression invalide")

                    val b = stack.pop()
                    val a = stack.pop()

                    val result = when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "×" -> a * b
                        "/" -> {
                            if (b == 0.0) throw ArithmeticException("Division par zéro")
                            a / b
                        }
                        else -> throw IllegalArgumentException("Opérateur non reconnu: $token")
                    }

                    stack.push(result)
                }
            }
        }

        if (stack.size != 1) throw IllegalArgumentException("Expression invalide")
        return stack.pop()
    }

    private fun hasPrecedence(op1: String, op2: String): Boolean {
        return op1 in listOf("×", "/") && op2 in listOf("+", "-")
    }
}