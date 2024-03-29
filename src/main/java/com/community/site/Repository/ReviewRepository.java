package com.community.site.Repository;

import com.community.site.entity.Review;
import com.community.site.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> getByUser(User user);
    List<Review> findByUser(User user);

    long countByUser(User user);
}
