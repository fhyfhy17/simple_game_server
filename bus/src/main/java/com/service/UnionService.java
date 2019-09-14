package com.service;

import com.entry.UnionEntry;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@Data
//工会service
public class UnionService {

    private Map<Long, UnionEntry> unionMap = Maps.newHashMap();

    public void getUnionList() {

    }

    public boolean addToUnion(Long unionId) {
        return true;
    }


}
