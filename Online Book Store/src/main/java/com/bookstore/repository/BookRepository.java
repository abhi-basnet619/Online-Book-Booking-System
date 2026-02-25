package com.bookstore.repository;

import com.bookstore.domain.entity.Book;
import com.bookstore.domain.enums.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCategory(BookCategory category);
    List<Book> findByDiscountPercentIsNotNull();
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
}
