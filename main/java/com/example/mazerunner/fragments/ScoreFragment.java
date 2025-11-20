package com.example.mazerunner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mazerunner.R;
import com.example.mazerunner.adapter.ScoreAdapter;
import com.example.mazerunner.model.Score;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ScoreFragment extends Fragment {

    private static final String ARG_SCORES = "arg_scores";

    private RecyclerView recyclerView;
    private ScoreAdapter adapter;
    private List<Score> scoreList = new ArrayList<>();

    public ScoreFragment() {}

    // Método para crear la instancia y pasar la lista
    public static ScoreFragment newInstance(ArrayList<Score> scores) {
        ScoreFragment fragment = new ScoreFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SCORES, scores);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score, container, false);

        recyclerView = view.findViewById(R.id.recyclerScores);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Recuperar lista del bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            Serializable data = bundle.getSerializable(ARG_SCORES);
            if (data instanceof ArrayList) {
                //noinspection unchecked
                scoreList = (ArrayList<Score>) data;
            }
        }

        adapter = new ScoreAdapter(scoreList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
