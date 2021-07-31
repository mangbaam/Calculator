package mangbaam.aop.part2.chaper04

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.room.Room
import mangbaam.aop.part2.chaper04.model.History
import java.lang.ArithmeticException
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val expressionTextView: TextView by lazy {
        findViewById(R.id.expressionTextView)
    }

    private val resultTextView: TextView by lazy {
        findViewById(R.id.resultTextView)
    }

    private val historyLayout: View by lazy {
        findViewById(R.id.historyLayout)
    }

    private val historyLinearLayout: LinearLayout by lazy {
        findViewById(R.id.historyLinearLayout)
    }

    private lateinit var db: AppDatabase

    private val expressionList = mutableListOf<String>()

    private var isOperator = false
    private var hasOperator = false
    private var numberCount = 0
    private var openBracketCount = 0
    private var isDotIn = false
    private var isResultClicked = false

    private fun priority(op: String): Int {
        return when (op) {
            "(" -> 0
            "+", "-" -> 1
            "×", "÷", "%" -> 2
            // "^" -> 3
            else -> -1
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build()
    }

    fun buttonClicked(v: View) {
        when (v.id) {
            R.id.button0 -> numberButtonClicked("0")
            R.id.button1 -> numberButtonClicked("1")
            R.id.button2 -> numberButtonClicked("2")
            R.id.button3 -> numberButtonClicked("3")
            R.id.button4 -> numberButtonClicked("4")
            R.id.button5 -> numberButtonClicked("5")
            R.id.button6 -> numberButtonClicked("6")
            R.id.button7 -> numberButtonClicked("7")
            R.id.button8 -> numberButtonClicked("8")
            R.id.button9 -> numberButtonClicked("9")
            R.id.buttonPlus -> operatorButtonClicked("+")
            R.id.buttonMinus -> operatorButtonClicked("-")
            R.id.buttonMulti -> operatorButtonClicked("×")
            R.id.buttonDivider -> operatorButtonClicked("÷")
            R.id.buttonModulo -> operatorButtonClicked("%")
        }
    }

    fun dotButtonClicked(v: View) {
        isOperator = false
        if (isResultClicked) {
            expressionTextView.text = ""
            expressionList.clear()
            isResultClicked = false
        }
        when {
            isDotIn -> return
            expressionList.isEmpty() -> {
                expressionList.add("0.")
            }
            else -> {
                when {
                    isOperator ||
                            expressionList.last() == "(" -> expressionList.add("0.")
                    expressionList.last() == ")" -> {
                        operatorButtonClicked("×")
                        expressionList.add("0.")
                    }
                    expressionList.last().isNumber() -> expressionList[expressionList.lastIndex] =
                        expressionList.last() + "."
                }
            }
        }
        expressionTextView.text = getExpressionText()
        numberCount++
        isDotIn = true
    }

    private fun numberButtonClicked(number: String) {
        isOperator = false
        if (isResultClicked) {
            expressionTextView.text = ""
            expressionList.clear()
            isResultClicked = false
        } else if (expressionList.isNotEmpty() && numberCount >= 15) {
            Toast.makeText(this, "15 자리 까지만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            return
        } else if (expressionList.isEmpty() && number == "0") {
            Toast.makeText(this, "0은 제일 앞에 올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        } else if (expressionList.isNotEmpty() && expressionList.last() == ")") {
            operatorButtonClicked("×")
            numberCount = 0
        }

        if (numberCount == 0 || expressionList.isEmpty()) {     // 새로 숫자가 입력되는 경우
            expressionList.add(number)
        } else {    // 숫자가 계속 입력되는 경우
            expressionList[expressionList.lastIndex] = expressionList.last() + number
        }

        expressionTextView.text = getExpressionText()
        numberCount++

        resultTextView.text = calculateExpression()

    }

    private fun operatorButtonClicked(operator: String) {
        isResultClicked = false
        if (expressionList.isEmpty() || expressionList.last() == "(") {
            return
        }

        when {
            isOperator -> expressionList[expressionList.lastIndex] = operator
            else -> expressionList.add(operator)
        }
        // TODO 연산자 여러 개 -> 연산자들 모두 초록색으로
        /*val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.green)),
            expressionTextView.text.length - 1,
            expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        expressionTextView.text = ssb*/

        expressionTextView.text = getExpressionText()

        isOperator = true
        hasOperator = true
        isDotIn = false
        numberCount = 0
    }

    fun bracketButtonClicked(v: View) {
        isResultClicked = false
        if (expressionList.isEmpty()) {        // 식이 비어있는 경우
            expressionList.add("(")
            openBracketCount++
        } else {
            val lastChar = expressionList.last()

            when {
                openBracketCount < 0 -> {
                    Log.d("BracketError", "$openBracketCount is negative")
                }
                openBracketCount == 0 -> { // 괄호의 짝이 다 맞는 경우
                    when {
                        lastChar == "(" || isOperator -> expressionList.add("(")
                        lastChar.isNumber() || lastChar == ")" -> {
                            operatorButtonClicked("×")
                            expressionList.add("(")
                        }
                    }
                    openBracketCount++
                    numberCount = 0
                }
                openBracketCount > 0 -> {
                    when {
                        lastChar.isNumber() || lastChar == ")" -> {
                            expressionList.add(")")
                            openBracketCount--
                        }
                        lastChar == "(" || isOperator -> {
                            expressionList.add("(")
                            openBracketCount++
                        }
                    }
                }
            }
        }

        isDotIn = false
        numberCount = 0
        isOperator = false

        expressionTextView.text = getExpressionText()
    }

    fun resultButtonClicked(v: View) {
        if (expressionList.size < 2) return
        if (openBracketCount > 0 || isOperator) {
            Toast.makeText(this, "아직 완성되지 않은 수식입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val expressionText = expressionList.joinToString("")
        val resultText = calculateExpression()

        // DB에 계산 결과 저장
        Thread(Runnable {
            db.historyDao()
                .insertHistory(History(null, expressionText, resultText))
        }).start()

        resultTextView.text = ""
        expressionTextView.text = resultText


        expressionList.clear()
        expressionList.add(resultText.replace(",", ""))

        isDotIn = false
        isOperator = false
        hasOperator = false
        numberCount = 0
        isResultClicked = true
        openBracketCount = 0
    }

    private fun calculateExpression(): String {
        val stack = Stack<String>()
        val exp = toPostfix()
        if (exp.isEmpty()) return ""

        for (c in exp) {
            when {
                stack.isEmpty() || c.isNumber() -> stack.push(c)
                stack.size >= 2 && c.isOp() -> {
                    try {
                        val num2 = stack.pop().toBigDecimal()
                        val num1 = stack.pop().toBigDecimal()
                        lateinit var tmp: BigDecimal
                        when (c) {
                            "+" -> tmp = num1.plus(num2)
                            "-" -> tmp = num1.minus(num2)
                            "×" -> tmp = num1.multiply(num2)
                            "÷" -> tmp = num1.divide(num2, 10, RoundingMode.HALF_UP)
                            "%" -> tmp = num1.remainder(num2)
                        }
                        stack.push(tmp.stripTrailingZeros().toString()) // 소수점 오른쪽의 0 제거
                    } catch (e: Exception) {
                        if (e is ArithmeticException) {
                            Toast.makeText(this, "연산 중 오류 발생", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    }
                }
                else -> {
                    // 수식이 완성되지 않았거나 오류
                    Log.d("MainActivity", "calculateExpression: stack: $stack, c: $c")
                }
            }
        }
        return if(stack.peek().isNumber()) NumberFormat.getInstance(Locale.US).format(stack.peek().toBigDecimal()).toString() else ""
    }

    private fun toPostfix(): List<String> {
        val stack = Stack<String>()
        val resultList = mutableListOf<String>()

        var expList = mutableListOf<String>()
        expList.addAll(expressionList)

        if (expList.last().isOp())
            expList = expList.dropLast(1) as MutableList<String>

        if (openBracketCount > 0)
            for (i in 1..openBracketCount)
                expList.add(")")

        for (exp in expList) {
            when {
                exp.isNumber() -> resultList.add(exp)
                exp == "(" -> stack.push(exp)
                exp == ")" -> {
                    while (stack.isNotEmpty() && stack.peek() != "(") {
                        resultList.add(stack.pop())
                    }
                    stack.pop()
                }
                else -> {
                    if (priority(exp) == -1) {
                        Toast.makeText(this, "잘못된 수식 입력!", Toast.LENGTH_SHORT).show()
                    }
                    while (stack.isNotEmpty() && priority(exp) <= priority(stack.peek())) {
                        resultList.add(stack.pop())
                    }
                    stack.push(exp)
                }
            }
        }
        while (stack.isNotEmpty()) {
            if (stack.peek() == "(") {
                Toast.makeText(this, "Invalid Expression", Toast.LENGTH_SHORT).show()
                return emptyList()
            }
            resultList.add(stack.pop())
        }
        return resultList
    }

    fun clearButtonClicked(v: View) {
        expressionTextView.text = ""
        resultTextView.text = ""
        expressionList.clear()
        isOperator = false
        hasOperator = false
        isDotIn = false
        numberCount = 0
        openBracketCount = 0
    }

    fun backButtonClicked(v: View) {
        if (expressionList.isEmpty()) return

        val lastExp = expressionList.last()
        val leftExp = expressionList.dropLast(1)

        when {
            lastExp.isOp() -> {
                expressionList.clear()
                expressionList.addAll(leftExp)
                isOperator = false
            }
            lastExp.isNumber() -> {

                when (lastExp.length) {
                    1 -> {
                        expressionList.clear()
                        expressionList.addAll(leftExp)
                        numberCount = 0
                    }
                    else -> {
                        expressionList[expressionList.lastIndex] = lastExp.dropLast(1)
                        numberCount--
                        isDotIn = "." in expressionList.last()
                    }
                }
            }
            lastExp == "(" -> {
                expressionList.clear()
                expressionList.addAll(leftExp)
                openBracketCount--
            }
            lastExp == ")" -> {
                expressionList.clear()
                expressionList.addAll(leftExp)
                openBracketCount++
            }
        }
        if (expressionList.isEmpty()) {
            numberCount = 0
            openBracketCount = 0
            isOperator = false
            hasOperator = false
            isDotIn = false
            isResultClicked = false

            expressionTextView.text = ""
            resultTextView.text = ""
            return
        } else {
            isOperator = expressionList.last().isOp()
        }
        expressionTextView.text = getExpressionText()
        resultTextView.text = calculateExpression()
    }

    fun historyButtonClicked(v: View) {
        historyLayout.isVisible = true
        historyLinearLayout.removeAllViews()

        Thread(Runnable {
            db.historyDao().getAll().reversed().forEach {
                runOnUiThread {
                    val historyView =
                        LayoutInflater.from(this).inflate(R.layout.history_row, null, false)
                    historyView.findViewById<TextView>(R.id.expressionTextView).text = it.expression
                    historyView.findViewById<TextView>(R.id.resultTextView).text = "= ${it.result}"

                    historyLinearLayout.addView(historyView)
                }
            }
        }).start()
    }

    fun historyClearButtonClicked(v: View) {
        historyLinearLayout.removeAllViews()

        Thread(Runnable {
            db.historyDao().deleteAll()
        }).start()
    }

    fun closeHistoryButtonClicked(v: View) {
        historyLayout.isVisible = false
    }

    private fun getExpressionText(): String {
        val exp = expressionList
        var expressionText = ""

        for (c in exp) {
            when {
                c.isNumber() -> {
                    expressionText += NumberFormat.getInstance(Locale.US).format(c.toBigDecimal())
                        .toString()
                    if (c.last()=='.') expressionText = "$expressionText."
                }
                /*c.isOperator() -> {
                    expressionText += c
                    val ssb = SpannableStringBuilder(expressionText)
                    ssb.setSpan(
                        ForegroundColorSpan(getColor(R.color.green)),
                        expressionText.length - 1,
                        expressionText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    expressionText = ssb.toString()
                }*/
                else -> expressionText += c
            }
        }
        return expressionText
    }
}

fun String.isNumber(): Boolean {
    return try {
        this.toBigDecimal()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

fun String.isOp(): Boolean {
    return when (this) {
        "+", "-", "×", "÷", "%" -> true
        else -> false
    }
}