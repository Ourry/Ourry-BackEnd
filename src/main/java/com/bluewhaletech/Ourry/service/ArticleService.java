package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.QuestionRegistrationDTO;
import com.bluewhaletech.Ourry.dto.QuestionTotalDTO;

import java.util.List;

public interface ArticleService {
    public List<QuestionTotalDTO> getQuestionList();

    public void addQuestion(QuestionRegistrationDTO dto);
}
