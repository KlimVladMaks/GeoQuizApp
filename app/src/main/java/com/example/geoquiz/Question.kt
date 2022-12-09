package com.example.geoquiz

import androidx.annotation.StringRes

// Данный файл является частью модели

// Создаём класс данных для хранения вопросов,
// содержащий идентификатор утверждения и его правильность (True или False).
// Анотация @StringRes указывает на то, что testResId содержит идентификатор строки
data class Question(@StringRes val textResId: Int, val answer: Boolean)


