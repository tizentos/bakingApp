package ltd.boku.bakingapp.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ltd.boku.bakingapp.R;
import ltd.boku.bakingapp.databinding.StepsCardBinding;
import ltd.boku.bakingapp.model.Step;

public class StepsRecyclerViewAdapter  extends RecyclerView.Adapter<StepsRecyclerViewAdapter.StepViewHolder>{

    OnStepsClickListener listener;
    List<Step> steps=new ArrayList<>();

    public interface OnStepsClickListener{
        void onStepsClickListener(List<Step> steps,int position);
    }

    public StepsRecyclerViewAdapter(OnStepsClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        StepsCardBinding stepsCardBinding= DataBindingUtil.inflate(layoutInflater, R.layout.steps_card,parent,false);
        StepViewHolder stepViewHolder=new StepViewHolder(stepsCardBinding);
        return stepViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, final int position) {
        final Step step=steps.get(position);
        holder.stepName.setText(step.getShortDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              listener.onStepsClickListener(steps,position);
            }
        });
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

        public StepViewHolder(StepsCardBinding itemView) {
            super(itemView.getRoot());
            stepName=itemView.stepName;
        }
    }
}
