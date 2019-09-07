package com.controller;

import com.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class BagController extends BaseController {
    @Autowired
    private PlayerService playerService;


}
