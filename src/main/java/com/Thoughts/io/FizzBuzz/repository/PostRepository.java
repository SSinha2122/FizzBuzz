package com.Thoughts.io.FizzBuzz.repository;

import com.Thoughts.io.FizzBuzz.model.Post;
import com.Thoughts.io.FizzBuzz.model.Blog;
import com.Thoughts.io.FizzBuzz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByBlogId(Long blogId);
    List<Post> findAllByUserId(Long userId);
}
