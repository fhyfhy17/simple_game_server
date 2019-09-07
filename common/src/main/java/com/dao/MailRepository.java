package com.dao;

import com.entry.MailEntry;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
public interface MailRepository extends PagingAndSortingRepository<MailEntry, Long> {

}