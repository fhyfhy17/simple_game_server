package com.dao;

import com.entry.CenterMailEntry;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
public interface CenterMailRepository extends PagingAndSortingRepository<CenterMailEntry, Long> {

}