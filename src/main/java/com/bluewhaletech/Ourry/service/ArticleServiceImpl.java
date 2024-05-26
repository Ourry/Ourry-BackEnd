package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.domain.*;
import com.bluewhaletech.Ourry.dto.*;
import com.bluewhaletech.Ourry.exception.*;
import com.bluewhaletech.Ourry.jwt.JwtProvider;
import com.bluewhaletech.Ourry.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {
    private final JwtProvider tokenProvider;
    private final FcmServiceImpl fcmService;
    private final MemberJpaRepository memberJpaRepository;
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
    private final AlarmRepository alarmRepository;
    private final AlarmJpaRepository alarmJpaRepository;

    @Autowired
    public ArticleServiceImpl(JwtProvider tokenProvider, FcmServiceImpl fcmService, MemberJpaRepository memberJpaRepository, QuestionRepository questionRepository, QuestionJpaRepository questionJpaRepository, CategoryRepository categoryRepository, ChoiceRepository choiceRepository, ChoiceJpaRepository choiceJpaRepository, PollRepository pollRepository, PollJpaRepository pollJpaRepository, SolutionRepository solutionRepository, SolutionJpaRepository solutionJpaRepository, ReplyRepository replyRepository, ReplyJpaRepository replyJpaRepository, AlarmRepository alarmRepository, AlarmJpaRepository alarmJpaRepository) {
        this.tokenProvider = tokenProvider;
        this.fcmService = fcmService;
        this.memberJpaRepository = memberJpaRepository;
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
        this.alarmRepository = alarmRepository;
        this.alarmJpaRepository = alarmJpaRepository;
    }

    @Override
    public List<QuestionDTO> getQuestionList() {
        List<Question> questions = Optional.ofNullable(questionRepository.findAll())
                .orElseThrow(() -> new QuestionLoadingFailedException("질문 목록을 불러오는 과정에서 오류가 발생했습니다."));
        return bringQuestionList(questions);
    }

    @Override
    public List<QuestionDTO> getQuestionList(Long categoryId) {
        Category category = Optional.ofNullable(categoryRepository.findOne(categoryId))
                .orElseThrow(() -> new CategoryNotFoundException("카테고리 정보가 존재하지 않습니다."));
        List<Question> questions = Optional.ofNullable(questionJpaRepository.findByCategory(category))
                .orElseThrow(() -> new QuestionLoadingFailedException("질문 목록을 불러오는 과정에서 오류가 발생했습니다."));
        return bringQuestionList(questions);
    }

    @Override
    public QuestionDetailDTO getQuestionDetail(String accessToken, Long questionId) {
        /* 비회원 확인 */
        Member member = null;
        if(accessToken != null) {
            /* 회원 존재유무 확인 */
            String email = tokenProvider.getTokenSubject(accessToken.substring(7));
            member = Optional.ofNullable(memberJpaRepository.findByEmail(email))
                    .orElseThrow(() -> new MemberNotFoundException("회원 정보가 존재하지 않습니다."));
        }

        /* 질문 존재유무 확인 */
        Question question = Optional.ofNullable(questionRepository.findOne(questionId))
                .orElseThrow(() -> new QuestionNotFoundException("질문 정보가 존재하지 않습니다."));

        /* 알림 수신유무 확인 */
        char alarmYN = 'N'; // 미수신(N)
        if(member != null && alarmJpaRepository.existsAlarmByMemberAndQuestion(member, question)) {
            alarmYN = alarmJpaRepository.findAlarmYNByMemberAndQuestion(member, question);
        }

        /* 회원 투표유무 확인 */
        char polled = 'A'; // 작성자(A)
        if(member == null) {
            polled = 'N'; // 미투표(N)
        } else if(!questionJpaRepository.existsByMemberAndQuestionId(member, questionId)) {
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
                                .sequence(reply.getSolution().getPoll().getChoice().getSequence())
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

        /* 비회원과 미응답자는 솔루션 및 답글 정보 확인 불가 */
        int solutionCnt = solutions.size();
        int replyCnt = replies.size();
        if(polled == 'N') {
            solutions = null;
            replies = null;
        }
        return QuestionDetailDTO.builder()
                .title(question.getTitle())
                .content(question.getContent())
                .category(question.getCategory().getName())
                .memberId(question.getMember().getMemberId())
                .nickname(question.getMember().getNickname())
                .alarmYN(alarmYN)
                .polled(polled)
                .pollCnt(polls.size())
                .responseCnt(solutionCnt+replyCnt)
                .createdAt(question.getCreatedAt())
                .choices(choices)
                .solutions(solutions)
                .replies(replies)
                .build();
    }

    @Override
    public List<QuestionDTO> searchQuestionList(String searchKeyword) {
        List<Question> questions = Optional.ofNullable(questionJpaRepository.searchQuestionList(searchKeyword))
                .orElseThrow(() -> new QuestionLoadingFailedException("질문 목록을 불러오는 과정에서 오류가 발생했습니다."));
        return bringQuestionList(questions);
    }

    @Override
    @Transactional
    public void addQuestion(String accessToken, QuestionRegistrationDTO dto) {
        /* Access Token으로부터 이메일 가져오기 */
        String email = tokenProvider.getTokenSubject(accessToken.substring(7));

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

        /* 알림 수신여부(Y) 등록 */
        alarmRepository.save(Alarm.builder()
                .member(member)
                .question(question)
                .alarmYN("Y")
                .build());
    }

    @Override
    @Transactional
    public void answerQuestion(String accessToken, QuestionResponseDTO dto) {
        /* Access Token으로부터 이메일 가져오기 */
        String email = tokenProvider.getTokenSubject(accessToken.substring(7));

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

        /* 질문 정보와 응답자가 선택한 시퀀스로 선택지 정보 가져오기 */
        Choice choice = Optional.ofNullable(choiceRepository.findByQuestionAndSequence(question, dto.getSequence()))
                .orElseThrow(() -> new ChoiceNotFoundException("선택지 정보를 불러오는 과정에서 오류가 발생했습니다."));
        /* 투표 정보 생성 및 저장 */
        Poll poll = Poll.builder()
                .member(member)
                .question(question)
                .choice(choice)
                .build();
        pollRepository.save(poll);

        String opinion = dto.getOpinion();
        if(opinion != null) {
            /* FCM */
            try {
                /* 의견을 제출했다면 솔루션 정보 생성 및 저장 */
                solutionRepository.save(Solution.builder()
                        .opinion(opinion)
                        .poll(poll)
                        .build());

                /* 질문에 대한 알림 수신여부 검사 */
                if(alarmJpaRepository.findAlarmYNByMemberAndQuestion(question.getMember(), question) == 'Y') {
                    /* 질문 작성자에게 발급된 FcmToken 가져오기 */
                    String questionAuthorEmail = question.getMember().getEmail();
                    String fcmToken = Optional.ofNullable(memberJpaRepository.findFcmTokenByEmail(questionAuthorEmail))
                            .orElseThrow(() -> new FcmTokenNotFoundException("해당 회원에게 발급된 FCM 토큰이 없습니다."));
                    /* 질문 작성자에게 FCM 알림 전송 */
                    fcmService.sendMessage(fcmToken, "'"+question.getTitle()+"' 질문에 의견이 추가됐습니다.", opinion);
                }
            } catch (Exception e) {
                log.error("FCM Service Error: {}", e.getMessage());
            }

            /* 알림 수신여부(Y) 등록 */
            alarmRepository.save(Alarm.builder()
                    .member(member)
                    .question(question)
                    .alarmYN("Y")
                    .build());
        }
    }

    @Override
    @Transactional
    public void addReply(String accessToken, ReplyRegistrationDTO dto) {
        /* Access Token으로부터 이메일 가져오기 */
        String email = tokenProvider.getTokenSubject(accessToken.substring(7));

        /* 회원 존재유무 확인 */
        Member member = Optional.ofNullable(memberJpaRepository.findByEmail(email))
                .orElseThrow(() -> new MemberNotFoundException("답글을 작성한 회원 정보가 존재하지 않습니다."));

        /* 투표 정보 확인 */
        Poll poll = Optional.ofNullable(pollJpaRepository.findByPollId(dto.getSolutionId()))
                .orElseThrow(() -> new PollNotFoundException("답글을 작성한 투표 정보가 존재하지 않습니다."));

        /* 솔루션 존재유무 확인 */
        Solution solution = Optional.ofNullable(solutionJpaRepository.findByPoll(poll))
                .orElseThrow(() -> new SolutionNotFoundException("답글을 작성한 솔루션 정보가 존재하지 않습니다."));

        /* FCM 토큰 목록 */
        List<String> list = new ArrayList<>();

        /* 질문자에게 발급된 FCM 토큰 가져오기 */
        String questionAuthorEmail = poll.getQuestion().getMember().getEmail();
        String questionAuthorFcmToken = Optional.ofNullable(memberJpaRepository.findFcmTokenByEmail(questionAuthorEmail))
                .orElseThrow(() -> new FcmTokenNotFoundException("질문 작성자에게 FCM 토큰이 발급되지 않았습니다."));
        list.add(questionAuthorFcmToken);

        /* 질문에 대한 알림 수신여부 검사 */
        if(alarmJpaRepository.findAlarmYNByMemberAndQuestion(poll.getMember(), poll.getQuestion()) == 'Y') {
            /* 솔루션 작성자에게 발급된 FCM 토큰 가져오기 */
            String solutionAuthorEmail = poll.getMember().getEmail();
            String solutionAuthorFcmToken = Optional.ofNullable(memberJpaRepository.findFcmTokenByEmail(solutionAuthorEmail))
                    .orElseThrow(() -> new FcmTokenNotFoundException("솔루션 작성자에게 FCM 토큰이 발급되지 않았습니다."));
            list.add(solutionAuthorFcmToken);
        }

        /* 답글 정보 생성 및 저장 */
        Reply reply = Reply.builder()
                .comment(dto.getComment())
                .solution(solution)
                .member(member)
                .build();
        replyRepository.save(reply);

        /* 현재 답글을 제외한 답글 목록 불러오기 */
        for(Reply r : replyJpaRepository.findBySolutionExceptReplier(solution, reply)) {
            /* 질문에 대한 알림 수신여부 검사 */
            if(alarmJpaRepository.findAlarmYNByMemberAndQuestion(r.getMember(), poll.getQuestion()) == 'Y') {
                /* 솔루션에 답글을 작성한 회원들로부터 FCM 토큰 가져오기 */
                String replyAuthorEmail = r.getMember().getEmail();
                String replyAuthorFcmToken = Optional.ofNullable(memberJpaRepository.findFcmTokenByEmail(replyAuthorEmail))
                        .orElseThrow(() -> new FcmTokenNotFoundException("답글 작성자에게 FCM 토큰이 발급되지 않았습니다."));
                list.add(replyAuthorFcmToken);
            }
        }

        /* FCM */
        try {
            /* 리스트에 담긴 토큰별로 FCM 알림 전송 */
            for(String fcmToken : list) {
                fcmService.sendMessage(fcmToken, "'"+poll.getQuestion().getTitle()+"' 질문에 답글이 추가됐습니다.", dto.getComment());
            }
        } catch (Exception e) {
            log.error("FCM Service Error: {}", e.getMessage());
        }

        /* 알림 수신여부(Y) 등록 */
        if(!alarmJpaRepository.existsAlarmByMemberAndQuestion(member, poll.getQuestion())) {
            alarmRepository.save(Alarm.builder()
                    .member(member)
                    .question(poll.getQuestion())
                    .alarmYN("Y")
                    .build());
        }
    }

    @Override
    @Transactional
    public void setAlarmOnQuestion(String accessToken, AlarmSettingDTO dto) {
        /* Access Token으로부터 이메일 가져오기 */
        String email = tokenProvider.getTokenSubject(accessToken.substring(7));

        /* 회원 존재유무 확인 */
        Member member = Optional.ofNullable(memberJpaRepository.findByEmail(email))
                .orElseThrow(() -> new MemberNotFoundException("회원 정보가 존재하지 않습니다."));

        /* 질문 존재유무 확인 */
        Question question = Optional.ofNullable(questionRepository.findOne(dto.getQuestionId()))
                .orElseThrow(() -> new QuestionNotFoundException("질문 정보가 존재하지 않습니다."));

        /* 질문에 대한 투표 기록 확인 */
        Alarm alarm = Optional.ofNullable(alarmJpaRepository.findByMemberAndQuestion(member, question))
                .orElseThrow(() -> new AlarmSettingNotFoundException("질문에 대해 투표한 기록이 존재하지 않습니다."));
        alarm.setAlarmYN(dto.getAlarmYN());
    }

    private List<QuestionDTO> bringQuestionList(List<Question> questions) {
        List<QuestionDTO> list = new ArrayList<>();
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

            QuestionDTO dto = QuestionDTO.builder()
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