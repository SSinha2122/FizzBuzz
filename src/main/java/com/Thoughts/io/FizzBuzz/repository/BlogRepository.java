package com.Thoughts.io.FizzBuzz.repository;

import com.Thoughts.io.FizzBuzz.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Optional<Blog> findByName(String blogName);
    Blog findByid(Long id);
}
