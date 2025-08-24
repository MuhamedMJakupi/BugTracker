package common.enums;

public enum IssueStatus {
    TODO(1, "TODO"),
    IN_PROGRESS(2, "IN_PROGRESS"),
    DONE(3, "DONE"),
    CANCELLED(4, "CANCELLED");

    private final int id;
    private final String name;

    IssueStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public static IssueStatus fromId(int id) {
        for (IssueStatus status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status ID: " + id);
    }

    public static IssueStatus fromName(String name) {
        for (IssueStatus status : values()) {
            if (status.name.equals(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status name: " + name);
    }

}

