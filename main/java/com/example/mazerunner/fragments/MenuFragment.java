package com.example.mazerunner.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.mazerunner.R;

public class MenuFragment extends Fragment {

    public MenuFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Botón Jugar → abre selección de niveles
        view.findViewById(R.id.btnPlay).setOnClickListener(v -> {
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new LevelSelectionFragment());
            ft.addToBackStack(null); // Permite volver al menú con botón atrás
            ft.commit();
        });

        // Botón Scores → abre fragmento de puntuaciones usando Bundle
        view.findViewById(R.id.btnScores).setOnClickListener(v -> {
            // Crear fragmento
            ScoreFragment scoreFragment = new ScoreFragment();

            // Recuperar puntuaciones guardadas en SharedPreferences
            SharedPreferences prefs = requireContext().getSharedPreferences("scores", Context.MODE_PRIVATE);
            String storedScores = prefs.getString("score_list", "");

            // Pasar datos mediante Bundle
            Bundle bundle = new Bundle();
            bundle.putString("score_list", storedScores);
            scoreFragment.setArguments(bundle);

            // Reemplazar fragmento
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, scoreFragment);
            ft.addToBackStack(null);
            ft.commit();
        });

        return view;
    }
}
