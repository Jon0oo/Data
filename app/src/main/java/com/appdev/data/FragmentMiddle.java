package com.appdev.data;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class FragmentMiddle extends Fragment {

    private SharedViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slider_middle, container, false);
        TextView textView = view.findViewById(R.id.wertBisHeuteBenutztErlaubt2);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe changes in the answer LiveData
        viewModel.getAnswer(1).observe(getViewLifecycleOwner(), answer -> {
            // Update your UI (e.g., TextView) with the answer
            if (answer != null) {
                textView.setText(answer);
            } else {

            }
        });

        return view;
    }
}
