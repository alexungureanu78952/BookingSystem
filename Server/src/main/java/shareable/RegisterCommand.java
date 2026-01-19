package shareable;

import java.io.Serializable;

public class RegisterCommand implements Command {
    private static final long serialVersionUID = 1L;

    public String username;
    public String password;
    public String email;
    public String fullName;

    public RegisterCommand(String username, String password, String email, String fullName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
    }
}
