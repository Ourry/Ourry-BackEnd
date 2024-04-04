package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.domain.*;
import com.bluewhaletech.Ourry.dto.*;
import com.bluewhaletech.Ourry.exception.*;
import com.bluewhaletech.Ourry.repository.*;
import jakarta.servlet.http.HttpServletRequest;
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
    private final QuestionJpaRepository questionJpaRepository;
    private final CategoryRepository categoryRepository;
    private final ChoiceRepository choiceRepository;
    private final ChoiceJpaRepository choiceJpaRepository;
    private final PollRepository pollRepository;
    private final PollJpaRepository pollJpaRepository;
    private final SolutionRepository solutionRepository;
    private final SolutionJpaRepository solutionJpaRepository;
    private final ReplyRepository replyRepository;
    private final ReplyJpaRepository replyJpaRepository;
    private final MemberJpaRepository memberJpaRepository;

    @Autowired
    public ArticleServiceImpl(MemberRepository memberRepository, QuestionRepository questionRepository, QuestionJpaRepository questionJpaRepository, CategoryRepository categoryRepository, ChoiceRepository choiceRepository, ChoiceJpaRepository choiceJpaRepository, PollRepository pollRepository, PollJpaRepository pollJpaRepository, SolutionRepository solutionRepository, SolutionJpaRepository solutionJpaRepository, ReplyRepository replyRepository, ReplyJpaRepository replyJpaRepository, MemberJpaRepository memberJpaRepository) {
        this.memberRepository = memberRepository;
        this.questionRepository = questionRepository;
        this.questionJpaRepository = questionJpaRepository;
        this.categoryRepository = categoryRepository;
        this.choiceRepository = choiceRepository;
        this.choiceJpaRepository = choiceJpaRepository;
        this.pollRepository = pollRepository;
        this.pollJpaRepository = pollJpaRepository;
        this.solutionRepository = solutionRepository;
        this.solutionJpaRepository = solutionJpaRepository;
        this.replyRepository = replyRepository;
        this.replyJpaRepository = replyJpaRepository;
        this.memberJpaRepository = memberJpaRepository;
    }

    @Override
    public List<QuestionListDTO> searchQuestionList(String searchKeyword) {
        List<Question> questions = Optional.ofNullable(questionJpaRepository.searchQuestionList(searchKeyword))
                .orElseThrow(() -> new QuestionLoadingFailedException("질문 목록을 불러오는 과정에서 오류가 발생했습니다."));
        return bringQuestionList(questions);
    }

    @Override
    public List<QuestionListDTO> getQuestionList() {
        List<Question> questions = Optional.ofNullable(questionRepository.findAll())
                .orElseThrow(() -> new QuestionLoadingFailedException("질문 목록을 불러오는 과정에서 오류가 발생했습니다."));
        return bringQuestionList(questions);
    }

    @Override
    public List<QuestionListDTO> getQuestionList(Long categoryId) {
        Category category = Optional.ofNullable(categoryRepository.findOne(categoryId))
                .orElseThrow(() -> new CategoryNotFoundException("카테고리 정보가 존재하지 않습니다."));
        List<Question> questions = Optional.ofNullable(questionJpaRepository.findByCategory(category))
                .orElseThrow(() -> new QuestionLoadingFailedException("질문 목록을 불러오는 과정에서 오류가 발생했습니다."));
        return bringQuestionList(questions);
    }

    @Override
    public QuestionDetailDTO getQuestionDetail(String email, Long questionId) {
        /* 회원 존재유무 확인 */
        Member member = Optional.ofNullable(memberJpaRepository.findByEmail(email))
                .orElseThrow(() -> new MemberNotFoundException("회원 정보가 존재하지 않습니다."));

        /* 질문 존재유무 확인 */
        Question question = Optional.ofNullable(questionRepository.findOne(questionId))
                .orElseThrow(() -> new QuestionNotFoundException("질문 정보가 존재하지 않습니다."));

        /* 회원 투표유무 확인 */
        char polled = 'A'; // 작성자(A)
        if(!questionJpaRepository.existsByMemberAndQuestionId(member, questionId)) {
            polled = pollJpaRepository.existsByMemberAndQuestion(member, question) ? 'Y' : 'N'; // 투표(Y), 미투표(N)
        }

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
                        .solutionId(solution.getPoll().getPollId())
                        .sequence(poll.getChoice().getSequence())
                        .opinion(solution.getOpinion())
                        .createdAt(poll.getCreatedAt())
                        .memberId(poll.getMember().getMemberId())
                        .nickname(poll.getMember().getNickname())
                        .build();
                solutions.add(s);
            }
        }

        /* 투표별 답글 데이터 목록 */
        List<ReplyDTO> replies = new ArrayList<>();
        for(Poll poll : polls) {
            if(solutionJpaRepository.existsByPoll(poll)) {
                Solution solution = solutionJpaRepository.findByPoll(poll);
                for(Reply reply : replyJpaRepository.findBySolution(solution)) {
                    if(Optional.ofNullable(reply).isPresent()) {
                        ReplyDTO r = ReplyDTO.builder()
                                .comment(reply.getComment())
                                .nickname(reply.getMember().getNickname())
                                .createdAt(reply.getCreatedAt())
                                .solutionId(poll.getPollId())
                                .build();
                        replies.add(r);
                    }
                }
            }
        }

        return QuestionDetailDTO.builder()
                .title(question.getTitle())
                .content(question.getContent())
                .category(question.getCategory().getName())
                .nickname(question.getMember().getNickname())
                .polled(polled)
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
    public void addQuestion(String email, QuestionRegistrationDTO dto) {
        /* 회원 존재유무 확인 */
        Member member = Optional.ofNullable(memberJpaRepository.findByEmail(email))
                .orElseThrow(() -> new MemberNotFoundException("회원 정보가 존재하지 않습니다."));

        /* 카테고리 존재유무 확인 */
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
    public void answerQuestion(String email, QuestionResponseDTO dto) {
        /* 회원 존재유무 확인 */
        Member member = Optional.ofNullable(memberJpaRepository.findByEmail(email))
                .orElseThrow(() -> new MemberNotFoundException("회원 정보가 존재하지 않습니다."));
        
        /* 질문 존재유무 확인 */
        Question question = Optional.ofNullable(questionRepository.findOne(dto.getQuestionId()))
                .orElseThrow(() -> new QuestionNotFoundException("질문 정보가 존재하지 않습니다."));

        /* 자문자답여부 확인 */
        if(email.equals(question.getMember().getEmail())) {
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

    @Override
    @Transactional
    public void addReply(String email, ReplyRegistrationDTO dto) {
        /* 회원 존재유무 확인 */
        Member member = Optional.ofNullable(memberJpaRepository.findByEmail(email))
                .orElseThrow(() -> new MemberNotFoundException("답글을 작성한 회원 정보가 존재하지 않습니다."));

        /* 투표 정보 확인 */
        Poll poll = Optional.ofNullable(pollJpaRepository.findByPollId(dto.getSolutionId()))
                .orElseThrow(() -> new PollNotFoundException("답글을 작성한 투표 정보가 존재하지 않습니다."));

        /* 솔루션 존재유무 확인 */
        Solution solution = Optional.ofNullable(solutionJpaRepository.findByPoll(poll))
                .orElseThrow(() -> new SolutionNotFoundException("답글을 작성한 솔루션에 대한 정보가 존재하지 않습니다."));

        Reply reply = Reply.builder()
                .comment(dto.getComment())
                .solution(solution)
                .member(member)
                .build();
        replyRepository.save(reply);
    }

    private List<QuestionListDTO> bringQuestionList(List<Question> questions) {
        List<QuestionListDTO> list = new ArrayList<>();
        for(Question question : questions) {
            /* 질문별 투표 데이터 목록 */
            List<Poll> polls = Optional.ofNullable(pollJpaRepository.findByQuestion(question))
                    .orElseThrow(() -> new PollNotFoundException("투표 데이터를 불러오는 과정에서 오류가 발생했습니다."));

            /* 질문별 솔루션 총합 & 솔루션별 답글 총합 */
            int replyCnt = 0;
            int solutionCnt = 0;
            for(Poll poll : polls) {
                if(solutionJpaRepository.existsByPoll(poll)) {
                    Solution solution = solutionJpaRepository.findByPoll(poll);
                    replyCnt += replyJpaRepository.countBySolution(solution);
                    solutionCnt++;
                }
            }

            QuestionListDTO dto = QuestionListDTO.builder()
                    .questionId(question.getQuestionId())
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
}