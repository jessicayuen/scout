package scout.scoutmobile.models;


public class User {

    private static User currentUser = null;

    private String email;
    private String firstName;
    private String lastName;
    private String passWord;
    private int points;

    public static User getInstance() {
        if (currentUser == null) {
            currentUser = new User();
        }
        return currentUser;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getEmail() {

        return email;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void resetUser() {
        this.email = this.firstName = this.lastName = this.passWord = "";
        this.points = -1;
    }

    private User() {
        resetUser();
    }
}
