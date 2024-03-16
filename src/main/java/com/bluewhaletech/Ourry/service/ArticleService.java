package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.*;

import java.util.List;

public interface ArticleService {
    List<QuestionListDTO> getQuestionList();

    List<QuestionListDTO> getQuestionList(Long categoryId);

    QuestionDetailDTO getQuestionDetail(Long memberId, Long questionId);

    void addQuestion(QuestionRegistrationDTO dto);

    void answerQuestion(QuestionResponseDTO dto);

    void addReply(ReplyRegistrationDTO dto);
}