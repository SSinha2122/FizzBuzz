package com.Thoughts.io.FizzBuzz.service;

import com.Thoughts.io.FizzBuzz.dto.VoteDto;
import com.Thoughts.io.FizzBuzz.exception.FizzBuzzException;
import com.Thoughts.io.FizzBuzz.exception.PostNotFoundException;
import com.Thoughts.io.FizzBuzz.model.Post;
import com.Thoughts.io.FizzBuzz.model.Vote;
import com.Thoughts.io.FizzBuzz.repository.PostRepository;
import com.Thoughts.io.FizzBuzz.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.Thoughts.io.FizzBuzz.model.VoteType.UPVOTE;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    @Transactional
    public void vote(VoteDto voteDto) {
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post Not Found with ID - " + voteDto.getPostId()));
        Optional<Vote> voteByPostAndUser = voteRepository.findByPostIdAndUserId(post.getPostId(), authService.getCurrentUser().getUserId());
        if(voteByPostAndUser.isPresent()) {
        if (voteByPostAndUser.isPresent() &&
                voteByPostAndUser.get().getVoteType()
                        .equals(voteDto.getVoteType())) {
            throw new FizzBuzzException("You have already "
                    + voteDto.getVoteType() + "'d for this post");
        }else if(voteByPostAndUser.isPresent()) {
        	voteByPostAndUser.get().setVoteType(voteDto.getVoteType());
        	voteRepository.save(voteByPostAndUser.get());
        }
        }else {
        if (UPVOTE.equals(voteDto.getVoteType())) {
            post.setVoteCount(post.getVoteCount() + 1);
        } else {
            post.setVoteCount(post.getVoteCount() - 1);
        }
        voteRepository.save(mapToVote(voteDto, post.getPostId()));
        }
        postRepository.save(post);
    }

    private Vote mapToVote(VoteDto voteDto, Long postId) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .postId(postId)
                .userId(authService.getCurrentUser().getUserId())
                .build();
    }
}
