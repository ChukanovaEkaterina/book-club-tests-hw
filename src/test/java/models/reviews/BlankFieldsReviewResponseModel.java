package models.reviews;

import java.util.List;

public record BlankFieldsReviewResponseModel(List<String> club, List<String> review,
                                             List<String> assessment, List<String> readPages) {}
