package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Setter
@Getter
public  final class Url {
    private Long id;
    private String name;
    private Timestamp createdAt;

    public Url() { }

    public Url(String name) {
        this.name = name;
    }

    public String getFormattedCreatedAt() {
        if (createdAt == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return sdf.format(createdAt);
    }
}
