package com.bluewhaletech.Ourry.controller;

import com.bluewhaletech.Ourry.service.FcmServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class FcmController {
    private final FcmServiceImpl fcmServiceImpl;

    @Autowired
    public FcmController(FcmServiceImpl fcmServiceImpl) {
        this.fcmServiceImpl = fcmServiceImpl;
    }
}