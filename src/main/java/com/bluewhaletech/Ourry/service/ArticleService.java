package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.*;

import java.util.List;

public interface ArticleService {
    List<QuestionTotalDTO> getQuestionList();

    QuestionDetailDTO getQuestionDetail(Long questionId);

    void addQuestion(QuestionRegistrationDTO dto);

    void answerQuestion(QuestionResponseDTO dto);

    void addReply(ReplyRegistrationDTO dto);
}