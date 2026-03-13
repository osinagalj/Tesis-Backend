package com.unicen.app.controller;

import com.unicen.app.dto.StatsDTO;
import com.unicen.app.service.ImageService;
import java.util.List;
import com.unicen.app.service.MetricService;
import com.unicen.app.service.ResultService;
import com.unicen.core.dto.ApiResultDTO;
import com.unicen.core.services.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@PreAuthorize("permitAll")
@Api(tags = "11. Stats")
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private MetricService metricService;

    @GetMapping("/summary")
    @ResponseBody
    public ResponseEntity<ApiResultDTO<StatsDTO>> getSummary() {
        StatsDTO stats = new StatsDTO(
                userService.count(),
                imageService.count(),
                resultService.count(),
                imageService.countWithResults(),
                imageService.countByType(List.of("jpg", "jpeg")),
                imageService.countByType(List.of("png")),
                imageService.countByType(List.of("bmp")),
                imageService.countImagesLastDays(7)
        );
        return ResponseEntity.ok(ApiResultDTO.ofSuccess(stats));
    }
}
