package models.club;

import java.util.List;

public record ClubListResponseModel(
        int count,
        String next,
        String previous,
        List<ClubResponseModel> results
) {}
