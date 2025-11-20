package com.example.mazerunner.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mazerunner.R;
import com.example.mazerunner.model.Score;
import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {

    private List<Score> scoreList;

    public ScoreAdapter(List<Score> scores) {
        this.scoreList = scores;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtScore;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtScore = itemView.findViewById(R.id.txtTime); // TextView que muestra la puntuación
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Score score = scoreList.get(position);
        holder.txtName.setText(score.getName());         // Nombre en mayúsculas
        holder.txtScore.setText(String.valueOf(score.getScore())); // Puntuación final
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }
}
