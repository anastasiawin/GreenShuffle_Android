package ru.astradeni.greenshuffle;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.astradeni.greenshuffle.model.Task;

public class MainActivity extends AppCompatActivity {

    String url = "https://greenshuffle.herokuapp.com/tasks";
    Task currentTask = null;

    SharedPreferences sPref;
    final String SAVED_TASK_TITLE = "saved_task_title";
    final String SAVED_TASK_DESCRIPTION = "saved_task_description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentTask = loadTask();
        redrawTask();
    }

    public void onClickShuffle(View view) {
        TaskController controller = new TaskController();
        controller.start();

    }

    private void redrawTask() {
        TextView titleView = (TextView) findViewById(R.id.titleText);
        TextView descriptionView = (TextView) findViewById(R.id.descriptionText);
        titleView.setText(currentTask.getName());
        descriptionView.setText(currentTask.getDescription());
    }

    public class TaskController implements Callback<Task> {

        static final String BASE_URL = "https://greenshuffle.herokuapp.com/";

        public void start() {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            TaskAPI tasksAPI = retrofit.create(TaskAPI.class);

            Call<Task> call = tasksAPI.getTask();
            call.enqueue(this);

        }

        @Override
        public void onResponse(Call<Task> call, Response<Task> response) {
            if (response.isSuccessful()) {
                Task task = response.body();
                currentTask = task;
                redrawTask();
            } else {
                System.out.println(response.errorBody());
            }
        }

        @Override
        public void onFailure(Call<Task> call, Throwable t) {
            t.printStackTrace();
        }
    }

    private void saveTask(Task task) {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TASK_TITLE, task.getName());
        ed.putString(SAVED_TASK_DESCRIPTION, task.getDescription());

        ed.commit();
    }

    private Task loadTask() {
        sPref = getPreferences(MODE_PRIVATE);
        String currentTaskTitle = sPref.getString(SAVED_TASK_TITLE, "");
        String currentTaskDescription = sPref.getString(SAVED_TASK_DESCRIPTION, "");
        return new Task(currentTaskTitle, currentTaskDescription);
    }
}
