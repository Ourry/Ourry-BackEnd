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
import java.util.Objects;
import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final ChoiceRepository choiceRepository;
    private final ChoiceJpaRepository choiceJpaRepository;
    private final PollRepository pollRepository;
    private final PollJpaRepository pollJpaRepository;
    private final SolutionRepository solutionRepository;
    private final SolutionJpaRepository solutionJpaRepository;
    private final ReplyJpaRepository replyJpaRepository;

    @Autowired
    public ArticleServiceImpl(MemberRepository memberRepository, QuestionRepository questionRepository, CategoryRepository categoryRepository, ChoiceRepository choiceRepository, ChoiceJpaRepository choiceJpaRepository, PollRepository pollRepository, PollJpaRepository pollJpaRepository, SolutionRepository solutionRepository, SolutionJpaRepository solutionJpaRepository, ReplyJpaRepository replyJpaRepository) {
        this.memberRepository = memberRepository;
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.choiceRepository = choiceRepository;
        this.choiceJpaRepository = choiceJpaRepository;
        this.pollRepository = pollRepository;
        this.pollJpaRepository = pollJpaRepository;
        this.solutionRepository = solutionRepository;
        this.solutionJpaRepository = solutionJpaRepository;
        this.replyJpaRepository = replyJpaRepository;
    }

    @Override
    public List<QuestionTotalDTO> getQuestionList() {
        List<QuestionTotalDTO> list = new ArrayList<>();
        List<Question> questions = Optional.ofNullable(questionRepository.findAll())
                .orElseThrow(() -> new QuestionLoadingFailedException("질문 목록을 불러오는 과정에서 오류가 발생했습니다."));
        for(Question question : questions) {
            /* 질문별 투표 데이터 목록 */
            List<Poll> polls = Optional.ofNullable(pollJpaRepository.findByQuestion(question))
                    .orElseThrow(() -> new PollNotFoundException("투표 데이터를 불러오는 과정에서 오류가 발생했습니다."));

            /* 질문별 솔루션 총합 */
            int solutionCnt = 0;
            for(Poll poll : polls) {
                solutionCnt += solutionJpaRepository.countByPoll(poll);
            }

            /* 질문별 답글 총합 */
            int replyCnt = 0;
            for(Poll poll : polls) {
                replyCnt += replyJpaRepository.countByPoll(poll);
            }

            QuestionTotalDTO dto = QuestionTotalDTO.builder()
                    .title(question.getTitle())
                    .content(question.getContent())
                    .nickname(question.getMember().getNickname())
                    .createdAt(question.getCreatedAt())
                    .pollCnt(polls.size())
                    .responseCnt(solutionCnt+replyCnt)
                    .build();
            list.add(dto);
        }
        return list;
    }

    @Override
    public QuestionDetailDTO getQuestionDetail(Long questionId) {
        Question question = Optional.ofNullable(questionRepository.findOne(questionId))
                .orElseThrow(() -> new QuestionNotFoundException("질문 정보가 존재하지 않습니다."));

        /* 질문별 선택지 데이터 목록 */
        List<ChoiceDTO> choices = new ArrayList<>();
        for(Choice choice : question.getChoices()) {
            ChoiceDTO c = ChoiceDTO.builder()
                    .sequence(choice.getSequence())
                    .detail(choice.getDetail())
                    .count(pollJpaRepository.countByQuestionAndChoice(question, choice))
                    .build();
            choices.add(c);
        }

        /* 질문별 투표 데이터 목록 */
        List<Poll> polls = Optional.ofNullable(pollJpaRepository.findByQuestion(question))
                .orElseThrow(() -> new PollNotFoundException("투표 데이터가 존재하지 않습니다."));

        /* 투표별 솔루션 데이터 목록 */
        List<SolutionDTO> solutions = new ArrayList<>();
        for(Poll poll : polls) {
            Solution solution = solutionJpaRepository.findByPoll(poll);
            if(Optional.ofNullable(solution).isPresent()) {
                SolutionDTO s = SolutionDTO.builder()
                        .memberId(poll.getMember().getMemberId())
                        .nickname(poll.getMember().getNickname())
                        .sequence(poll.getChoice().getSequence())
                        .opinion(solution.getOpinion())
                        .createdAt(poll.getCreatedAt())
                        .build();
                solutions.add(s);
            }
        }

        /* 투표별 답글 데이터 목록 */
        List<ReplyDTO> replies = new ArrayList<>();
        for(Poll poll : polls) {
            Reply reply = replyJpaRepository.findByPoll(poll);
            if(Optional.ofNullable(reply).isPresent()) {
                ReplyDTO r = ReplyDTO.builder()
                        .comment(reply.getComment())
                        .nickname(reply.getMember().getNickname())
                        .createdAt(reply.getCreatedAt())
                        .build();
                replies.add(r);
            }
        }

        return QuestionDetailDTO.builder()
                .title(question.getTitle())
                .content(question.getContent())
                .category(question.getCategory().getName())
                .nickname(question.getMember().getNickname())
                .pollCnt(polls.size())
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
        Member member = Optional.ofNullable(memberRepository.findOne(dto.getMemberId()))
                .orElseThrow(() -> new MemberNotFoundException("회원 정보가 존재하지 않습니다."));

        Category category = Optional.ofNullable(categoryRepository.findOne(dto.getCategoryId()))
                .orElseThrow(() -> new CategoryNotFoundException("존재하지 않는 카테고리입니다."));

        Question question = Question.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .member(member)
                .category(category)
                .build();
        questionRepository.save(question);

        for(ChoiceDTO item : dto.getChoices()) {
            Choice choice = Choice.builder()
                    .sequence(item.getSequence())
                    .detail(item.getDetail())
                    .question(question)
                    .build();
            choiceRepository.save(choice);
        }
    }

    @Override
    @Transactional
    public void answerQuestion(QuestionResponseDTO dto) {
        /* 회원 존재유무 확인 */
        Member member = Optional.ofNullable(memberRepository.findOne(dto.getMemberId()))
                .orElseThrow(() -> new MemberNotFoundException("회원 정보가 존재하지 않습니다."));
        
        /* 질문 존재유무 확인 */
        Question question = Optional.ofNullable(questionRepository.findOne(dto.getQuestionId()))
                .orElseThrow(() -> new QuestionNotFoundException("질문 정보가 존재하지 않습니다."));

        /* 자문자답여부 확인 */
        if(Objects.equals(dto.getMemberId(), question.getMember().getMemberId())) {
            throw new AnswerToOneselfException("본인이 질문한 글에는 의견을 제출할 수 없습니다.");
        }

        /* 선택지 존재유무 확인 */
        if(!choiceJpaRepository.existsByQuestionAndSequence(question, dto.getSequence())) {
            throw new ChoiceNotFoundException("선택지 정보가 존재하지 않습니다.");
        }

        /* 답변 작성유무 확인 */
        if(pollJpaRepository.existsByMemberAndQuestion(member, question)) {
            throw new QuestionAlreadyAnsweredException("해당 질문에 대해 답변한 기록이 존재합니다.");
        }

        Choice choice = choiceRepository.findByQuestionAndSequence(question, dto.getSequence());

        Poll poll = Poll.builder()
                .member(member)
                .question(question)
                .choice(choice)
                .build();
        pollRepository.save(poll);

        String opinion = dto.getOpinion();
        if(opinion != null) {
            Solution solution = Solution.builder()
                    .opinion(opinion)
                    .poll(poll)
                    .build();
            solutionRepository.save(solution);
        }
    }
}