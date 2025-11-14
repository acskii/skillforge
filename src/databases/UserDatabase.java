package databases;

import models.User;
import security.PasswordService;

public class UserDatabase extends Database<User> {
    public UserDatabase(String filename) {
        super(filename, User.class);
        this.logName = "UserDatabase";
    }

    public void deleteUser(int id) {deleteRecord(id);}
    public User getUserById(int id) {return getRecordById(id);}

    public void addUser(String name, String email, String password, String role) {
        User user = new User();
        user.setId(++this.index);
        user.setRole(role);     // Validation needed here
        user.setName(name);
        user.setEmail(email);   // Validation needed here
        user.setPassword(PasswordService.encode(password));

        insertRecord(user);
    }

    public void updateUser(int id, String name, String email, String password, String role) {
        User user = getRecordById(id);
        if (user == null) {
            System.out.printf("[%s]: User attempting to update doesn't exist [ID] %d\n", logName, id);
            return;
        }

        /* Change filters */
        if (!user.getName().equals(name)) user.setName(name);

        // Validation needed here
        if (email != null && !user.getEmail().equals(email)) user.setEmail(email);

        String passwordHash = PasswordService.encode(password);
        if (password != null && !PasswordService.compare(passwordHash, user.getPassword())) user.setPassword(passwordHash);

        // Validation needed here
        if (!user.getRole().equals(role)) user.setRole(role);

        /* Remove the old one */
        deleteRecord(id);
        /* Add the changed one */
        insertRecord(user);
    }

    // TODO: login
}
