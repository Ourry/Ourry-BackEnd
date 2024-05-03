package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.*;

import java.util.List;

public interface ArticleService {
    List<QuestionListDTO> getQuestionList();

    List<QuestionListDTO> getQuestionList(Long categoryId);

    QuestionDetailDTO getQuestionDetail(String accessToken, Long questionId);

    List<QuestionListDTO> searchQuestionList(String searchKeyword);

    void addQuestion(String accessToken, QuestionRegistrationDTO dto);

    void answerQuestion(String accessToken, QuestionResponseDTO dto);

    void addReply(String accessToken, ReplyRegistrationDTO dto);

    void setAlarmOnQuestion(String accessToken, AlarmSettingDTO dto);
}