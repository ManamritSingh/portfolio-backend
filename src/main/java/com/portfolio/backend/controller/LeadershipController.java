package com.portfolio.backend.controller;

import com.portfolio.backend.model.Leadership;
import com.portfolio.backend.repository.LeadershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/leadership")
@CrossOrigin(origins = "http://localhost:5173")
public class LeadershipController {

    @Autowired
    private LeadershipRepository leadershipRepository;

    // Get all visible leadership (for public resume)
    @GetMapping("/public")
    public List<Leadership> getVisibleLeadership() {
        return leadershipRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    // Get all leadership (for admin)
    @GetMapping
    public List<Leadership> getAllLeadership() {
        return leadershipRepository.findAllByOrderByOrderIndexAsc();
    }

    // Get single leadership
    @GetMapping("/{id}")
    public ResponseEntity<Leadership> getLeadership(@PathVariable Long id) {
        Optional<Leadership> leadership = leadershipRepository.findById(id);
        return leadership.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Search leadership by keyword
    @GetMapping("/search/{keyword}")
    public List<Leadership> searchLeadership(@PathVariable String keyword) {
        return leadershipRepository.findByDescriptionContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(keyword);
    }

    // Get leadership count
    @GetMapping("/count")
    public ResponseEntity<Long> getLeadershipCount() {
        return ResponseEntity.ok(leadershipRepository.countByIsVisibleTrue());
    }

    // Create new leadership
    @PostMapping
    public ResponseEntity<Leadership> createLeadership(@RequestBody Leadership leadership) {
        // Auto-set order index if not provided
        if (leadership.getOrderIndex() == null) {
            leadership.setOrderIndex(leadershipRepository.getMaxOrderIndex() + 1);
        }

        Leadership savedLeadership = leadershipRepository.save(leadership);
        return ResponseEntity.ok(savedLeadership);
    }

    // Update leadership
    @PutMapping("/{id}")
    public ResponseEntity<Leadership> updateLeadership(@PathVariable Long id, @RequestBody Leadership leadershipDetails) {
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

    // Delete leadership
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLeadership(@PathVariable Long id) {
        if (leadershipRepository.existsById(id)) {
            leadershipRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
