package com.example.auction_server.controller;

import com.example.auction_server.aop.LoginCheck;
import com.example.auction_server.dto.CategoryDTO;
import com.example.auction_server.dto.CategoryUpdateDTO;
import com.example.auction_server.model.CommonResponse;
import com.example.auction_server.service.serviceImpl.CategoryServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/category")
@RestController
@Log4j2
@RequiredArgsConstructor
public class CategoryController {

    private final Logger logger = LogManager.getLogger(CategoryController.class);

    private final CategoryServiceImpl categoryService;

    @PostMapping
    @LoginCheck(types = LoginCheck.LoginType.ADMIN)
    public ResponseEntity<CommonResponse<CategoryDTO>> registerCategory(Long id, @RequestBody @Valid CategoryDTO categoryDTO,
                                                                        HttpServletRequest request) {
        logger.debug("category을 등록합니다.");
        CommonResponse<CategoryDTO> response = new CommonResponse<>("SUCCESS", "Category를 등록했습니다.",
                request.getRequestURI(), categoryService.registerCategory(categoryDTO));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{categoryId}")
    @LoginCheck(types = LoginCheck.LoginType.ADMIN)
    public ResponseEntity<CommonResponse<CategoryDTO>> updateCategory(Long id, @PathVariable("categoryId") Long categoryId,
                                                                      @RequestBody @Valid CategoryUpdateDTO categoryDTO,
                                                                      HttpServletRequest request) {
        logger.debug("category을 수정합니다.");
        CommonResponse<CategoryDTO> response = new CommonResponse<>("SUCCESS", "Category를 수정했습니다.",
                request.getRequestURI(), categoryService.updateCategory(categoryDTO, categoryId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CommonResponse<CategoryDTO>> selectCategory(@PathVariable("categoryId") Long categoryId,
                                                                      HttpServletRequest request) {
        logger.debug("category를 조회합니다.");
        CommonResponse<CategoryDTO> response = new CommonResponse<>("SUCCESS", "Category를 조회했습니다.",
                request.getRequestURI(), categoryService.selectCategory(categoryId));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    @LoginCheck(types = LoginCheck.LoginType.ADMIN)
    public ResponseEntity<CommonResponse<String>> deleteCategory(Long id, @PathVariable("categoryId") Long categoryId,
                                                                 HttpServletRequest request) {
        logger.debug("category을 삭제합니다.");
        String categoryName = categoryService.deleteCategory(categoryId);
        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "Category를 삭제 했습니다.",
                request.getRequestURI(), categoryName + "은 정상적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
}
