package com.example.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

// Создаём ключ для получения ответа на вопрос через Intent Extra
private const val EXTRA_ANSWER_IS_TRUE = "com.example.geoquiz.answer_is_true"

// Создаём ключ для возвращения результат, подсмотрел ли пользователь ответ
private const val EXTRA_ANSWER_SHOWN = "com.example.geoquiz.answer_is_shown"

// Создаём ключ для получения доступа к индексу вопроса
private const val EXTRA_CURRENT_INDEX = "com.example.geoquiz.current_index"

// Ключ для сохранения показателя, был ли подсмотрен ответ на вопрос
private const val KEY_ANSWER_IS_SHOWN = "answerIsShown"

// Создаём CheatActivity, наследуя её от AppCompatActivity
class CheatActivity: AppCompatActivity() {

    // Создаём переменную для обращения к текстовому полю с ответом
    private lateinit var answerTextView: TextView

    // Создаём переменную для обращений к кнопке показа ответа
    private lateinit var showAnswerButton: Button

    // Создаём переменную для хранения ответа на вопрос
    private var answerIsTrue = false

    // Создаём переменную для хранения индекса текущего вопроса
    // (передаётся при каждом вызове функции показа ответа)
    private var currentAnswerIndex = -1

    // Линиво инициализируем cheatViewModel, привязывая его к CheatActivity с помощью ViewModelProvider
    private val cheatViewModel: CheatViewModel by lazy {
        ViewModelProvider(this)[CheatViewModel::class.java]
    }

    // Создаём переменную для текстового поля с версией API
    private lateinit var apiTextView: TextView

    // Переопределяем функцию onCreate, отвечающую за создание Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        // Вызываем базовый функционал onCreate()
        super.onCreate(savedInstanceState)
        // Подключаем XML-макет к данной Activity
        setContentView(R.layout.activity_cheat)

        // Загружаем из Intent Extra ответ на вопрос (по умолчанию значение равно false)
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        // Загружем из Intent Extra индекс текущего вопроса
        currentAnswerIndex = intent.getIntExtra(EXTRA_CURRENT_INDEX, -1)

        // Инициализируем элементы интрфейса
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)

        // Инициализируем текстовое поле для версии API и помещаем в него текущую версию API
        apiTextView = findViewById(R.id.api_text_view)
        val apiLevel = getString(R.string.api_level, Build.VERSION.SDK_INT)
        apiTextView.text = apiLevel

        // Достаём из памяти показатель, был ли подсмотрен ответ на вопрос
        cheatViewModel.answerIsShown = savedInstanceState
            ?.getBoolean(KEY_ANSWER_IS_SHOWN, false) ?: false

        // Если ответ уже был подсмотрен до поворота устройства, то возобновляем все необходимые свойства
        if (cheatViewModel.answerIsShown) showAnswer(answerIsTrue, currentAnswerIndex)

        // Добавляем слушателя для кнопки "Show Answer"
        showAnswerButton.setOnClickListener {

            // Показываем ответ на вопрос
            showAnswer(answerIsTrue, currentAnswerIndex)
        }
    }

    // Функция, вызываем перед уничтожением Activity
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Сохраняем показатель, был ли подсмотрен ответ на вопрос
        outState.putBoolean(KEY_ANSWER_IS_SHOWN, cheatViewModel.answerIsShown)
    }

    // Функция, выводящая ответ на вопрос
    private fun showAnswer(answerIsTrue: Boolean, currentAnswerIndex: Int) {

        // Помещаем в переменную значение ответа на вопрос
        val answerText = when {
            answerIsTrue -> R.string.true_button
            else -> R.string.false_button
        }

        // Записываем ответ на вопрос в соответствующее текстовое поле
        answerTextView.setText(answerText)
        // Записываем в ответ Activity, что пользователь посмотрел ответ и индекс текущего вопроса
        setAnswerShownResult(true, currentAnswerIndex)
    }

    // Функция, помещающая в ответ Activity результат, посмотрел ли пользователь ответ
    private fun setAnswerShownResult(isAnswerShown: Boolean, currentAnswerIndex: Int) {

        // Записываем в cheatViewModel, что ответ был подсмотрен
        cheatViewModel.answerIsShown = true

        // Создаём интент с результатом, посмотрел ли пользователь ответ
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
            // Также добовляем индекс текущего вопроса
            putExtra(EXTRA_CURRENT_INDEX, currentAnswerIndex)
        }
        // Помещаем в ответ RESULT_OK (число) и созданный интент
        setResult(Activity.RESULT_OK, data)
    }

    // Создаём companion-объект, к содержимому которого можно обращаться без создания экземляра класса
    companion object {

        // Функция для создания нового интента с загруженным в него ответом
        // (Принимает на вход контекст класса из которого вызывается и ответ на вопрос)
        fun newIntent(packageContext: Context, answerIsTrue: Boolean, currentAnswerIndex: Int): Intent {

            // Возвращаем интент перехода на CheatActivity с добавленным в Extra ответом на вопрос
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
                putExtra(EXTRA_CURRENT_INDEX, currentAnswerIndex)
            }
        }
    }
}


