package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.*;

import java.util.List;

public interface ArticleService {
    List<QuestionListDTO> searchQuestionList(String searchKeyword);

    List<QuestionListDTO> getQuestionList();

    List<QuestionListDTO> getQuestionList(Long categoryId);

    QuestionDetailDTO getQuestionDetail(String email, Long questionId);

    void addQuestion(String email, QuestionRegistrationDTO dto);

    void answerQuestion(String email, QuestionResponseDTO dto);

    void addReply(String email, ReplyRegistrationDTO dto);
}