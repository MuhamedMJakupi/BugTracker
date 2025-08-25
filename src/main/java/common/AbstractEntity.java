package common;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;
import java.util.List;
import java.util.UUID;

public abstract class AbstractEntity {

    @SerializedName("id")
    private UUID id;

    public abstract List<String> validate();
    public abstract List<String> validateForCreation();
    public abstract List<String> validateForUpdate();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AbstractEntity that = (AbstractEntity) obj;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
