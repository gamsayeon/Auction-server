package com.example.auction_server.controller;

import com.example.auction_server.aop.LoginCheck;
import com.example.auction_server.dto.CategoryDTO;
import com.example.auction_server.service.serviceImpl.CategoryServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/category")
@RestController
@Log4j2
public class CategoryController {

    private final Logger logger = LogManager.getLogger(CategoryController.class);

    private final CategoryServiceImpl categoryService;

    public CategoryController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @LoginCheck(types = LoginCheck.LoginType.ADMIN)
    public ResponseEntity<CategoryDTO> registerCategory(Long id, @RequestBody @Valid CategoryDTO categoryDTO) {
        logger.debug("category을 등록합니다.");
        CategoryDTO resultCategoryDTO = categoryService.registerCategory(categoryDTO);
        return ResponseEntity.ok(resultCategoryDTO);
    }

    @PatchMapping("/{categoryId}")
    @LoginCheck(types = LoginCheck.LoginType.ADMIN)
    public ResponseEntity<CategoryDTO> updateCategory(Long id, @PathVariable("categoryId") Long categoryId,
                                                      @RequestBody @Validated(CategoryDTO.isNotUpdate.class) CategoryDTO categoryDTO) {
        logger.debug("category을 수정합니다.");
        CategoryDTO resultCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);
        return ResponseEntity.ok(resultCategoryDTO);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> selectCategory(@PathVariable("categoryId") Long categoryId) {
        logger.debug("category를 조회합니다.");
        CategoryDTO resultCategoryDTO = categoryService.selectCategory(categoryId);
        return ResponseEntity.ok(resultCategoryDTO);
    }

    @DeleteMapping("/{categoryId}")
    @LoginCheck(types = LoginCheck.LoginType.ADMIN)
    public ResponseEntity<String> deleteCategory(Long id, @PathVariable("categoryId") Long categoryId) {
        logger.debug("category을 삭제합니다.");
        String categoryName = categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(categoryName + "은 정상적으로 삭제되었습니다.");
    }
}
