package com.example.geoquiz

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

// Создаём ключ для получения ответа на вопрос через Intent Extra
private const val EXTRA_ANSWER_IS_TRUE = "com.example.geoquiz.answer_is_true"

// Создаём CheatActivity, наследуя её от AppCompatActivity
class CheatActivity: AppCompatActivity() {

    // Создаём переменную для обращения к текстовому полю с ответом
    private lateinit var answerTextView: TextView

    // Создаём переменную для обращений к кнопке показа ответа
    private lateinit var showAnswerButton: Button

    // Создаём переменную для хранения ответа на вопрос
    private var answerIsTrue = false

    // Переопределяем функцию onCreate, отвечающую за создание Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        // Вызываем базовый функционал onCreate()
        super.onCreate(savedInstanceState)
        // Подключаем XML-макет к данной Activity
        setContentView(R.layout.activity_cheat)

        // Загружаем из Intent Extra ответ на вопрос (по умолчанию значение равно false)
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        // Инициализируем элементы интрфейса
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)

        // Добавляем слушателя для кнопки "Show Answer"
        showAnswerButton.setOnClickListener {

            // Помещаем в переменную значение ответа на вопрос
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }

            // Запичываем ответ на вопрос в соответствующее текстовое поле
            answerTextView.setText(answerText)
        }
    }

    // Создаём companion-объект, к содержимому которого можно обращаться без создания экземляра класса
    companion object {

        // Функция для создания нового интента с загруженным в него ответом
        // (Принимает на вход контекст класса из которого вызывается и ответ на вопрос)
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {

            // Возвращаем интент перехода на CheatActivity с добавленным в Extra ответом на вопрос
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}