package java8.concurrent.dbaccess.model;

import java.util.Date;
import java.util.List;


public final class Mention {

    private final long id;
    private final Date created_at;
    private final String text;
    private final User fromUser;
    private final List<User> users;

    public Mention(long id, Date created_at, String text, User fromUser, List<User> users) {
        this.id = id;
        this.created_at = created_at;
        this.text = text;
        this.fromUser = fromUser;
        this.users = users;
    }

    @Override
    public String toString() {
        return "Mention [created_at=" + created_at + ", from=" + fromUser.getName() + "]";
    }

    public long getId() {
        return id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public String getText() {
        return text;
    }

    public User getFromUser() {
        return fromUser;
    }

    public List<User> getUsers() {
        return users;
    }
    
    
}
