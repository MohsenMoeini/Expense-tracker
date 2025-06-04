package ir.snp.expense.service;

import ir.snp.expense.dto.CategoryDTO;
import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.User;
import ir.snp.expense.exception.CategoryNotFoundException;
import ir.snp.expense.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public List<CategoryDTO> getAllCategories(String username) {
        return categoryRepository.findByUser_Username(username).stream()
                .map(this::mapToCategoryDTO)
                .collect(Collectors.toList());
    }
    
    public CategoryDTO getCategoryById(Long id, String username) {
        return categoryRepository.findByIdAndUser_Username(id, username)
                .map(this::mapToCategoryDTO)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }
    
    public CategoryDTO createCategory(String name, String username) {
        Category category = new Category();
        category.setName(name);
        category.setUser(new User(username));
        Category savedCategory = categoryRepository.save(category);
        return mapToCategoryDTO(savedCategory);
    }
    
    private CategoryDTO mapToCategoryDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName()
        );
    }
}
