package ir.snp.expense.controller;

import ir.snp.expense.dto.CategoryDTO;
import ir.snp.expense.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/get-categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories(@AuthenticationPrincipal Jwt jwtToken) {
        String username = jwtToken.getClaimAsString("preferred_username");
        return ResponseEntity.ok(categoryService.getAllCategories(username));
    }
    
    @GetMapping("/get-category/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long categoryId, @AuthenticationPrincipal Jwt jwtToken) {
        String username = jwtToken.getClaimAsString("preferred_username");
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId, username));
    }
    
    @PostMapping("/create")
    public ResponseEntity<CategoryDTO> createCategory(@RequestParam String name, @AuthenticationPrincipal Jwt jwtToken) {
        String username = jwtToken.getClaimAsString("preferred_username");
        return ResponseEntity.ok(categoryService.createCategory(name, username));
    }
}
