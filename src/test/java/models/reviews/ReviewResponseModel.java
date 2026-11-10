package models.reviews;

import models.club.ReviewAuthorResponseModel;

public record ReviewResponseModel(
        int id,
        int club,
        ReviewAuthorResponseModel user,
        String review,
        int assessment,
        int readPages,
        String created,
        String modified
) {}
