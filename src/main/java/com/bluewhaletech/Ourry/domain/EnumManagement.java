package com.bluewhaletech.Ourry.domain;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class EnumManagement {
    protected Map<Integer, ArticleCategory> articleCategoryMap = new HashMap<>();
    protected Map<Integer, ReportCategory> reportCategoryMap = new HashMap<>();
    protected Map<Integer, ReportStatus> reportStatusMap = new HashMap<>();
    protected Map<Integer, ArticleType> articleTypeMap = new HashMap<>();

    public EnumManagement() {
        this.articleCategoryMap.put(0, ArticleCategory.MY_QUESTION);
        this.articleCategoryMap.put(1, ArticleCategory.STUDY_AND_CAREER);
        this.articleCategoryMap.put(2, ArticleCategory.LOVE_AND_MARRIAGE);
        this.articleCategoryMap.put(3, ArticleCategory.HOME_AND_PARENTING);
        this.articleCategoryMap.put(4, ArticleCategory.ECONOMY_AND_INVESTMENT);
        this.articleCategoryMap.put(5, ArticleCategory.SOCIETY_AND_RELATIONSHIP);
        this.reportCategoryMap.put(0, ReportCategory.SPAMMING_ARTICLES);
        this.reportCategoryMap.put(1, ReportCategory.ILLEGAL_INFORMATION);
        this.reportCategoryMap.put(2, ReportCategory.PERSONAL_INFORMATION);
        this.reportCategoryMap.put(3, ReportCategory.OFFENSIVE_EXPRESSION);
        this.reportCategoryMap.put(4, ReportCategory.SPREADING_PORNOGRAPHY);
        this.reportStatusMap.put(0, ReportStatus.RECEIVED);
        this.reportStatusMap.put(1, ReportStatus.UNDERWAY);
        this.reportStatusMap.put(2, ReportStatus.COMPLETED);
        this.articleTypeMap.put(0, ArticleType.QUESTION);
        this.articleTypeMap.put(1, ArticleType.SOLUTION);
        this.articleTypeMap.put(2, ArticleType.REPLY);
    }
}