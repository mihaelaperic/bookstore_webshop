package model;

public enum Category {
    VIEW_ALL("View All"),
    FICTION("Fiction"),
    NON_FICTION("Non-fiction"),
    HISTORY("History"),
    BIOGRAPHY("Romance"),
    SCIENCE("Science"),
    FANTASY("Fantasy"),
    CLASSICS("Classics");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;

    }

    public String getDisplayName() {
        return displayName;
    }

    public static Category fromString(String value) {
        if (value == null) {
            System.out.println("Category value is null");
            return null;
        }

        try {
            return Category.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid category value: " + value);
            return null;
        }
    }

    @Override
    public String toString() {
        return displayName;
    }
}
