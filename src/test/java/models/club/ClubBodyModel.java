package models.club;

public record ClubBodyModel(
        String bookTitle,
        String bookAuthors,
        int publicationYear,
        String description,
        String telegramChatLink
) {}
