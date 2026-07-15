package com.ptmharsha.urlshortener.repository;

import com.ptmharsha.urlshortener.entity.UrlClick;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlClickRepository
        extends JpaRepository<UrlClick, Long> {

    long countByUrlMappingId(Long id);

}