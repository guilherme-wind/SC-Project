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

    /**
     * Gets a string representation of the User object.
     * @return
     *      String representation of User.
     */
    public String parseToSerial() {
        return String.format("%s:%s", this.name, this.password);
    }

    /**
     * Creates an User from string with the format
     * <username>:<password>
     * @param serial
     *      String representation of a user.
     * @return
     *      User object or null if the string doesn't have
     *      the correct format.
     */
    public static User parseFromSerial(String serial) {
        User parsedUser = null;
        String[] tokens = serial.split(":", 2);
        if (tokens.length >= 2)
            parsedUser = new User(tokens[0], tokens[1]); 

        return parsedUser;
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
