package com.dinehub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dinehub.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p
            FROM Product p
            WHERE
            (
                :keyword IS NULL
                OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(p.category.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND
            (
                :category IS NULL
                OR LOWER(p.category.categoryName) = LOWER(:category)
            )
            """)
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("category") String category,
            Pageable pageable);

}