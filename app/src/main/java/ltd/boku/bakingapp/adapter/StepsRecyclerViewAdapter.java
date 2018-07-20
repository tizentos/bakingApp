package ltd.boku.bakingapp.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ltd.boku.bakingapp.R;
import ltd.boku.bakingapp.databinding.StepsCardBinding;
import ltd.boku.bakingapp.model.Step;

public class StepsRecyclerViewAdapter  extends SelectableAdapter<StepsRecyclerViewAdapter.StepViewHolder>{

    OnStepsClickListener listener;
    Context context;
    List<Step> steps=new ArrayList<>();

    public interface OnStepsClickListener{
        void onStepsClickListener(List<Step> steps,int position);
    }

    public StepsRecyclerViewAdapter(Context context,OnStepsClickListener listener) {
         this.listener =  listener;
         this.context=context;
    }


    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        StepsCardBinding stepsCardBinding= DataBindingUtil.inflate(layoutInflater, R.layout.steps_card,parent,false);
        StepViewHolder stepViewHolder=new StepViewHolder(stepsCardBinding);
        return stepViewHolder;
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder,  int position) {
        final Step step=steps.get(position);
        final int scopePosition=position;
        Picasso.with(context)
                .load(step.getThumbnailURL().isEmpty()?null: step.getThumbnailURL())
                .placeholder(context.getDrawable(R.drawable.placeholder))
                .error(context.getDrawable(R.drawable.placeholder))
                .into(holder.stepThumbnail);

        holder.stepName.setText(step.getShortDescription());

        holder.stepCard.setOnClickListener(v -> {
          listener.onStepsClickListener(steps,scopePosition);
            if (!isSelected(scopePosition)){
                v.setBackground(null);
            }else{
                v.setSelected(true);
                v.setBackground(context.getResources().getDrawable(R.drawable.card_background));
                unSelectItem(scopePosition);
            }
        });

        if (!isSelected(position)){
            holder.stepCard.setSelected(false);
        }else{
            holder.stepCard.setSelected(true);
            holder.stepCard.setBackground(context.getResources().getDrawable(R.drawable.card_background));
        }

    }
//
    @Override
    public int getItemCount() {
        if(steps == null) return 0;
        return steps.size();
    }
//
    public void setSteps (List<Step> steps){
        this.steps=steps;
        notifyDataSetChanged();
    }


    public static class StepViewHolder extends RecyclerView.ViewHolder{
        TextView stepName;
        CardView stepCard;
        ImageView stepThumbnail;

        public StepViewHolder(StepsCardBinding itemView) {
            super(itemView.getRoot());
            stepName=itemView.stepName;
            stepCard=itemView.stepCard;
            stepThumbnail=itemView.thumbnailImageView;
        }
    }
}
