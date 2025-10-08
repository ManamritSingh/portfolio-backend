package com.portfolio.backend.controller;

import com.portfolio.backend.model.Skill;
import com.portfolio.backend.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
// Class-level annotations removed
public class SkillController {

    @Autowired
    private SkillRepository skillRepository;

    // ===========================================
    // PUBLIC ENDPOINTS - No authentication required
    // ===========================================

    @GetMapping("/api/public/skills")
    public List<Skill> getVisibleSkills() {
        return skillRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    @GetMapping("/api/public/skills/category/{category}")
    public List<Skill> getSkillsByCategory(@PathVariable String category) {
        return skillRepository.findByCategoryIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(category);
    }

    @GetMapping("/api/public/skills/technical")
    public List<Skill> getTechnicalSkills() {
        return skillRepository.findTechnicalSkills();
    }

    @GetMapping("/api/public/skills/other")
    public List<Skill> getOtherSkills() {
        return skillRepository.findOtherSkills();
    }

    @GetMapping("/api/public/skills/hobbies")
    public List<Skill> getHobbiesAndPassions() {
        return skillRepository.findHobbiesAndPassions();
    }

    @GetMapping("/api/public/skills/categories")
    public List<String> getAllCategories() {
        return skillRepository.findAllUniqueCategories();
    }

    @GetMapping("/api/public/skills/search/{keyword}")
    public List<Skill> searchSkills(@PathVariable String keyword) {
        return skillRepository.findBySkillsListContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(keyword);
    }

    @GetMapping("/api/public/skills/category-exists/{category}")
    public ResponseEntity<Boolean> categoryExists(@PathVariable String category) {
        return ResponseEntity.ok(skillRepository.existsByCategoryIgnoreCase(category));
    }

    // ===========================================
    // ADMIN ENDPOINTS - Requires ROLE_ADMIN
    // ===========================================

    @GetMapping("/api/admin/skills")
    public List<Skill> getAllSkills() {
        return skillRepository.findAllByOrderByOrderIndexAsc();
    }

    @GetMapping("/api/admin/skills/{id}")
    public ResponseEntity<Skill> getSkill(@PathVariable Long id) {
        Optional<Skill> skill = skillRepository.findById(id);
        return skill.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/admin/skills")
    public ResponseEntity<Skill> createSkill(@RequestBody Skill skill) {
        if (skillRepository.existsByCategoryIgnoreCase(skill.getCategory())) {
            return ResponseEntity.badRequest().build();
        }

        if (skill.getOrderIndex() == null) {
            skill.setOrderIndex(skillRepository.getMaxOrderIndex() + 1);
        }

        Skill savedSkill = skillRepository.save(skill);
        return ResponseEntity.ok(savedSkill);
    }

    @PutMapping("/api/admin/skills/{id}")
    public ResponseEntity<Skill> updateSkill(@PathVariable Long id, @RequestBody Skill skillDetails) {
        Optional<Skill> optionalSkill = skillRepository.findById(id);

        if (optionalSkill.isPresent()) {
            Skill skill = optionalSkill.get();

            if (!skill.getCategory().equalsIgnoreCase(skillDetails.getCategory()) &&
                    skillRepository.existsByCategoryIgnoreCase(skillDetails.getCategory())) {
                return ResponseEntity.badRequest().build();
            }

            skill.setCategory(skillDetails.getCategory());
            skill.setSkillsList(skillDetails.getSkillsList());
            skill.setOrderIndex(skillDetails.getOrderIndex());
            skill.setVisible(skillDetails.getVisible());

            return ResponseEntity.ok(skillRepository.save(skill));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/admin/skills/{id}")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id) {
        if (skillRepository.existsById(id)) {
            skillRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
