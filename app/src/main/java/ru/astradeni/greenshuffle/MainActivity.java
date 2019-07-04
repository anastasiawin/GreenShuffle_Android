package ru.astradeni.greenshuffle;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.astradeni.greenshuffle.model.Task;

public class MainActivity extends AppCompatActivity {

    String url = "https://greenshuffle.herokuapp.com/tasks";
    String currentTask = "";
    SharedPreferences sPref;
    final String SAVED_TASK = "saved_task";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentTask = loadTask();
        redrawTask();
        Button btnGetTask = (Button) findViewById(R.id.button);
        btnGetTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTask();
            }
        });
    }

    private void getTask() {
        TaskController controller = new TaskController();
        controller.start();

    }

    private void redrawTask() {
        TextView btnGetTask = (TextView) findViewById(R.id.textView);
        btnGetTask.setText(currentTask);
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
                currentTask = task.getName();
                saveTask(task.getName());
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

    private void saveTask(String taskName) {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TASK, taskName);
        ed.commit();
    }

    private String loadTask() {
        sPref = getPreferences(MODE_PRIVATE);
        String savedTask = sPref.getString(SAVED_TASK, "");
        return savedTask;
    }
}
