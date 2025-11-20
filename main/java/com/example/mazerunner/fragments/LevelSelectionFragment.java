package com.example.mazerunner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.mazerunner.R;

public class LevelSelectionFragment extends Fragment {

    public LevelSelectionFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_selection, container, false);

        // Niveles 1-9
        int[] buttonIds = {
                R.id.btnLevel1, R.id.btnLevel2, R.id.btnLevel3,
                R.id.btnLevel4, R.id.btnLevel5, R.id.btnLevel6,
                R.id.btnLevel7, R.id.btnLevel8, R.id.btnLevel9
        };

        for (int i = 0; i < buttonIds.length; i++) {
            int level = i + 1; // Niveles 1 a 9
            Button btn = view.findViewById(buttonIds[i]);
            btn.setOnClickListener(v -> openLevel(level));
        }

        // Botón Atrás
        Button btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> goBackToMenu());

        return view;
    }

    private void openLevel(int level) {
        // Por ahora todos los niveles abren GameFragment (Nivel 1)
        // En el futuro aquí puedes pasar datos para cargar distintos laberintos
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new GameFragment())
                .addToBackStack(null)
                .commit();
    }

    private void goBackToMenu() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new MenuFragment())
                .commit();
    }
}
