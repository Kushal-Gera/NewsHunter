package kushal.application.newshunter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class DeleteCache extends Worker {

    private Context c;

    public DeleteCache(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        c = context;

    }

    @NonNull
    @Override
    public Result doWork() {

        c.getSharedPreferences("shared_pref", Context.MODE_PRIVATE)
                .edit().putString("data", "none").apply();


        return Result.success();
    }
}
