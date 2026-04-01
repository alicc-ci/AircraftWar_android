package edu.hitsz.score;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.hitsz.R;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private List<Score> scoreList;
    private OnDeleteClickListener deleteListener;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public interface OnDeleteClickListener {
        void onDeleteClick(Score score, int position);
    }

    public ScoreAdapter(List<Score> scoreList, OnDeleteClickListener listener) {
        this.scoreList = scoreList;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        Score score = scoreList.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvName.setText(score.getPlayerName());
        holder.tvScore.setText(String.valueOf(score.getScore()));
        holder.tvTime.setText(score.getTime().format(FORMATTER));

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(score, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    public void updateData(List<Score> newList) {
        this.scoreList = newList;
        notifyDataSetChanged();
    }

    static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvScore, tvTime;
        Button btnDelete;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvName = itemView.findViewById(R.id.tv_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvTime = itemView.findViewById(R.id.tv_time);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}