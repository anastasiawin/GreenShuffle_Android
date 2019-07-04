package ru.astradeni.greenshuffle;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.astradeni.greenshuffle.model.Task;

public interface TaskAPI {

    @GET("tasks/")
    Call<Task> getTask();
}
