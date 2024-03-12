package com.bluewhaletech.Ourry.controller;

import com.bluewhaletech.Ourry.dto.QuestionRegistrationDTO;
import com.bluewhaletech.Ourry.dto.QuestionResponseDTO;
import com.bluewhaletech.Ourry.dto.ReplyRegistrationDTO;
import com.bluewhaletech.Ourry.service.ArticleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/article/getQuestionDetail/{questionId}")
    public ResponseEntity<Object> getQuestionDetail(@PathVariable Long questionId) {
        return ResponseEntity.ok().body(articleService.getQuestionDetail(questionId));
    }

    @PostMapping("/article/addQuestion")
    public ResponseEntity<Object> addQuestion(@RequestBody QuestionRegistrationDTO dto) {
        articleService.addQuestion(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/article/answerQuestion")
    public ResponseEntity<Object> answerQuestion(@RequestBody QuestionResponseDTO dto) {
        articleService.answerQuestion(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/article/addReply")
    public ResponseEntity<Object> addReply(@RequestBody ReplyRegistrationDTO dto) {
        articleService.addReply(dto);
        return ResponseEntity.ok().build();
    }
}