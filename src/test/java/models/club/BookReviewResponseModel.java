package models.club;

public record BookReviewResponseModel(
        int id,
        int club,
        ReviewAuthorResponseModel user,
        String review,
        int assessment,
        int readPages,
        String created,
        String modified
) {}
