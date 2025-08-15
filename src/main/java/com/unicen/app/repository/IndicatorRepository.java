package com.unicen.app.repository;

import com.unicen.app.model.Image;
import com.unicen.app.model.Indicator;
import com.unicen.app.model.Metric;
import com.unicen.app.model.Result;
import com.unicen.core.repositories.PublicObjectRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IndicatorRepository extends PublicObjectRepository<Indicator, Long> {


}
