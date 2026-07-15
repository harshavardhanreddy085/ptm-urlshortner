package com.ptmharsha.urlshortener.service;

import com.ptmharsha.urlshortener.entity.UrlMapping;
import com.ptmharsha.urlshortener.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlMappingPersister {

    private final UrlMappingRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UrlMapping save(UrlMapping mapping) {
        return repository.saveAndFlush(mapping);
    }
}
