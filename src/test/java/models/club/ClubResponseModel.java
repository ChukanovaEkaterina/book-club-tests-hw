package models.club;

import java.util.List;
import models.reviews.ReviewResponseModel;

public record ClubResponseModel(
        int id,
        String bookTitle,
        String bookAuthors,
        int publicationYear,
        String description,
        String telegramChatLink,
        int owner,
        List<Integer> members,
        List<ReviewResponseModel> reviews,
        String created,
        String modified
) {}
