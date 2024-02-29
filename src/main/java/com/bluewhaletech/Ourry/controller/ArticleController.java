package com.bluewhaletech.Ourry.controller;

import com.bluewhaletech.Ourry.dto.QuestionRegistrationDTO;
import com.bluewhaletech.Ourry.service.ArticleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ArticleController {
    private final ArticleServiceImpl articleService;

    @Autowired
    public ArticleController(ArticleServiceImpl articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/article/getQuestionList")
    public ResponseEntity<Object> getQuestionList() {
        return ResponseEntity.ok().body(articleService.getQuestionList());
    }

    @PostMapping("/article/addQuestion")
    public ResponseEntity<Object> addQuestion(@RequestBody QuestionRegistrationDTO dto) {
        articleService.addQuestion(dto);
        return ResponseEntity.ok().build();
    }
}