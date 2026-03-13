package com.unicen.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class StatsDTO {
    private long users;
    private long images;
    private long results;
    private long metrics;
    private long jpgImages;
    private long pngImages;
    private long bmpImages;
    private List<Long> imagesPerDay;
}
