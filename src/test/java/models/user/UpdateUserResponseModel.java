package models.user;

public record UpdateUserResponseModel(
        int id,
        String username,
        String firstName,
        String lastName,
        String email,
        String remoteAddr
) {}