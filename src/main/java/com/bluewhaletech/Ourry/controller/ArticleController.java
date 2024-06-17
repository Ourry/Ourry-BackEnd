package com.bluewhaletech.Ourry.controller;

import com.bluewhaletech.Ourry.dto.AlarmSettingDTO;
import com.bluewhaletech.Ourry.dto.QuestionRegistrationDTO;
import com.bluewhaletech.Ourry.dto.QuestionResponseDTO;
import com.bluewhaletech.Ourry.dto.ReplyRegistrationDTO;
import com.bluewhaletech.Ourry.service.ArticleServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<Object> getQuestionList(@PathVariable int categoryId) {
        return ResponseEntity.ok().body(articleService.getQuestionList(categoryId));
    }

    @GetMapping("/article/getQuestionDetail")
    public ResponseEntity<Object> getQuestionDetail(HttpServletRequest request, @RequestParam(value = "questionId") int questionId) {
        String accessToken = request.getHeader("Authorization");
        return ResponseEntity.ok().body(articleService.getQuestionDetail(accessToken, questionId));
    }

    @GetMapping("/article/searchQuestionList")
    public ResponseEntity<Object> searchQuestionList(@RequestParam String searchKeyword) {
        return ResponseEntity.ok().body(articleService.searchQuestionList(searchKeyword));
    }

    @PostMapping("/article/addQuestion")
    public ResponseEntity<Object> addQuestion(HttpServletRequest request, @RequestBody QuestionRegistrationDTO dto) {
        String accessToken = request.getHeader("Authorization");
        articleService.addQuestion(accessToken, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/article/answerQuestion")
    public ResponseEntity<Object> answerQuestion(HttpServletRequest request, @RequestBody QuestionResponseDTO dto) {
        String accessToken = request.getHeader("Authorization");
        articleService.answerQuestion(accessToken, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/article/addReply")
    public ResponseEntity<Object> addReply(HttpServletRequest request, @RequestBody ReplyRegistrationDTO dto) {
        String accessToken = request.getHeader("Authorization");
        articleService.addReply(accessToken, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/article/setAlarmOnQuestion")
    public ResponseEntity<Object> setAlarmOnQuestion(HttpServletRequest request, @RequestBody AlarmSettingDTO dto) {
        String accessToken = request.getHeader("Authorization");
        articleService.setAlarmOnQuestion(accessToken, dto);
        return ResponseEntity.ok().build();
    }
}