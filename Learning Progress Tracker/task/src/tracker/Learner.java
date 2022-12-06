package tracker;

import java.util.Arrays;

public class Learner {
    private final String firstName;
    private final String lastName;
    private final String email;

    public Learner( String studentCredentials) {
        String[] credentials = studentCredentials.split(" ");
        String[] lastNameArray = Arrays.copyOfRange(credentials, 1, credentials.length-1);
        StringBuilder lname = new StringBuilder();

        this.firstName = credentials[0];
        for (String name: lastNameArray) {
            lname.append(name + " ");
        }
        this.lastName = String.valueOf(lname).trim();
        this.email = credentials[credentials.length - 1];
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

}
