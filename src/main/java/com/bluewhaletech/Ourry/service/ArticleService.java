package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.QuestionDetailDTO;
import com.bluewhaletech.Ourry.dto.QuestionRegistrationDTO;
import com.bluewhaletech.Ourry.dto.QuestionResponseDTO;
import com.bluewhaletech.Ourry.dto.QuestionTotalDTO;

import java.util.List;

public interface ArticleService {
    public List<QuestionTotalDTO> getQuestionList();

    public QuestionDetailDTO getQuestionDetail(Long questionId);

    public void addQuestion(QuestionRegistrationDTO dto);

    public void answerQuestion(QuestionResponseDTO dto);
}