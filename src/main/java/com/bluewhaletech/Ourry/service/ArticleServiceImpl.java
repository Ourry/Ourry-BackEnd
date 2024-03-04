package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.domain.*;
import com.bluewhaletech.Ourry.dto.*;
import com.bluewhaletech.Ourry.exception.*;
import com.bluewhaletech.Ourry.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final ChoiceRepository choiceRepository;
    private final SolutionRepository solutionRepository;
    private final SolutionJpaRepository solutionJpaRepository;
    private final ReplyJpaRepository replyJpaRepository;

    @Autowired
    public ArticleServiceImpl(MemberRepository memberRepository, QuestionRepository questionRepository, CategoryRepository categoryRepository, ChoiceRepository choiceRepository, SolutionRepository solutionRepository, SolutionJpaRepository solutionJpaRepository, ReplyJpaRepository replyJpaRepository) {
        this.memberRepository = memberRepository;
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.choiceRepository = choiceRepository;
        this.solutionRepository = solutionRepository;
        this.solutionJpaRepository = solutionJpaRepository;
        this.replyJpaRepository = replyJpaRepository;
    }

    @Override
    public List<QuestionTotalDTO> getQuestionList() {
        List<QuestionTotalDTO> list = new ArrayList<>();
        List<Question> questions = questionRepository.findAll()
                .orElseThrow(() -> new QuestionLoadingFailedException("질문 목록을 불러오는 과정에서 오류가 발생했습니다."));
        for(Question question : questions) {
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
    public QuestionDetailDTO getQuestionDetail(Long questionId) {
        Question question = questionRepository.findOne(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("질문 상세정보를 불러오는 과정에서 오류가 발생했습니다."));

        List<ChoiceDTO> choices = new ArrayList<>();
        List<SolutionDTO> solutions = new ArrayList<>();
        List<ReplyDTO> replies = new ArrayList<>();
        for(Choice choice : question.getChoices()) {
            ChoiceDTO c = ChoiceDTO.builder()
                    .detail(choice.getDetail())
                    .seq(choice.getSeq())
                    .build();
            choices.add(c);

            Solution solution = solutionJpaRepository.findByChoice(choice);
            SolutionDTO s = SolutionDTO.builder()
                    .opinion(solution.getOpinion())
                    .nickname(solution.getMember().getNickname())
                    .createdAt(solution.getCreatedAt())
                    .build();
            solutions.add(s);

            Reply reply = replyJpaRepository.findBySolution(solution);
            ReplyDTO r = ReplyDTO.builder()
                    .comment(reply.getComment())
                    .seq(reply.getSeq())
                    .nickname(reply.getMember().getNickname())
                    .createdAt(reply.getCreatedAt())
                    .build();
            replies.add(r);
        }

        return QuestionDetailDTO.builder()
                .title(question.getTitle())
                .content(question.getContent())
                .nickname(question.getMember().getNickname())
                .category(question.getCategory().getName())
                .solutionCnt(solutions.size())
                .responseCnt(solutions.size()+replies.size())
                .createdAt(question.getCreatedAt())
                .choices(choices)
                .solutions(solutions)
                .replies(replies)
                .build();
    }

    @Override
    @Transactional
    public void addQuestion(QuestionRegistrationDTO dto) {
        Member member = memberRepository.findOne(dto.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("회원 정보가 존재하지 않습니다."));

        Category category = categoryRepository.findOne(dto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("존재하지 않는 카테고리입니다."));

        Question question = Question.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .isSolved('N')
                .member(member)
                .category(category)
                .build();
        questionRepository.save(question);

        for(ChoiceDTO item : dto.getChoices()) {
            Choice choice = Choice.builder()
                .detail(item.getDetail())
                .seq(item.getSeq())
                .question(question)
                .build();
            choiceRepository.save(choice);
        }
    }

    @Override
    @Transactional
    public void answerQuestion(QuestionResponseDTO dto) {
        Member member = memberRepository.findOne(dto.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("회원 정보가 존재하지 않습니다."));

        Choice choice = choiceRepository.findOne(dto.getChoiceId())
                .orElseThrow(() -> new ChoiceNotFoundException("선택지 정보를 불러올 수 없습니다."));

        Solution solution = Solution.builder()
                .opinion(dto.getOpinion())
                .member(member)
                .choice(choice)
                .build();

        questionRepository.findByMemberId(member.getMemberId());


        solutionRepository.save(solution);
    }
}