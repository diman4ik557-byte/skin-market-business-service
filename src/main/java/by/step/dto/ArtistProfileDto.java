package by.step.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistProfileDto {
    private Long id;
    private Long profileId;
    private String username;
    private String avatarUrl;
    private Long studioId;
    private String studioName;
    private String styles;
    private BigDecimal minPrice;
    private Integer averageTime;
    private Boolean isAvailable;
    private List<String> socialLinks;
}