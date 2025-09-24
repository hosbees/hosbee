package com.hosbee.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Thymeleaf 페이지 매핑을 담당하는 컨트롤러
 * 모든 정적 페이지와 템플릿 페이지의 라우팅을 처리합니다.
 */
@Controller
public class PageMappingController {

    /**
     * 메인 페이지
     */
    @GetMapping({"/", "/main", "/home"})
    public String main() {
        return "main";
    }

    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 회원가입 페이지
     */
    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    /**
     * 비밀번호 재설정 페이지
     */
    @GetMapping("/password-reset")
    public String passwordReset() {
        return "password-reset";
    }

    /**
     * 프로필 페이지
     */
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    /**
     * 게시판 목록 페이지
     */
    @GetMapping("/board")
    public String board(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "") String category,
                       @RequestParam(defaultValue = "") String keyword,
                       Model model) {
        // 실제 구현 시 게시판 데이터를 모델에 추가
        model.addAttribute("currentPage", page);
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);
        return "board";
    }

    /**
     * 게시글 작성 페이지
     */
    @GetMapping("/board/write")
    public String boardWrite() {
        return "board-write";
    }

    /**
     * 게시글 수정 페이지
     */
    @GetMapping("/board/edit/{id}")
    public String boardEdit(@PathVariable Long id, Model model) {
        // 실제 구현 시 게시글 데이터를 모델에 추가
        model.addAttribute("postId", id);
        return "board-write";
    }

    /**
     * 게시글 상세 페이지
     */
    @GetMapping("/board/{id}")
    public String boardDetail(@PathVariable Long id, Model model) {
        // 실제 구현 시 게시글 상세 데이터를 모델에 추가
        model.addAttribute("postId", id);
        return "board-detail";
    }

    /**
     * 화면 목록 페이지 (개발용)
     */
    @GetMapping("/page-list")
    public String pageList() {
        return "page_list";
    }

    /**
     * 에러 페이지 처리
     */
    @GetMapping("/error/{errorCode}")
    public String errorPage(@PathVariable String errorCode, Model model) {
        model.addAttribute("errorCode", errorCode);

        switch (errorCode) {
            case "404":
                return "error/404";
            case "500":
                return "error/500";
            default:
                return "error/error";
        }
    }

    /**
     * 404 에러 페이지 직접 접근
     */
    @GetMapping("/404")
    public String notFoundPage() {
        return "error/404";
    }

    /**
     * 500 에러 페이지 직접 접근
     */
    @GetMapping("/500")
    public String serverErrorPage() {
        return "error/500";
    }

    // API 관련 페이지들 (필요시 추가)

    /**
     * API 문서 페이지
     */
//    @GetMapping("/api-docs")
//    public String apiDocs() {
//        return "api-docs";
//    }


    /**
     * 사용자 설정 페이지
     */
//    @GetMapping("/settings")
//    public String settings() {
//        return "settings";
//    }

    /**
     * 도움말 페이지
     */
    @GetMapping("/help")
    public String help() {
        return "help";
    }

    /**
     * 약관 페이지
     */
    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }

    /**
     * 개인정보 처리방침 페이지
     */
    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }
}
