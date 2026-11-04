package api;

public class ApiClient {
    public final AuthApiClient auth = new AuthApiClient();
    public final RegistrationApiClient registration = new RegistrationApiClient();
    public final UsersApiClient users = new UsersApiClient();
}
