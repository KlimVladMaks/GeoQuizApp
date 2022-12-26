package com.example.geoquiz

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider

/** Страница 168 **/

// Данный файл является частью контроллера

// Создаём TAG для отладки приложения с помощью журнала
private const val TAG = "MainActivityTAG"

// Создаём ключ для сохранения пары "ключ-значение" при уничтожении Activity
private const val KEY_INDEX = "index"

// Создаём ключ для хранения счётчика правильно отвеченных вопросов
private const val KEY_COUNTER = "counter"

// Создаём ключ для хранения индексов отвеченных вопросов
private const val KEY_ANSWERED_QUESTIONS = "answeredQuestions"

// Создаём ключ для хранения индексов вопросов, на которые бы подсмотрен ответ
private const val KEY_CHEATING_QUESTIONS = "cheatingQuestions"

// Создаём ключ для проверки результата, подсмотрел ли пользователь ответ
private const val EXTRA_ANSWER_SHOWN = "com.example.geoquiz.answer_is_shown"

// Создаём ключ для получения доступа к индексу вопроса, на который был подсмотрен ответ
private const val EXTRA_CURRENT_INDEX = "com.example.geoquiz.current_index"

// Обявляем класс MainActivity, с которого начинается работа приложения.
// Наследуем его от класса AppCompatActivity(), обеспечивающего поддержку старых версий Android
class MainActivity : AppCompatActivity() {

    // Создаём приватные переменные для хранения кнопок приложения "True" и "False"
    // Переменные являются изменяемыми и будут объявлены позже
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button

    // Создаём переменные для кнопок "Prev" и "Next"
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton

    // Создаём переменную для кнопки "Cheat"
    private lateinit var cheatButton: Button

    // Создаём переменную для тестового поля с вопросом
    private lateinit var questionTextView: TextView

