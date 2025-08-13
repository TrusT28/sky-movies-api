package rustam.fadeev.sky_movies_api.models;

import org.springframework.data.domain.Page;
import rustam.fadeev.sky_movies_api.entities.RatingEntity;

import java.util.List;

public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
    public PageResponse(Page<T> page) {
        this(page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}