package src.server.model;

public class User {
    private final String name;
    private final String password;
    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.name.equals(user.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
