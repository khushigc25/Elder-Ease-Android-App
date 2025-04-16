package com.example.elderease.medicinesreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.elderease.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class MedicineReminderAdapter extends RecyclerView.Adapter<MedicineReminderAdapter.ReminderViewHolder> {

    private List<MedicineReminderModel> reminderList;
    private Context context;

    public MedicineReminderAdapter(Context context, List<MedicineReminderModel> reminderList) {
        this.context = context;
        this.reminderList = reminderList;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medicine_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        MedicineReminderModel reminder = reminderList.get(position);

        // Set the medicine name without a dash.
        holder.tvReminderName.setText(reminder.getMedicineName());

        // Add a dash prefix for the other fields.
        holder.tvReminderDosage.setText("- " + reminder.getDosage());
        holder.tvReminderType.setText("- " + reminder.getMedicineType());

        // Compute the Before/After Meal string from booleans.
        StringBuilder mealTiming = new StringBuilder();
        if (reminder.isBeforeBreakfast()) {
            mealTiming.append("Before Breakfast, ");
        }
        if (reminder.isAfterBreakfast()) {
            mealTiming.append("After Breakfast, ");
        }
        if (reminder.isBeforeLunch()) {
            mealTiming.append("Before Lunch, ");
        }
        if (reminder.isAfterLunch()) {
            mealTiming.append("After Lunch, ");
        }
        if (reminder.isBeforeDinner()) {
            mealTiming.append("Before Dinner, ");
        }
        if (reminder.isAfterDinner()) {
            mealTiming.append("After Dinner, ");
        }
        if (mealTiming.length() > 0) {
            // Remove the trailing comma and space.
            mealTiming.setLength(mealTiming.length() - 2);
        } else {
            mealTiming.append("None");
        }
        holder.tvReminderBeforeAfter.setText("- " + mealTiming.toString());

        // Compute the Days string based on booleans.
        StringBuilder days = new StringBuilder();
        if (reminder.isEveryday()) {
            days.append("Everyday");
        } else {
            if (reminder.isMonday()) {
                days.append("Monday, ");
            }
            if (reminder.isTuesday()) {
                days.append("Tuesday, ");
            }
            if (reminder.isWednesday()) {
                days.append("Wednesday, ");
            }
            if (reminder.isThursday()) {
                days.append("Thursday, ");
            }
            if (reminder.isFriday()) {
                days.append("Friday, ");
            }
            if (reminder.isSaturday()) {
                days.append("Saturday, ");
            }
            if (reminder.isSunday()) {
                days.append("Sunday, ");
            }
            if (days.length() > 0) {
                days.setLength(days.length() - 2);
            } else {
                days.append("None");
            }
        }
        holder.tvReminderDays.setText("- " + days.toString());

        // Set up delete button click listener to remove the reminder from Firebase.
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Reminder")
                    .setMessage("Are you sure you want to delete this reminder?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference reminderRef = FirebaseDatabase.getInstance()
                                .getReference("MedicineReminders")
                                .child(currentUserId)
                                .child(reminder.getId());

                        reminderRef.removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    reminderList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, reminderList.size());
                                    Toast.makeText(context, "Reminder deleted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Failed to delete reminder", Toast.LENGTH_SHORT).show()
                                );
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });


    }


    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public void updateList(List<MedicineReminderModel> newList) {
        this.reminderList = newList;
        notifyDataSetChanged();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView tvReminderName, tvReminderDosage, tvReminderType, tvReminderBeforeAfter, tvReminderDays;
        ImageButton btnDelete;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReminderName = itemView.findViewById(R.id.tv_reminder_name);
            tvReminderDosage = itemView.findViewById(R.id.tv_reminder_dosage);
            tvReminderType = itemView.findViewById(R.id.tv_reminder_type);
            tvReminderBeforeAfter = itemView.findViewById(R.id.tv_reminder_before_after);
            tvReminderDays = itemView.findViewById(R.id.tv_reminder_days);
            btnDelete = itemView.findViewById(R.id.btn_med_delete);
        }
    }
}
