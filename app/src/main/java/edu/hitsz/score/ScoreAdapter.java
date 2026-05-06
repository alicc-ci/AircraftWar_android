package edu.hitsz.score;

import android.graphics.Color;
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

        // 设置模式标签文本和颜色
        String difficulty = score.getDifficulty();
        holder.tvMode.setText(difficulty);
        
        // 根据难度设置不同的标签颜色
        if ("SIMPLE".equalsIgnoreCase(difficulty)) {
            holder.tvMode.setBackgroundColor(Color.parseColor("#4CAF50")); // 绿色
            holder.tvMode.setTextColor(Color.WHITE);
        } else if ("NORMAL".equalsIgnoreCase(difficulty)) {
            holder.tvMode.setBackgroundColor(Color.parseColor("#FF9800")); // 橙色
            holder.tvMode.setTextColor(Color.WHITE);
        } else if ("HARD".equalsIgnoreCase(difficulty)) {
            holder.tvMode.setBackgroundColor(Color.parseColor("#F44336")); // 红色
            holder.tvMode.setTextColor(Color.WHITE);
        } else {
            holder.tvMode.setBackgroundColor(Color.LTGRAY);
            holder.tvMode.setTextColor(Color.BLACK);
        }

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
        TextView tvRank, tvName, tvScore, tvTime, tvMode;
        Button btnDelete;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvName = itemView.findViewById(R.id.tv_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvMode = itemView.findViewById(R.id.tv_mode);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}