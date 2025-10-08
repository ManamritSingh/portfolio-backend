package com.portfolio.backend.controller;

import com.portfolio.backend.model.Leadership;
import com.portfolio.backend.repository.LeadershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
// Removed class-level @RequestMapping and @CrossOrigin for global config
public class LeadershipController {

    @Autowired
    private LeadershipRepository leadershipRepository;

    // ===========================================
    // PUBLIC ENDPOINTS - No authentication required
    // ===========================================

    @GetMapping("/api/public/leadership")
    public List<Leadership> getVisibleLeadership() {
        // Main public endpoint to get all visible leadership entries
        return leadershipRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    @GetMapping("/api/public/leadership/search/{keyword}")
    public List<Leadership> searchLeadership(@PathVariable String keyword) {
        // Public search endpoint; only searches within visible entries
        return leadershipRepository.findByDescriptionContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(keyword);
    }

    @GetMapping("/api/public/leadership/count")
    public ResponseEntity<Long> getLeadershipCount() {
        // Public count of visible leadership entries
        return ResponseEntity.ok(leadershipRepository.countByIsVisibleTrue());
    }

    // ===========================================
    // ADMIN ENDPOINTS - Requires ROLE_ADMIN
    // ===========================================

    @GetMapping("/api/admin/leadership")
    public List<Leadership> getAllLeadership() {
        // Admin gets all leadership entries, including hidden ones
        return leadershipRepository.findAllByOrderByOrderIndexAsc();
    }

    @GetMapping("/api/admin/leadership/{id}")
    public ResponseEntity<Leadership> getLeadership(@PathVariable Long id) {
        // Admin gets a single leadership entry by its ID
        Optional<Leadership> leadership = leadershipRepository.findById(id);
        return leadership.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/admin/leadership")
    public ResponseEntity<Leadership> createLeadership(@RequestBody Leadership leadership) {
        // Admin creates a new leadership entry
        if (leadership.getOrderIndex() == null) {
            leadership.setOrderIndex(leadershipRepository.getMaxOrderIndex() + 1);
        }

        Leadership savedLeadership = leadershipRepository.save(leadership);
        return ResponseEntity.ok(savedLeadership);
    }

    @PutMapping("/api/admin/leadership/{id}")
    public ResponseEntity<Leadership> updateLeadership(@PathVariable Long id, @RequestBody Leadership leadershipDetails) {
        // Admin updates an existing leadership entry
        Optional<Leadership> optionalLeadership = leadershipRepository.findById(id);

        if (optionalLeadership.isPresent()) {
            Leadership leadership = optionalLeadership.get();
            leadership.setDescription(leadershipDetails.getDescription());
            leadership.setOrderIndex(leadershipDetails.getOrderIndex());
            leadership.setVisible(leadershipDetails.getVisible());

            return ResponseEntity.ok(leadershipRepository.save(leadership));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/admin/leadership/{id}")
    public ResponseEntity<?> deleteLeadership(@PathVariable Long id) {
        // Admin deletes a leadership entry
        if (leadershipRepository.existsById(id)) {
            leadershipRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
