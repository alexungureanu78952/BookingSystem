package shareable;

import java.io.Serializable;

public class LoginCommand implements Command {
    private static final long serialVersionUID = 1L;

    public String username;
    public String password;

    public LoginCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
