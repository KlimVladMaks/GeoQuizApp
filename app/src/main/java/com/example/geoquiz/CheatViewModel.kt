package com.example.geoquiz

import androidx.lifecycle.ViewModel

// Создаём ViewModel для сохранения информации при повороте устройства
class CheatViewModel: ViewModel() {

    // Переменная, хранящая информацию, был ли показан ответ
    var answerIsShown = false
}


