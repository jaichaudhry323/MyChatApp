package org.o7planning.mychatapp.Model;

public class User {

    String id;
    String username;
    String imageurl;
    String status;
    String search;
    String email;

    public User(String id, String userame, String imageurl, String status, String search) {
        this.id = id;
        this.username = userame;
        this.imageurl = imageurl;
        this.status = status;
        this.search = search;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageurl;
    }

    public void setImageURL(String imageURL) {
        this.imageurl = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
