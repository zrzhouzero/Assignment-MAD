package model;

public class UserImpl extends AbstractUser {

    private String userName;
    private String password;

    public UserImpl(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public boolean validatePassword(String str) {
        return str.equals(password);
    }

}
