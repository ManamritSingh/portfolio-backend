package com.portfolio.backend.controller;

import com.portfolio.backend.model.Skill;
import com.portfolio.backend.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = "http://localhost:5173")
public class SkillController {

    @Autowired
    private SkillRepository skillRepository;

    // Get all visible skills (for public resume)
    @GetMapping("/public")
    public List<Skill> getVisibleSkills() {
        return skillRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    // Get all skills (for admin)
    @GetMapping
    public List<Skill> getAllSkills() {
        return skillRepository.findAllByOrderByOrderIndexAsc();
    }

    // Get skills by category
    @GetMapping("/category/{category}")
    public List<Skill> getSkillsByCategory(@PathVariable String category) {
        return skillRepository.findByCategoryIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(category);
    }

    // Get technical skills
    @GetMapping("/technical")
    public List<Skill> getTechnicalSkills() {
        return skillRepository.findTechnicalSkills();
    }

    // Get other skills
    @GetMapping("/other")
    public List<Skill> getOtherSkills() {
        return skillRepository.findOtherSkills();
    }

    // Get hobbies and passions
    @GetMapping("/hobbies")
    public List<Skill> getHobbiesAndPassions() {
        return skillRepository.findHobbiesAndPassions();
    }

    // Get all unique categories
    @GetMapping("/categories")
    public List<String> getAllCategories() {
        return skillRepository.findAllUniqueCategories();
    }

    // Search skills by keyword in skillsList
    @GetMapping("/search/{keyword}")
    public List<Skill> searchSkills(@PathVariable String keyword) {
        return skillRepository.findBySkillsListContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(keyword);
    }

    // Check if category exists
    @GetMapping("/category-exists/{category}")
    public ResponseEntity<Boolean> categoryExists(@PathVariable String category) {
        return ResponseEntity.ok(skillRepository.existsByCategoryIgnoreCase(category));
    }

    // Get single skill
    @GetMapping("/{id}")
    public ResponseEntity<Skill> getSkill(@PathVariable Long id) {
        Optional<Skill> skill = skillRepository.findById(id);
        return skill.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get skill by category name (for updating specific category)
    @GetMapping("/by-category/{category}")
    public ResponseEntity<Skill> getSkillByCategory(@PathVariable String category) {
        Optional<Skill> skill = skillRepository.findByCategoryIgnoreCase(category);
        return skill.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new skill
    @PostMapping
    public ResponseEntity<Skill> createSkill(@RequestBody Skill skill) {
        // Check if category already exists
        if (skillRepository.existsByCategoryIgnoreCase(skill.getCategory())) {
            return ResponseEntity.badRequest().build(); // Category already exists
        }

        // Auto-set order index if not provided
        if (skill.getOrderIndex() == null) {
            skill.setOrderIndex(skillRepository.getMaxOrderIndex() + 1);
        }

        Skill savedSkill = skillRepository.save(skill);
        return ResponseEntity.ok(savedSkill);
    }

    // Update skill
    @PutMapping("/{id}")
    public ResponseEntity<Skill> updateSkill(@PathVariable Long id, @RequestBody Skill skillDetails) {
        Optional<Skill> optionalSkill = skillRepository.findById(id);

        if (optionalSkill.isPresent()) {
            Skill skill = optionalSkill.get();

            // Check if new category conflicts with existing ones (excluding current)
            if (!skill.getCategory().equalsIgnoreCase(skillDetails.getCategory()) &&
                    skillRepository.existsByCategoryIgnoreCase(skillDetails.getCategory())) {
                return ResponseEntity.badRequest().build(); // Category already exists
            }

            skill.setCategory(skillDetails.getCategory());
            skill.setSkillsList(skillDetails.getSkillsList());
            skill.setOrderIndex(skillDetails.getOrderIndex());
            skill.setVisible(skillDetails.getVisible());

            return ResponseEntity.ok(skillRepository.save(skill));
        }

        return ResponseEntity.notFound().build();
    }

    // Update skills by category (convenient for updating specific category directly)
    @PutMapping("/category/{category}")
    public ResponseEntity<Skill> updateSkillsByCategory(@PathVariable String category,
                                                        @RequestBody Skill skillDetails) {
        Optional<Skill> optionalSkill = skillRepository.findByCategoryIgnoreCase(category);

        if (optionalSkill.isPresent()) {
            Skill skill = optionalSkill.get();
            skill.setSkillsList(skillDetails.getSkillsList());
            skill.setOrderIndex(skillDetails.getOrderIndex());
            skill.setVisible(skillDetails.getVisible());

            return ResponseEntity.ok(skillRepository.save(skill));
        }

        return ResponseEntity.notFound().build();
    }

    // Delete skill
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id) {
        if (skillRepository.existsById(id)) {
            skillRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
