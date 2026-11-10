package models.reviews;

import java.util.List;

public record ReviewsListResponseModel(int count, String next, String previous,
                                      List<ReviewResponseModel> results) {}
