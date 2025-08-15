package com.unicen.app.controller;

import com.unicen.app.dto.ImageResultDTO;
import com.unicen.app.dto.MetricDTO;
import com.unicen.app.model.Algorithm;
import com.unicen.app.model.Image;
import com.unicen.app.model.Metric;
import com.unicen.app.model.Result;
import com.unicen.app.service.AlgorithmService;
import com.unicen.app.service.MetricService;
import com.unicen.core.controller.GenericController;
import com.unicen.core.dto.ApiResultDTO;
import com.unicen.core.model.GenericSuccessResponse;
import com.unicen.core.model.User;
import com.unicen.core.security.GenericAuthenticationToken;
import com.unicen.core.services.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@PreAuthorize("permitAll")
@Api(tags = "10. Metrics")
@RequestMapping("/metric")
public class MetricController extends GenericController<Metric, MetricDTO> {

    @Override
    protected Class<MetricDTO> getDTOClass() {
        return MetricDTO.class;
    }

    @Override
    protected Class<Metric> getObjectClass() {
        return Metric.class;
    }

    @Autowired
    MetricService service;

    @Autowired
    UserService userService;


    @GetMapping("/getAll")
    @ResponseBody
    public ResponseEntity<ApiResultDTO<Page<MetricDTO>>> getMetrics(@RequestParam("imageExternalId") String imageExternalId, @RequestParam(defaultValue = "0") Integer page,
                                                                               @RequestParam(defaultValue = "5") Integer pageSize) throws IOException {
        GenericAuthenticationToken authentication = (GenericAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getById((long)authentication.getPrincipal());
        var metrics = service.findPageImages(imageExternalId, page, pageSize, Sort.Direction.ASC, "ratio");

        return pageResult(service.findPageImages(imageExternalId, page, pageSize, Sort.Direction.ASC, "ratio"));
    }
}
