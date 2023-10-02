package model;

public enum Language {
    VIEW_ALL("View All"),
    HR("Croatian"),
    EN("English"),
    DE("German"),
    FR("French");


    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
