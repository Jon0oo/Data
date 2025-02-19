package com.appdev.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel3 extends ViewModel {
    private static MutableLiveData<String> answerLiveData = new MutableLiveData<>();

    public LiveData<String> getAnswer(int questionNumber) {
        // Return the LiveData for the specified question number
        return answerLiveData;
    }

    public static void setAnswer(int questionNumber, String answer) {
        // Update the LiveData with the provided answer
        answerLiveData.setValue(answer);
    }
}
