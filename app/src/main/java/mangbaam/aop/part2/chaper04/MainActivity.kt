package mangbaam.aop.part2.chaper04

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.room.Room
import mangbaam.aop.part2.chaper04.model.History
import java.lang.Character.isDigit

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
    /*private val digits = ListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", ".")*/
    private val digits = ((0..9).map { it -> it.toString() } + ".").toList() // 0~9, "."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build()

        val s = (1..9).map { it.toString() }.toMutableList()
        Log.d("TEST", "s: $s")
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
        resultTextView.text = "bracketCount: $openBracketCount, List: $expressionList"
    }

    fun dotButtonClicked(v: View) {
        val expressionText = expressionTextView.text.toString()

        if (isDotIn) return
        else if (expressionList.isEmpty()) {
            expressionTextView.append("0.")
        } else {
            when {
                isOperator ||
                        expressionList.last() == "(" -> expressionList.add("0.")
                expressionList.last() == ")" -> {
                    operatorButtonClicked("×")
                    expressionList.add("0.")
                }
                expressionList.last().isNumber() -> expressionList[expressionList.lastIndex] =
                    expressionList.last()+"."
            }
        }
        expressionTextView.text = expressionList.joinToString("")
        numberCount++
        isDotIn = true
    }

    private fun numberButtonClicked(number: String) {
        isOperator = false

        if (expressionList.isNotEmpty() && numberCount >= 15) {
            Toast.makeText(this, "15 자리 까지만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            return
        } else if (expressionList.isEmpty() && number == "0") {
            Toast.makeText(this, "0은 제일 앞에 올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        } else if (expressionList.isNotEmpty() && expressionList.last() == ")") {
            operatorButtonClicked("×")
            numberCount = 0
        }

        if (numberCount == 0 || expressionList.isEmpty()) {
            expressionList.add(number)
        } else {
            expressionList[expressionList.lastIndex] = expressionList.last() + number
        }

        expressionTextView.text = expressionList.joinToString("")
        numberCount++
        // resultTextView.text = calculateExpression()

    }

    private fun operatorButtonClicked(operator: String) {
        if (expressionList.isEmpty() || expressionList.last() == "(") {
            return
        }

        when {
            isOperator -> expressionList[expressionList.lastIndex] = operator
            else -> expressionList.add(operator)
        }
/*
        // TODO 연산자 여러 개 -> 연산자들 모두 초록색으로
        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.green)),
            expressionTextView.text.length - 1,
            expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        expressionTextView.text = ssb*/

        expressionTextView.text = expressionList.joinToString("")

        isOperator = true
        hasOperator = true
        isDotIn = false
        numberCount = 0
    }

    fun bracketButtonClicked(v: View) {
        if (expressionList.isEmpty()) {        // 식이 비어있는 경우
            expressionList.add("(")
            openBracketCount++
        } else {
            val lastChar = expressionList.last()

            if (openBracketCount < 0) {
                Log.d("BracketError", "$openBracketCount is negative")
            } else if (openBracketCount == 0) { // 괄호의 짝이 다 맞는 경우
                when {
                    lastChar == "(" || isOperator -> expressionList.add("(")
                    lastChar.isNumber() || lastChar == ")" -> {
                        operatorButtonClicked("×")
                        expressionList.add("(")
                    }
                }
                openBracketCount++
                numberCount = 0
            } else if (openBracketCount > 0) {
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

        isDotIn = false
        numberCount = 0
        isOperator = false

        expressionTextView.text = expressionList.joinToString("")
    }

    fun resultButtonClicked(v: View) {
        val expressionTexts = expressionTextView.text.split(" ")
        if (expressionTextView.text.isEmpty() || expressionTexts.size == 1) {
            return
        }

        if (openBracketCount > 0 || (expressionTexts.size != 3 && hasOperator)) {
            Toast.makeText(this, "아직 완성되지 않은 수식입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if ((expressionTexts[0].isNumber() && expressionTexts[2].isNumber()).not()) {
            Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val expressionText = expressionTextView.text.toString()
        val resultText = calculateExpression()

        Thread(Runnable {
            db.historyDao().insertHistory(History(null, expressionText, resultText))
        }).start()

        resultTextView.text = ""
        expressionTextView.text = resultText

        isDotIn = false
        isOperator = false
        hasOperator = false

    }

    private fun calculateExpression(): String {
        val expressionTexts = expressionTextView.text.split(" ")

        if (hasOperator.not() || expressionTexts.size != 3) {
            return ""
        } else if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            return ""
        }

        val exp1 = expressionTexts[0].toBigInteger()
        val exp2 = expressionTexts[2].toBigInteger()
        val op = expressionTexts[1]

        return when (op) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "×" -> (exp1 * exp2).toString()
            "÷" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> ""
        }
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
}

fun String.isNumber(): Boolean {
    return try {
        this.toBigDecimal()
        true
    } catch (e: NumberFormatException) {
        false
    }
}