package org.o7planning.mychatapp.Utils;

import java.util.ArrayList;

public class Global {

    private String email;
    private String password;
    private ArrayList<String> invalidEmails = new ArrayList<>();
    // Getter/setter

    private static Global instance;

    public static Global getInstance() {
        if (instance == null)
            instance = new Global();
        return instance;
    }

    private Global() {
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addEmail(String email) {
        if (!invalidEmails.contains(email))
            invalidEmails.add(email);
    }

    public void removeEmail(String email) {
        if (invalidEmails.contains(email))
            invalidEmails.remove(email);
    }

    public ArrayList<String> getInvalidEmails() {
        return invalidEmails;
    }
}
