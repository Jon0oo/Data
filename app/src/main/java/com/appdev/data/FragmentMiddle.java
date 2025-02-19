package com.appdev.data;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


public class FragmentMiddle extends Fragment {
    private static final String TAG = "DEBUG_FragmentMiddle";
    private SharedViewModel viewModel1;
    private SharedViewModel3 viewModel3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slider_middle, container, false);
        TextView textView = view.findViewById(R.id.wertBisHeuteBenutztErlaubt2);
        TextView textViewDataLeft = view.findViewById(R.id.textFeldBisHeuteWert);

        // Initialize ViewModel
        viewModel1 = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel3 = new ViewModelProvider(requireActivity()).get(SharedViewModel3.class);

        // Observe changes in the answer LiveData
        viewModel1.getAnswer(1).observe(getViewLifecycleOwner(), answer -> {
            // Update your UI (e.g., TextView) with the answer
            if (answer != null) {
                textView.setText(answer);
            }
        });


        viewModel3.getAnswer(1).observe(getViewLifecycleOwner(), answer3 -> {

        if(answer3.equals("off")){
            textViewDataLeft.setText("Data left");
            Log.d(TAG,"State set to data left");
        }
        else if (answer3.equals("on")) {
            textViewDataLeft.setText("Data used");
            Log.d(TAG,"State set to data used");
        }
        });

        return view;
    }
}
