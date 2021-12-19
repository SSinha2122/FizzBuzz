package com.Thoughts.io.FizzBuzz.service;

import com.Thoughts.io.FizzBuzz.dto.CommentsDto;
import com.Thoughts.io.FizzBuzz.exception.PostNotFoundException;
import com.Thoughts.io.FizzBuzz.model.Comment;
import com.Thoughts.io.FizzBuzz.model.NotificationEmail;
import com.Thoughts.io.FizzBuzz.model.Post;
import com.Thoughts.io.FizzBuzz.model.User;
import com.Thoughts.io.FizzBuzz.repository.CommentRepository;
import com.Thoughts.io.FizzBuzz.repository.PostRepository;
import com.Thoughts.io.FizzBuzz.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import static java.time.Instant.now;

import java.util.List;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CommentService {
    //TODO: Construct POST URL
    private static final String POST_URL = "";

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final MailService mailService;

    public void createComment(CommentsDto commentsDto) {
        Post post = postRepository.findById(commentsDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(commentsDto.getPostId().toString()));
        User user = authService.getCurrentUser();
        Comment comment = new Comment();
        comment.setCreatedDate(now());
        comment.setPostId(post.getPostId());
        comment.setText(commentsDto.getText());
        comment.setUserId(user.getUserId());
        commentRepository.save(comment);
        post.setCommentCount(post.getCommentCount()+1);
        postRepository.save(post);
        String message = post.getUserId() + " posted a comment on your post." + POST_URL;
        sendCommentNotification(message, user);
    }

    public List<CommentsDto> getAllCommentsForPost(Long postId) {
    	List<CommentsDto> commentsDtos = new ArrayList<CommentsDto>();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId.toString()));
        List<Comment> comments= commentRepository.findByPostId(post.getPostId());
        if(!CollectionUtils.isEmpty(comments)) {
        	comments.forEach(comment->{
        		CommentsDto commentsDto = new CommentsDto();
        		commentsDto.setCreatedDate(comment.getCreatedDate());
        		commentsDto.setId(comment.getId());
        		commentsDto.setPostId(postId);
        		commentsDto.setText(comment.getText());
        		commentsDto.setUserName(authService.getCurrentUser().getUsername());
        		commentsDtos.add(commentsDto);
        	});
        }
        return commentsDtos;
    }

    public List<CommentsDto> getCommentsByUser(String userName) {
    	List<CommentsDto> commentsDtos =new ArrayList<CommentsDto>();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
        List<Comment> comments = commentRepository.findAllByUserId(user.getUserId());
        if(!CollectionUtils.isEmpty(comments)) {
        	comments.forEach(comment->{
        		CommentsDto commentsDto = new CommentsDto();
        		commentsDto.setCreatedDate(comment.getCreatedDate());
        		commentsDto.setId(comment.getId());
        		commentsDto.setPostId(comment.getPostId());
        		commentsDto.setText(comment.getText());
        		commentsDto.setUserName(authService.getCurrentUser().getUsername());
        		commentsDtos.add(commentsDto);
        	});
        }
        return commentsDtos;
    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() + " Commented on your post", user.getEmail(), message));
    }
}
