package service;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private final UserService service = new UserService();

    @BeforeEach
    public void setup() throws Exception {
        new ClearService().clearApplication();
    }

    @Test
    public void testRegisterPass() throws Exception {
        UserData user = new UserData("konner","password","k@gmail.com");
        AuthData auth = service.register(user);

        assertEquals("konner",auth.username());
        assertNotNull(auth.authToken());
    }

    @Test
    public void testLoginPass() throws Exception {
        UserData user = new UserData("konner","password","k@gmail.com");
        service.register(user);

        AuthData auth = service.login(user);

        assertEquals("konner",auth.username());
        assertNotNull(auth.authToken());
    }

    @Test
    public void testLogoutPass() throws Exception {
        UserData user = new UserData("konner","password","k@gmail.com");
        service.register(user);

        AuthData auth = service.login(user);

        assertDoesNotThrow(()->service.logout(auth.authToken()));
    }


    @Test
    public void testRegisterFail() throws Exception {
        UserData user = new UserData("konner","password","k@gmail.com");
        AuthData auth = service.register(user);

        assertThrows(Exception.class,()->service.register(user));
    }

    @Test
    public void testLoginFail() throws Exception {
        UserData user = new UserData("konner","password","k@gmail.com");
        AuthData auth = service.register(user);

        assertThrows(Exception.class,()->service.login(null));
    }

    @Test
    public void testLogoutFail() throws Exception {
        String badToken = "1234";
        assertThrows(Exception.class,()->service.logout(badToken));
    }
}
