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
import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final ChoiceRepository choiceRepository;
    private final VoteRepository voteRepository;
    private final VoteJpaRepository voteJpaRepository;
    private final SolutionRepository solutionRepository;
    private final SolutionJpaRepository solutionJpaRepository;
    private final ReplyJpaRepository replyJpaRepository;

    @Autowired
    public ArticleServiceImpl(MemberRepository memberRepository, QuestionRepository questionRepository, CategoryRepository categoryRepository, ChoiceRepository choiceRepository, VoteRepository voteRepository, VoteJpaRepository voteJpaRepository, SolutionRepository solutionRepository, SolutionJpaRepository solutionJpaRepository, ReplyJpaRepository replyJpaRepository) {
        this.memberRepository = memberRepository;
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.choiceRepository = choiceRepository;
        this.voteRepository = voteRepository;
        this.voteJpaRepository = voteJpaRepository;
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
            List<Vote> votes = Optional.ofNullable(voteJpaRepository.findByQuestion(question))
                    .orElseThrow(() -> new VoteNotFoundException("투표 데이터를 불러오는 과정에서 오류가 발생했습니다."));
            /* 질문별 솔루션 총합 */
            int solutionCnt = 0;
            for(Vote vote : votes) {
                solutionCnt += solutionJpaRepository.countByVote(vote);
            }
            /* 질문별 답글 총합 */
            int replyCnt = 0;
            for(Vote vote : votes) {
                replyCnt += replyJpaRepository.countByVote(vote);
            }

            QuestionTotalDTO dto = QuestionTotalDTO.builder()
                    .title(question.getTitle())
                    .content(question.getContent())
                    .nickname(question.getMember().getNickname())
                    .createdAt(question.getCreatedAt())
                    .voteCnt(votes.size())
                    .responseCnt(solutionCnt+replyCnt)
                    .build();
            list.add(dto);
        }
        return list;
    }

    @Override
    public QuestionDetailDTO getQuestionDetail(Long questionId) {
        Question question = Optional.ofNullable(questionRepository.findOne(questionId))
                .orElseThrow(() -> new QuestionNotFoundException("질문 상세정보를 불러오는 과정에서 오류가 발생했습니다."));

        /* 질문별 선택지 데이터 목록 */
        List<ChoiceDTO> choices = new ArrayList<>();
        for(Choice choice : question.getChoices()) {
            ChoiceDTO c = ChoiceDTO.builder()
                    .detail(choice.getDetail())
                    .seq(choice.getSeq())
                    .build();
            choices.add(c);
        }

        /* 질문별 투표 데이터 목록 */
        List<Vote> votes = Optional.ofNullable(voteJpaRepository.findByQuestion(question))
                .orElseThrow(() -> new VoteNotFoundException("질문 데이터 목록을 불러와는 과정에서 오류가 발생했습니다."));

        /* 투표별 솔루션 데이터 목록 */
        List<SolutionDTO> solutions = new ArrayList<>();
        for(Vote vote : votes) {
            Solution solution = solutionJpaRepository.findByVote(vote);
            SolutionDTO s = SolutionDTO.builder()
                    .opinion(solution.getOpinion())
                    .nickname(vote.getMember().getNickname())
                    .createdAt(vote.getCreatedAt())
                    .build();
            solutions.add(s);
        }

        /* 투표별 답글 데이터 목록 */
        List<ReplyDTO> replies = new ArrayList<>();
        for(Vote vote : votes) {
            Reply reply = replyJpaRepository.findByVote(vote);
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
                .category(question.getCategory().getName())
                .nickname(question.getMember().getNickname())
                .voteCnt(votes.size())
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
        Member member = Optional.of(memberRepository.findOne(dto.getMemberId()))
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
        /* 회원 존재유무 확인 */
        Member member = Optional.ofNullable(memberRepository.findOne(dto.getMemberId()))
                .orElseThrow(() -> new MemberNotFoundException("회원 정보가 존재하지 않습니다."));

        /* 질문 존재유무 확인 */
        Question question = Optional.ofNullable(questionRepository.findOne(dto.getQuestionId()))
                .orElseThrow(() -> new QuestionNotFoundException("질문 정보를 불러올 수 없습니다."));

        /* 선택지 존재유무 확인 */
        Choice choice = Optional.ofNullable(choiceRepository.findOne(dto.getChoiceId()))
                .orElseThrow(() -> new ChoiceNotFoundException("선택지 정보를 불러올 수 없습니다."));

        /* 답변 작성유무 확인 */
        if(voteRepository.findByMemberAndChoice(member, choice).isPresent()) {
            throw new QuestionAlreadyAnsweredException("해당 질문에 대해 답변한 기록이 존재합니다.");
        }

        Vote vote = Vote.builder()
                .member(member)
                .question(question)
                .choice(choice)
                .build();
        voteRepository.save(vote);

        if(!dto.getOpinion().isEmpty()) {
            Solution solution = Solution.builder()
                    .opinion(dto.getOpinion())
                    .vote(vote)
                    .build();
            solutionRepository.save(solution);
        }
    }
}