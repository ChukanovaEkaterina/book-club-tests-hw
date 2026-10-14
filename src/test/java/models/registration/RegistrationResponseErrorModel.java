package models.registration;

import java.util.List;

public record RegistrationResponseErrorModel (List<String> username, List<String> password) {}
