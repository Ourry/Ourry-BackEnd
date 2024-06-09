package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.domain.*;
import com.bluewhaletech.Ourry.dto.ReportRegistrationDTO;
import com.bluewhaletech.Ourry.exception.CategoryNotFoundException;
import com.bluewhaletech.Ourry.exception.MemberNotFoundException;
import com.bluewhaletech.Ourry.jwt.JwtProvider;
import com.bluewhaletech.Ourry.repository.MemberJpaRepository;
import com.bluewhaletech.Ourry.repository.ReportDetailRepository;
import com.bluewhaletech.Ourry.repository.ReportRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReportServiceImpl implements ReportService {
    private final JwtProvider tokenProvider;
    private final EnumManagement enumManagement;
    private final MemberJpaRepository memberJpaRepository;
    private final ReportRepository reportRepository;
    private final ReportDetailRepository reportDetailRepository;

    @Autowired
    public ReportServiceImpl(JwtProvider tokenProvider, EnumManagement enumManagement, MemberJpaRepository memberJpaRepository, ReportRepository reportRepository, ReportDetailRepository reportDetailRepository) {
        this.tokenProvider = tokenProvider;
        this.enumManagement = enumManagement;
        this.memberJpaRepository = memberJpaRepository;
        this.reportRepository = reportRepository;
        this.reportDetailRepository = reportDetailRepository;
    }

    @Override
    @Transactional
    public void addReport(String accessToken, ReportRegistrationDTO dto) {
        /* Access Token으로부터 이메일 가져오기 */
        String email = tokenProvider.getTokenSubject(accessToken.substring(7));

        /* 신고자 존재유무 확인 */
        Member author = Optional.ofNullable(memberJpaRepository.findByEmail(email))
                .orElseThrow(() -> new MemberNotFoundException("신고자 정보가 존재하지 않습니다."));

        /* 피신고자 존재유무 확인 */
        Member target = Optional.ofNullable(memberJpaRepository.findByEmail(dto.getTargetEmail()))
                .orElseThrow(() -> new MemberNotFoundException("피신고자 정보가 존재하지 않습니다."));

        /* 신고 카테고리 존재유무 확인 */
        if(dto.getArticleTypeId() < 0 || dto.getArticleTypeId() >= ArticleType.values().length) {
            throw new CategoryNotFoundException("잘못된 게시물 유형입니다. 다시 확인해주세요.");
        }

        /* 신고 카테고리 존재유무 확인 */
        if(dto.getCategoryId() < 0 || dto.getCategoryId() >= ReportCategory.values().length) {
            throw new CategoryNotFoundException("등록되지 않은 카테고리입니다. 다시 확인해주세요.");
        }

        Report report = Report.builder()
                .status(ReportStatus.RECEIVED)
                .articleType(enumManagement.getArticleTypeMap().get(dto.getArticleTypeId()))
                .articleId(dto.getArticleId())
                .build();
        reportRepository.save(report);

        ReportDetail reportDetail = ReportDetail.builder()
                .category(enumManagement.getReportCategoryMap().get(dto.getCategoryId()))
                .reason(dto.getReason())
                .author(author)
                .target(target)
                .build();
        reportDetailRepository.save(reportDetail);
    }
}