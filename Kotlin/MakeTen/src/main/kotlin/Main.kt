val DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
val SYMBOLS = charArrayOf('+', '-', '*', '/')

enum class Operator {
    Add,
    Subtract,
    Multiply,
    Divide,
    BracketAdd,
    BracketSubtract,
    BracketMultiply,
    BracketDivide
}

fun getOperator(char: Char, inBracket: Boolean): Operator {
    return when (char) {
        '+' -> if (inBracket) Operator.BracketAdd else Operator.Add
        '-' -> if (inBracket) Operator.BracketSubtract else Operator.Subtract
        '*' -> if (inBracket) Operator.BracketMultiply else Operator.Multiply
        '/' -> if (inBracket) Operator.BracketDivide else Operator.Divide
        else -> throw IllegalArgumentException("Invalid operator: $char")
    }
}

fun evaluateOperation(operands: MutableList<Double>, operators: MutableList<Operator>, index: Int) {
    val operator = operators[index]
    val operand1 = operands[index]
    val operand2 = operands[index + 1]

    operands[index] = when (operator) {
        Operator.Add -> operand1 + operand2
        Operator.BracketAdd -> operand1 + operand2
        Operator.Subtract -> operand1 - operand2
        Operator.BracketSubtract -> operand1 - operand2
        Operator.Multiply -> operand1 * operand2
        Operator.BracketMultiply -> operand1 * operand2
        Operator.Divide -> operand1 / operand2
        Operator.BracketDivide -> operand1 / operand2
    }

    operands.removeAt(index + 1)
    operators.removeAt(index)
}

fun evaluate(expression: String): Double {
    var inBracket = false
    val operands = mutableListOf<Double>()
    val operators = mutableListOf<Operator>()

    for (char in expression) {
        if (DIGITS.contains(char)) {
            operands.add(char.toString().toDouble())
        } else if (SYMBOLS.contains(char)) {
            operators.add(getOperator(char, inBracket))
        } else if (char == '(') {
            inBracket = true
        } else if (char == ')') {
            inBracket = false
        }
    }

    while (operators.size > 0) {
        var index = operators.indexOfFirst { it == Operator.BracketMultiply || it == Operator.BracketDivide }
        if (index == -1) {
            index = operators.indexOfFirst { it == Operator.BracketAdd || it == Operator.BracketSubtract }
        }
        if (index == -1) {
            index = operators.indexOfFirst { it == Operator.Multiply || it == Operator.Divide }
        }
        if (index == -1) {
            index = operators.indexOfFirst { it == Operator.Add || it == Operator.Subtract }
        }

        if (index != -1) {
            evaluateOperation(operands, operators, index)
        } else {
            throw IllegalArgumentException("Invalid expression")
        }
    }

    return operands[0]
}

class Solution {
    var numbers = mutableListOf<Char>()
    var operators = mutableListOf<Char>()
    var brackets = Pair(0, 0)

    private fun toExpression(): String {
        var expression = ""
        for (i in 0 until numbers.size) {
            if (brackets.first == i) expression += '('
            expression += numbers[i]
            if (brackets.second == i) expression += ')'
            if (i < operators.size) expression += operators[i]
        }
        return expression
    }

    fun calculate(): Double {
        return evaluate(toExpression())
    }
}

typealias IteratorBody = (solution: Solution) -> Boolean

inline fun forEachNumber(baseSolution: Solution, callback: IteratorBody): Boolean {
    for (i in DIGITS.indices) {
        for (j in i until DIGITS.size) {
            for (k in j until DIGITS.size) {
                for (l in k until DIGITS.size) {
                    baseSolution.numbers = mutableListOf(DIGITS[i], DIGITS[j], DIGITS[k], DIGITS[l])
                    if (callback(baseSolution)) return true
                }
            }
        }
    }

    return false
}

inline fun forEachDistinctNumber(baseSolution: Solution, callback: IteratorBody): Boolean {
    for (i in DIGITS.indices) {
        for (j in i + 1 until DIGITS.size) {
            for (k in j + 1 until DIGITS.size) {
                for (l in k + 1 until DIGITS.size) {
                    baseSolution.numbers = mutableListOf(DIGITS[i], DIGITS[j], DIGITS[k], DIGITS[l])
                    if (callback(baseSolution)) return true
                }
            }
        }
    }

    return false
}

inline fun forEachNumberShuffle(baseSolution: Solution, callback: IteratorBody): Boolean {
    val numbers = baseSolution.numbers.toList()
    for (i in numbers.indices) {
        val remaining1 = numbers.toMutableList()
        remaining1.removeAt(i)
        for (j in remaining1.indices) {
            val remaining2 = remaining1.toMutableList()
            remaining2.removeAt(j)
            for (k in remaining2.indices) {
                val remaining3 = remaining2.toMutableList()
                remaining3.removeAt(k)
                baseSolution.numbers = mutableListOf(numbers[i], remaining1[j], remaining2[k], remaining3[0])
                if (callback(baseSolution)) return true
            }
        }
    }

    return false
}

inline fun forEachOperator(baseSolution: Solution, callback: IteratorBody): Boolean {
    for (operator1 in SYMBOLS) {
        for (operator2 in SYMBOLS) {
            for (operator3 in SYMBOLS) {
                baseSolution.operators = mutableListOf(operator1, operator2, operator3)
                if (callback(baseSolution)) return true
            }
        }
    }

    return false
}

inline fun forEachBracket(baseSolution: Solution, callback: IteratorBody): Boolean {
    for (i in 0..3) {
        for (j in i + 1..3) {
            baseSolution.brackets = Pair(i, j)
            if (callback(baseSolution)) return true
        }
    }

    return false
}

inline fun search(title: String, numberIterator: (solution: Solution, callback: IteratorBody) -> Boolean) {
    val solution = Solution()

    val impossibleCombinations = mutableListOf<List<Char>>()

    numberIterator(solution) { solution1 ->
        val isPossible = forEachNumberShuffle(solution1) { solution2 ->
            forEachOperator(solution2) { solution3 ->
                forEachBracket(solution3) {
                    it.calculate() == 10.0
                }
            }
        }

        if (!isPossible) {
            impossibleCombinations.add(solution1.numbers.sorted())
        }

        return@numberIterator false
    }

    println("Impossible combinations for $title:")

    impossibleCombinations.forEach { println(it) }

    println("Total: ${impossibleCombinations.size}")
}

fun main() {
    search("distinct numbers", ::forEachDistinctNumber)
    search("non-distinct numbers", ::forEachNumber)
}