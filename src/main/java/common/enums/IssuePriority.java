package common.enums;

public enum IssuePriority {
    LOW(1, "LOW"),
    MEDIUM(2, "MEDIUM"),
    HIGH(3, "HIGH"),
    CRITICAL(4, "CRITICAL");

    private final int id;
    private final String name;

    IssuePriority(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public static IssuePriority fromId(int id) {
        for (IssuePriority priority : values()) {
            if (priority.id == id) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid priority ID: " + id);
    }

    public static IssuePriority fromName(String name) {
        for (IssuePriority priority : values()) {
            if (priority.name.equals(name)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid priority name: " + name);
    }

    public boolean isHigherThan(IssuePriority other) {
        return this.id > other.id;
    }

    public boolean isLowerThan(IssuePriority other) {
        return this.id < other.id;
    }
}

