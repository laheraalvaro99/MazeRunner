package com.example.mazerunner.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mazerunner.R;
import com.example.mazerunner.game.GameView;
import com.example.mazerunner.model.Score;

import java.util.ArrayList;
import java.util.List;






public class GameFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private GameView gameView;

    private static final float ALPHA = 0.25f;
    private float[] gravity = new float[2];

    public GameFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        gameView = view.findViewById(R.id.gameView);
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);

        // Score dinámico (si tienes un TextView txtScore, puedes actualizarlo aquí)
        gameView.setOnScoreUpdateListener(newScore -> {
            // Actualizar en UI si tienes un TextView
        });

        // Meta alcanzada
        gameView.setOnGoalReachedListener(() -> showScoreDialog(gameView.getScore()));



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        gravity[0] = ALPHA * event.values[0] + (1 - ALPHA) * gravity[0];
        gravity[1] = ALPHA * event.values[1] + (1 - ALPHA) * gravity[1];

        float x = -gravity[0];
        float y = gravity[1];

        gameView.updateBallPosition(x, y);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // ---------------- SCORE DIALOG ----------------
    private void showScoreDialog(int finalScore) {
        if (getActivity() == null) return;

        EditText input = new EditText(getContext());
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)}); // solo 3 letras

        new AlertDialog.Builder(requireActivity())
                .setTitle("¡Nivel completado!")
                .setMessage("Introduce tu nombre (3 letras)")
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String name = input.getText().toString().toUpperCase();
                    if (name.isEmpty()) name = "AAA";
                    saveScore(name, finalScore);
                    openLevelSelection();
                })
                .show();
    }

    // ---------------- GUARDAR SCORE EN SHARED PREFERENCES ----------------
    private void saveScore(String name, int score) {
        SharedPreferences prefs = requireContext().getSharedPreferences("scores", Context.MODE_PRIVATE);
        String existing = prefs.getString("score_list", "");
        String newEntry = name + "," + score;
        prefs.edit().putString("score_list", existing + (existing.isEmpty() ? "" : ";") + newEntry).apply();
    }

    // ---------------- MOSTRAR LISTA DE PUNTUACIONES ----------------
    private void showScoreList() {
        if (getActivity() == null) return;

        // Leer puntuaciones de SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("scores", Context.MODE_PRIVATE);
        String savedScores = prefs.getString("score_list", ""); // formato: "AAA,1000;BBB,900"

        ArrayList<Score> scores = new ArrayList<>();
        if (!savedScores.isEmpty()) {
            String[] entries = savedScores.split(";");
            for (String entry : entries) {
                String[] parts = entry.split(",");
                if (parts.length == 2) {
                    String name = parts[0];
                    int scoreValue;
                    try {
                        scoreValue = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        scoreValue = 0;
                    }
                    scores.add(new Score(name, scoreValue));
                }
            }
        }

        // Crear fragmento de ScoreFragment con Bundle
        ScoreFragment scoreFragment = ScoreFragment.newInstance(scores);

        // Abrir fragmento
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, scoreFragment)
                .addToBackStack(null)
                .commit();
    }


    // ---------------- ABRIR SELECCIÓN DE NIVELES ----------------
    private void openLevelSelection() {
        if (getActivity() == null) return;

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new LevelSelectionFragment());
        ft.addToBackStack(null);
        ft.commit();
    }



}
