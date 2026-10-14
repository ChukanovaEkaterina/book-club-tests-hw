package models.login;

import java.util.List;

public record LoginResponseErrorModel(List<String> username, List<String> password, String detail) {}