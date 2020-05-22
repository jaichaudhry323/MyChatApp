package org.o7planning.mychatapp.Model;

public class Chat {

    private String sender;
    String receiver;
    String message;
    Boolean isseen;

    public Chat(String sender, String receiver, String message, Boolean isseen) {
        this.isseen = isseen;
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Chat() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
