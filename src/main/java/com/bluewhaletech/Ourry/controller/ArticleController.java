package com.bluewhaletech.Ourry.controller;

import com.bluewhaletech.Ourry.dto.QuestionRegistrationDTO;
import com.bluewhaletech.Ourry.dto.QuestionResponseDTO;
import com.bluewhaletech.Ourry.dto.ReplyRegistrationDTO;
import com.bluewhaletech.Ourry.service.ArticleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/article/getQuestionList/{categoryId}")
    public ResponseEntity<Object> getQuestionList(@PathVariable Long categoryId) {
        return ResponseEntity.ok().body(articleService.getQuestionList(categoryId));
    }

    @GetMapping("/article/getQuestionDetail")
    public ResponseEntity<Object> getQuestionDetail(@RequestParam(value = "memberId") Long memberId, @RequestParam(value = "questionId") Long questionId) {
        return ResponseEntity.ok().body(articleService.getQuestionDetail(memberId, questionId));
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