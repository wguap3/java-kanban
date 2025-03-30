package main.server.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import main.task.Epic;
import main.task.TaskStatus;

import java.io.IOException;

public class EpicAdapter extends TypeAdapter<Epic> {
    private final Gson gson = new GsonBuilder().create();

    @Override
    public void write(JsonWriter out, Epic epic) throws IOException {
        out.beginObject();
        out.name("id").value(epic.getId());
        out.name("name").value(epic.getName());
        out.name("describe").value(epic.getDescribe());
        out.name("status").value(epic.getStatus().toString());
        out.name("subtaskIds").value(gson.toJson(epic.getSubtaskIds()));
        out.name("duration").value(epic.getDuration().toString());
        out.name("startTime").value(epic.getStartTime() != null ? epic.getStartTime().toString() : null);
        out.name("endTime").value(epic.getEndTime() != null ? epic.getEndTime().toString() : null);
        out.endObject();
    }

    @Override
    public Epic read(JsonReader in) throws IOException {
        Epic epic = new Epic(null, null, null, null);
        in.beginObject();
        while (in.hasNext()) {
            String field = in.nextName();
            switch (field) {
                case "id":
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        epic.setId(null);
                    } else {
                        epic.setId(in.nextInt());
                    }
                    break;
                case "name":
                    epic.setName(in.nextString());
                    break;
                case "describe":
                    epic.setDescribe(in.nextString());
                    break;
                case "status":
                    String status = in.nextString();
                    epic.setStatus(TaskStatus.valueOf(status.toUpperCase()));
                    break;
                default:
                    in.skipValue();
            }
        }
        in.endObject();
        return epic;
    }
}