package com.voltlab.app_backend.favorite.repository;

import com.voltlab.app_backend.favorite.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserIdAndProduct_Id(Long userId, Long productId);
    void deleteByUserIdAndProduct_Id(Long userId, Long productId);
}


