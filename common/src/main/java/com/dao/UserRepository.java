package com.dao;

import com.entry.UserEntry;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface UserRepository extends PagingAndSortingRepository<UserEntry, Long> {

    List<UserEntry> findUserEntriesByUserName(String userName);

    Optional<UserEntry> findByUserNameAndPassWord(String userName, String passWord);

    Optional<UserEntry> findById(long id);
}