package models.club;

import java.util.List;

public record ClubResponseModel(
        int id,
        String bookTitle,
        String bookAuthors,
        int publicationYear,
        String description,
        String telegramChatLink,
        int owner,
        List<Integer> members,
        List<BookReviewResponseModel> reviews,
        String created,
        String modified
) {}