    // Проводим линивую инициализацию экземпляра класса QuizViewModel
    // (линивая инициализация означает, что его можно будет инициализировать позже, при первом вызове))
    // При инициализации достаём класс QuizViewModel из реестра ViewModelProvider,
    // привязывая его к текущей Activity
    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this)[QuizViewModel::class.java]
    }

    // Помещаем в переменную startForResult компонент обработки ответа от CheatActivity
    // (иными словами, регистрируем запрос к CheatActivity)
    // Если ответ успешен, то обновялем перемнную quizViewModel.isCheater на переданное значение
    // (если ответа нет, то устанавливаем false)
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // (К коду ниже переходим лишь если в CheatActivity был специально установлен RESULT_OK)
        if (it.resultCode == Activity.RESULT_OK) {
            // Вытаскиваем из Extra индекс вопроса, на который был подсмотрен ответ, и помещаем
            // данный индекс в список cheatingQuestions
            val intent = it.data
            val cheatQuestionIndex = intent?.getIntExtra(EXTRA_CURRENT_INDEX, -1) ?: -1
            quizViewModel.cheatingQuestions.add(cheatQuestionIndex)
        }
    }

    // Переопределяем функцию onCreate, отвечающую за запуск приложения.
    // В Bundle? передаётся информация о предыдущем состоянии приложения (например, после изменение ориентации)
    // или None, если информации о предыдущем состоянии нет.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Создаём экземпляр подкласса activity, вызывая оригинальную (непереопределённую) функцию onCreate
        super.onCreate(savedInstanceState)

        // Записываем сообщение в журнал
        Log.d(TAG, "onCreate(Bundle?) called")

        // Привязываем макет activity_main к текущей активности, используя его индентификатор
        setContentView(R.layout.activity_main)

        // Проверяем, содержится ли в savedInstanceState значение индекса текущего вопроса
        // Если содержится, то восстанавливаем его
        // Если не содержится, то устанавливаем его по умолчанию как 0
        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        // Востанавливаем из памяти счётчик правильно отвеченных вопросов
        val correctAnswersCounter = savedInstanceState?.getInt(KEY_COUNTER, 0) ?: 0
        quizViewModel.correctAnswersCounter = correctAnswersCounter

        // Востанавливаем из памяти список индексов отвеченных вопросов
        // (если индекса в памяти нет, то устанавливаем пустой список)
        val answeredQuestions = savedInstanceState?.getIntArray(KEY_ANSWERED_QUESTIONS)
            ?.toMutableList() ?: emptyArray<Int>().toMutableList()
        quizViewModel.answeredQuestions = answeredQuestions

        // Загружаем из памяти список индексов вопросов, на которые был подсмотрен ответ
        val cheatingQuestions = savedInstanceState?.getIntArray(KEY_CHEATING_QUESTIONS)
            ?.toMutableList() ?: emptyArray<Int>().toMutableList()
        quizViewModel.cheatingQuestions = cheatingQuestions

        // Присваиваем кнопкам "True" и "False" объекты View по индентификатору
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)

        // Идентифицируем кнопки "Prev" и "Next"
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)

        // Идентифицируем кнопку "Cheat"
        cheatButton = findViewById(R.id.cheat_button)

        // Индентифицируем текстовое поле для вопросов
         questionTextView = findViewById(R.id.question_text_view)

        // Создаём слушателя, реагирующего на нажатие кнопки "True".
        // Указываем, что для работы ему нужно передать объект View (это необязательно)
        // Используем лямбда-выражение
        trueButton.setOnClickListener {
            // Проверяем пользовательский ответ
            checkAnswer(true)
        }

        // Создаём слушателя для кнопки "False"
        falseButton.setOnClickListener {
            // Проверяем пользовательский ответ
            checkAnswer(false)
        }

        // Создаём слушателя для кнопки "Next"
        nextButton.setOnClickListener {
            // Увеличиваем индекс текущего вопроса
            quizViewModel.moveToNext()
            // Обновлеям вопрос
            updateQuestion()
        }

        // Создаём слушателя для кнопки "Prev"
        prevButton.setOnClickListener {
            // Уменьшаем индекс текущего вопроса
            quizViewModel.moveToPrev()
            // Обновлеям вопрос
            updateQuestion()
        }

        // Создаём слушателя для кнопки "Cheat!"
        cheatButton.setOnClickListener {
            // Подготавливаем интент перехода на CheatActivity, загружая в него информацию об
            // ответе на текущий вопрос и индексе текущего вопроса, после чего запускаем CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity,
                answerIsTrue,
                quizViewModel.currentIndex)
            // (При возврате из CheatActivity автоматичеки обновится quizViewModel.isCheater)
            startForResult.launch(intent)
        }

        // Создаём слушателя для поля TextView с вопросом
        questionTextView.setOnClickListener {
            // Увеличиваем индекс текущего вопроса
            quizViewModel.moveToNext()
            // Обновлеям вопрос
            updateQuestion()
        }

        // Обновляем вопрос
        updateQuestion()
    }

    // Функция для запуска Activity
    override fun onStart() {
        super.onStart()
        // Делаем запись в журнал
        Log.d(TAG, "onStart() called")
    }

    // Функция для перевода Activity на передний план
    override fun onResume() {
        super.onResume()
        // Делаем запись в журнал
        Log.d(TAG, "onResume() called")
    }

    // Функция для приостановки Activity
    override fun onPause() {
        super.onPause()
        // Делаем запись в журнал
        Log.d(TAG, "onPause() called")
    }

    // Переобределяем функцию onSaveInstanceState, которая вызывается при остановке Activity,
    // чтобы сохранить состояние экземпляра (т.е. сохранить определённые данные)
    // На вход функции передаём объект Bundle, хранящий данные о предыдущем состоянии
    override fun onSaveInstanceState(savedInstanceState: Bundle) {

        // Вызываем оригинальную функцию (тажке передаём ей объект Bundle)
        super.onSaveInstanceState(savedInstanceState)

        // Отправляем отладочное сообщение о вызове данной функции
        Log.i(TAG, "onSaveInstanceState")

        // Сохраняем значение индекса текущего вопроса
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)

        // Сохраняем значение счётчика правильно отвеченных вопросов
        savedInstanceState.putInt(KEY_COUNTER, quizViewModel.correctAnswersCounter)

        // Сохраняем индексы отвеченных вопросов
        savedInstanceState.putIntArray(KEY_ANSWERED_QUESTIONS, quizViewModel.answeredQuestions.toIntArray())

        // Сохраняем индексы вопросов, на которые был подсмотрен ответ
        savedInstanceState.putIntArray(KEY_CHEATING_QUESTIONS, quizViewModel.cheatingQuestions.toIntArray())
    }

    // Функция для отсановки Activity
    override fun onStop() {
        super.onStop()
        // Делаем запись в журнал
        Log.d(TAG, "onStop() called")
    }

    // Функция для уничтожения Activity
    override fun onDestroy() {
        super.onDestroy()
        // Делаем запись в журнал
        Log.d(TAG, "onDestroy() called")
    }

    // Функция для обновления (или установки) вопроса в поле TextView
    private fun updateQuestion() {
        // Получаем идентификатор текущего вопроса
        val questionTextResId = quizViewModel.currentQuestionText
        // Размещаем его в поле TextView
        questionTextView.setText(questionTextResId)
        // Проверяем, был ли уже дан ответ на вопрос
        checkQuestion()
    }

    // Функция для проверки правильности пользовательского ответа
    private fun checkAnswer(userAnswer: Boolean) {

        // Узнаём правильный ответ на текущий вопрос
        val correctAnswer = quizViewModel.currentQuestionAnswer

        // Подготавливаем сообщение для всплывающего уведомления
        val messageResId = when {
            // Если ответ на данный вопрос подсмотрен
            quizViewModel.currentIndex in quizViewModel.cheatingQuestions -> R.string.judgment_toast
            // Если ответ верный
            userAnswer == correctAnswer -> R.string.correct_toast
            // Если ответ неверный
            else -> R.string.incorrect_toast
        }

        // Если ответ правильный, то увеличивает счётчик правильных ответов на один
        if (messageResId == R.string.correct_toast) quizViewModel.correctAnswersCounter++

        // Выводим соответствующую строку в виде всплывающего уведомления
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        // Блокируем кнопки ответа
        blockAnswerButton()

        // Добавляем индекс вопроса в список отвеченных вопросов
        quizViewModel.answeredQuestions.add(quizViewModel.currentIndex)
    }

    // Функция, проверяющая, должен ли вопрос быть заблокирован
    private fun checkQuestion() {
        // Если ответы на все вопросы получены, то выводим результат и очищаем список отвеченных вопросов
        // После чего завершаем функцию
        if (quizViewModel.answeredQuestions.size == quizViewModel.questionBankSize) {
            showResult()
            quizViewModel.answeredQuestions.clear()
            return
        }

        // Если на вопрос уже дан ответ, то блокирем кнопки ответа
        if (quizViewModel.currentIndex in quizViewModel.answeredQuestions) blockAnswerButton()
        // Иначе разблокируем кнопки ответа
        else unblockAnswerButton()
    }

    // Функция для блокировки кнопок ответа на вопрос
    private fun blockAnswerButton() {
        // Блокируем кнопки
        trueButton.isClickable = false
        falseButton.isClickable = false
        // Меняем цвет на серый
        trueButton.setBackgroundColor(Color.GRAY)
        falseButton.setBackgroundColor(Color.GRAY)
    }

    // Функция для разблокировки кнопок ответа на вопрос
    private fun unblockAnswerButton() {
        // Разблокируем кнопки
        trueButton.isClickable = true
        falseButton.isClickable = true
        // Возвращаем прежний цвет
        trueButton.setBackgroundColor(Color.parseColor("#FF6200EE"))
        falseButton.setBackgroundColor(Color.parseColor("#FF6200EE"))
    }

    // Функция для вывода всплыващего сообщения с количеством правильных ответов
    private fun showResult() {
        // Создаём переменную с сообщением
        val messageResult = "Result: " +
                "${quizViewModel.correctAnswersCounter}/${quizViewModel.questionBankSize} " +
                "correct answers"
        // Выводим сообщение с результатами
        Toast.makeText(this, messageResult, Toast.LENGTH_SHORT).show()
        // Обнуляем счётчик правильных ответов
        quizViewModel.correctAnswersCounter = 0
        // Очищаем список ответов, на которые был подсмотрен ответ
        quizViewModel.cheatingQuestions.clear()
        // Разблокируем кнопки ответов
        unblockAnswerButton()
    }
}




