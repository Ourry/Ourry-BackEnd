package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.domain.*;
import com.bluewhaletech.Ourry.dto.ReportDTO;
import com.bluewhaletech.Ourry.dto.ReportRegistrationDTO;
import com.bluewhaletech.Ourry.exception.*;
import com.bluewhaletech.Ourry.jwt.JwtProvider;
import com.bluewhaletech.Ourry.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReportServiceImpl implements ReportService {
    private final JwtProvider tokenProvider;
    private final EnumManagement enumManagement;
    private final MemberJpaRepository memberJpaRepository;
    private final ReportRepository reportRepository;
    private final ReportJpaRepository reportJpaRepository;
    private final ReportDetailRepository reportDetailRepository;
    private final ReportDetailJpaRepository reportDetailJpaRepository;

    @Autowired
    public ReportServiceImpl(JwtProvider tokenProvider, EnumManagement enumManagement, MemberJpaRepository memberJpaRepository, ReportRepository reportRepository, ReportJpaRepository reportJpaRepository, ReportDetailRepository reportDetailRepository, ReportDetailJpaRepository reportDetailJpaRepository) {
        this.tokenProvider = tokenProvider;
        this.enumManagement = enumManagement;
        this.memberJpaRepository = memberJpaRepository;
        this.reportRepository = reportRepository;
        this.reportJpaRepository = reportJpaRepository;
        this.reportDetailRepository = reportDetailRepository;
        this.reportDetailJpaRepository = reportDetailJpaRepository;
    }

    @Override
    public List<ReportDTO> getReportList(String accessToken) {
        /* Access Token으로부터 이메일 가져오기 */
        String email = tokenProvider.getTokenSubject(accessToken.substring(7));

        /* 관리자 계정 존재유무 확인 */
        Optional.ofNullable(memberJpaRepository.findByEmail(email))
                .orElseThrow(() -> new MemberNotFoundException("계정 정보가 존재하지 않습니다."));

        /* 신고 목록 불러오기 */
        List<Report> reportList = Optional.ofNullable(reportRepository.findAll())
                .orElseThrow(() -> new ReportLoadingFailedException("신고 목록을 불러오는 과정에서 오류가 발생했습니다."));

        List<ReportDTO> list = new ArrayList<>();
        for(Report report : reportList) {
            /* 신고 사유 불러오기 */
            List<ReportDetail> detailList = Optional.ofNullable(reportDetailJpaRepository.findByReport(report))
                    .orElseThrow(() -> new ReportDetailLoadingFailedException("신고 사유를 불러오는 과정에서 오류가 발생했습니다."));

            ReportDTO dto = ReportDTO.builder()
                    .articleType(report.getArticleType())
                    .articleId(report.getArticleId())
                    .status(report.getStatus().getName())
                    .targetEmail(detailList.getFirst().getTarget().getEmail())
                    .targetNickname(detailList.getFirst().getTarget().getNickname())
                    .reportCnt(detailList.size())
                    .createdAt(detailList.getFirst().getCreatedAt())
                    .build();
            list.add(dto);
        }
        return list;
    }

    @Override
    @Transactional
    public void addReport(String accessToken, ReportRegistrationDTO dto) {
        /* Access Token으로부터 이메일 가져오기 */
        String email = tokenProvider.getTokenSubject(accessToken.substring(7));

        /* 신고자 계정 존재유무 확인 */
        Member author = Optional.ofNullable(memberJpaRepository.findByEmail(email))
                .orElseThrow(() -> new MemberNotFoundException("신고자 정보가 존재하지 않습니다."));

        /* 피신고자 존재유무 확인 */
        Member target = Optional.ofNullable(memberJpaRepository.findByEmail(dto.getTargetEmail()))
                .orElseThrow(() -> new MemberNotFoundException("피신고자 정보가 존재하지 않습니다."));

        /* 신고자와 피신고자가 동일한지 확인 */
        if(email.equals(dto.getTargetEmail())) {
            throw new ReportToOneselfException("본인이 작성한 게시물은 신고할 수 없습니다.");
        }

        /* 게시물 유형 존재유무 확인 */
        if(dto.getArticleTypeId() < 0 || dto.getArticleTypeId() >= ArticleType.values().length) {
            throw new CategoryNotFoundException("잘못된 게시물 유형입니다. 다시 확인해주세요.");
        }

        /* 신고 카테고리 존재유무 확인 */
        if(dto.getCategoryId() < 0 || dto.getCategoryId() >= ReportCategory.values().length) {
            throw new CategoryNotFoundException("등록되지 않은 카테고리입니다. 다시 확인해주세요.");
        }

        ArticleType articleType = enumManagement.getArticleTypeMap().get(dto.getArticleTypeId());
        if(reportJpaRepository.existsByArticleIdAndArticleType(dto.getArticleId(), articleType)) {
            /* 기존 Report 엔티티 불러오기 */
            Report report = reportJpaRepository.findByArticleIdAndArticleType(dto.getArticleId(), articleType);
            /* 해당 Report 엔티티에 대한 신고이력 검증 */
            if(reportDetailJpaRepository.existsByReportAndAuthor(report, author)) {
                throw new ReportHistoryExistException("해당 게시물에 대한 신고 이력이 존재합니다.");
            }
            /* 참조한 Report 엔티티를 바탕으로 ReportDetail 생성 */
            reportDetailRepository.save(ReportDetail.builder()
                    .category(enumManagement.getReportCategoryMap().get(dto.getCategoryId()))
                    .reason(dto.getReason())
                    .author(author)
                    .target(target)
                    .report(report)
                    .build());
        } else {
            /* 신규 Report 엔티티 생성*/
            Report report = Report.builder()
                    .articleType(articleType)
                    .articleId(dto.getArticleId())
                    .status(ReportStatus.RECEIVED)
                    .build();
            reportRepository.save(report);
            /* 참조한 Report 엔티티를 바탕으로 ReportDetail 생성 */
            reportDetailRepository.save(ReportDetail.builder()
                    .category(enumManagement.getReportCategoryMap().get(dto.getCategoryId()))
                    .reason(dto.getReason())
                    .author(author)
                    .target(target)
                    .report(report)
                    .build());
        }
    }
}