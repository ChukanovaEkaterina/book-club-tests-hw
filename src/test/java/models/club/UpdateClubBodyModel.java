package models.club;

public record UpdateClubBodyModel(
        String bookTitle,
        String bookAuthors,
        int publicationYear,
        String description,
        String telegramChatLink
) {}
