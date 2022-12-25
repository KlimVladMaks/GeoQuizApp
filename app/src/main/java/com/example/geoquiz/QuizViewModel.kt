package com.example.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

// Создаём тэг с именем класса для отладки
private const val TAG = "QuizViewModelTAG"

// Создаём класс QuizViewModel и наследуем его от класса ViewModel, преданзанченного для хранения
// данных и бизнес-логики для конкретного экрана
class QuizViewModel: ViewModel() {

    // Создаём переменную для отслеживания индекса списка
    var currentIndex = 0

    // Создаём счётчик для подсчёта количества правильных ответов
    var correctAnswersCounter = 0

    // Список индексов вопросов, на которые уже дан ответ
    var answeredQuestions = mutableListOf<Int>()

    // Создаём список вопросов, состоящий из экземпляров класса Question,
    // с загруженными в них индитификаторами строк и ответов на вопросы
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    // Переменная, хранящая ответ на текущий вопрос
    // get() вызывается при попытке получить значение из данной переменной
    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    // Переменная, хранящая текст текущего вопроса
    // get() вызывается при попытке получить значение из данной переменной
    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    // Переменная, хранящая размер списка вопросов
    // get() вызывается при попытке получить значение из данной переменной
    val questionBankSize: Int
        get() = questionBank.size

    // Переменная, показывающая подсматривал ли пользователь ответ
    var isCheater = false

    // Функция для увеличения индекса текущего вопроса (с защитой от выхода за границы диапозона)
    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    // Функция для уменьшения индекса текущего вопроса (с защитой от выхода за границы диапозона)
    fun moveToPrev() {
        // Уменьшаем индекс текущего вопроса на один
        // (если новое значение меньше нуля, то устанавливаем максимальный индекс)
        currentIndex = if (currentIndex - 1 >= 0) {
            currentIndex - 1
        } else {
            questionBank.size - 1
        }
    }
}