package UserRegistration;

/**
 * The response model which stores the user's username, user type and password.
 */
public class UserRegisterResponseModel {
    private String username;
    private String userType;
    private String creationTime;

    /**
     * Instantiates a new response model.
     *
     * @param username     the user's username
     * @param userType     the user's user type
     * @param creationTime the user's creation time
     */
    public UserRegisterResponseModel(String username, String userType, String creationTime){
        this.username = username;
        this.userType = userType;
        this.creationTime = creationTime;
    }

    /**
     * Returns the user's username as a string.
     *
     * @return the user's username
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * Returns the user's user type as a string.
     *
     * @return the user's user type
     */
    public String getUserType(){
        return this.userType;
    }

}
