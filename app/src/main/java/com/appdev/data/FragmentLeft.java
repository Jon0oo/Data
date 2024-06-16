package com.appdev.data;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class FragmentLeft extends Fragment {

    private SharedViewModel2 viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slider_left, container, false);
        ProgressBar progressBar = view.findViewById(R.id.ProgressBarData);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel2.class);

        // Observe changes in the answer LiveData
        viewModel.getAnswer(1).observe(getViewLifecycleOwner(), answer2 -> {
            // Update your UI (e.g., TextView) with the answer
            if (answer2 != null ) {

            int calcvalue3 = (int) Math.round(Float.parseFloat(answer2));
            progressBar.setProgress(calcvalue3);




            }

        });


        return view;


    }
}