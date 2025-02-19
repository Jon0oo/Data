package com.appdev.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.HashMap;
import java.util.Map;

public class SharedViewModel2 extends ViewModel {
    private final Map<Integer, MutableLiveData<String>> answersMap = new HashMap<>();

    public SharedViewModel2() {
        // Initialize if you have predefined question numbers
        answersMap.put(1, new MutableLiveData<>());
        answersMap.put(2, new MutableLiveData<>());
    }

    public LiveData<String> getAnswer(int questionNumber) {
        // Ensure we return a LiveData instance even if the key is missing
        if (!answersMap.containsKey(questionNumber)) {
            answersMap.put(questionNumber, new MutableLiveData<>());
        }
        return answersMap.get(questionNumber);
    }

    public void setAnswer(int questionNumber, String answer) {
        if (!answersMap.containsKey(questionNumber)) {
            answersMap.put(questionNumber, new MutableLiveData<>());
        }
        answersMap.get(questionNumber).setValue(answer);
    }
}
