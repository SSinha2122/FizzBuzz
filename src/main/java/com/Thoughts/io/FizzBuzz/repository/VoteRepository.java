package com.Thoughts.io.FizzBuzz.repository;

import com.Thoughts.io.FizzBuzz.model.Post;
import com.Thoughts.io.FizzBuzz.model.User;
import com.Thoughts.io.FizzBuzz.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByPostIdAndUserId(Long postId, Long userid);
}

