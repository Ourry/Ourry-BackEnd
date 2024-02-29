package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.domain.*;
import com.bluewhaletech.Ourry.dto.*;
import com.bluewhaletech.Ourry.exception.CategoryNotFoundException;
import com.bluewhaletech.Ourry.exception.MemberNotFoundException;
import com.bluewhaletech.Ourry.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final ChoiceRepository choiceRepository;
    private final SolutionJpaRepository solutionJpaRepository;
    private final ReplyJpaRepository replyJpaRepository;

    @Autowired
    public ArticleServiceImpl(MemberRepository memberRepository, ArticleRepository articleRepository, CategoryRepository categoryRepository, ChoiceRepository choiceRepository, SolutionJpaRepository solutionJpaRepository, ReplyJpaRepository replyJpaRepository) {
        this.memberRepository = memberRepository;
        this.articleRepository = articleRepository;
        this.categoryRepository = categoryRepository;
        this.choiceRepository = choiceRepository;
        this.solutionJpaRepository = solutionJpaRepository;
        this.replyJpaRepository = replyJpaRepository;
    }

    @Override
    public List<QuestionTotalDTO> getQuestionList() {
        List<QuestionTotalDTO> list = new ArrayList<>();
        for(Question question : articleRepository.findAll().stream().toList()) {
            List<Solution> solutions = new ArrayList<>();
            for(Choice choice : question.getChoices()) {
                solutions.add(solutionJpaRepository.findByChoice(choice));
            }

            int replyCnt = 0;
            for(Solution solution : solutions) {
                replyCnt += replyJpaRepository.countBySolution(solution);
            }

            QuestionTotalDTO dto = QuestionTotalDTO.builder()
                    .title(question.getTitle())
                    .content(question.getContent())
                    .nickname(question.getMember().getNickname())
                    .createdAt(question.getCreatedAt())
                    .solutionCnt(solutions.size())
                    .responseCnt(solutions.size()+replyCnt)
                    .build();
            list.add(dto);
        }
        return list;
    }

    @Override
    @Transactional
    public void addQuestion(QuestionRegistrationDTO dto) {
        Member member = memberRepository.findOne(dto.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));

        Category category = categoryRepository.findOne(dto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("존재하지 않는 카테고리입니다."));

        Question question = Question.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .isSolved('N')
                .member(member)
                .category(category)
                .build();
        articleRepository.save(question);

        for(ChoiceDTO item : dto.getChoices()) {
            Choice choice = Choice.builder()
                .detail(item.getDetail())
                .seq(item.getSeq())
                .question(question)
                .build();
            choiceRepository.save(choice);
        }
    }
}