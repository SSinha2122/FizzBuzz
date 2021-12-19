package com.Thoughts.io.FizzBuzz.service;

import static java.time.Instant.now;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.Thoughts.io.FizzBuzz.dto.PostRequest;
import com.Thoughts.io.FizzBuzz.dto.PostResponse;
import com.Thoughts.io.FizzBuzz.exception.BlogNotFoundException;
import com.Thoughts.io.FizzBuzz.exception.PostNotFoundException;
import com.Thoughts.io.FizzBuzz.model.Blog;
import com.Thoughts.io.FizzBuzz.model.Comment;
import com.Thoughts.io.FizzBuzz.model.Post;
import com.Thoughts.io.FizzBuzz.model.User;
import com.Thoughts.io.FizzBuzz.repository.BlogRepository;
import com.Thoughts.io.FizzBuzz.repository.CommentRepository;
import com.Thoughts.io.FizzBuzz.repository.PostRepository;
import com.Thoughts.io.FizzBuzz.repository.UserRepository;
import com.github.marlonlom.utilities.timeago.TimeAgo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

	private final PostRepository postRepository;
	private final BlogRepository blogRepository;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	private final AuthService authService;

	@Transactional(readOnly = true)
	public PostResponse getPost(Long id) {
		PostResponse postResponse = new PostResponse();
		Optional<Post> post = postRepository.findById(id);
		if (post != null) {
			List<Comment> comments = commentRepository.findByPostId(post.get().getPostId());
			Optional<User> user = userRepository.findById(post.get().getUserId());
			Blog blog = blogRepository.findByid(post.get().getBlogId());
			postResponse.setPostName(post.get().getPostName());
			postResponse.setCommentCount(comments.size());
			postResponse.setUserName(user.get().getUsername());
			postResponse.setUrl(post.get().getUrl());
			postResponse.setDescription(post.get().getDescription());
			postResponse.setId(post.get().getPostId());
			postResponse.setVoteCount(post.get().getVoteCount());
			postResponse.setSubredditName(blog.getName());
			postResponse.setDuration(TimeAgo.using(post.get().getCreatedDate().toEpochMilli()));
		} else {
			throw new PostNotFoundException("Post is not there!");
		}
		return postResponse;
	}

	@Transactional(readOnly = true)
	public List<PostResponse> getAllPosts() {
		List<PostResponse> postResponses = new ArrayList<PostResponse>();
		List<Post> posts = postRepository.findAll();
		if (!CollectionUtils.isEmpty(posts)) {
			posts.forEach(post -> {
				List<Comment> comments = commentRepository.findByPostId(post.getPostId());
				Optional<User> user = userRepository.findById(post.getUserId());
				Blog blog = blogRepository.findByid(post.getBlogId());
				PostResponse postResponse = new PostResponse();
				postResponse.setPostName(post.getPostName());
				postResponse.setCommentCount(comments.size());
				postResponse.setUserName(user.get().getUsername());
				postResponse.setUrl(post.getUrl());
				postResponse.setDescription(post.getDescription());
				postResponse.setVoteCount(post.getVoteCount());
				postResponse.setSubredditName(blog.getName());
				postResponse.setId(post.getPostId());
				postResponse.setDuration(TimeAgo.using(post.getCreatedDate().toEpochMilli()));
				postResponses.add(postResponse);
			});
		}
		return postResponses;
	}

	public void save(PostRequest postRequest) {
		Optional<Blog> blog = Optional.ofNullable(blogRepository.findByName(postRequest.getSubredditName())
      .orElseThrow(() -> new BlogNotFoundException(postRequest.getSubredditName())));
		Post post = new Post();
		post.setBlogId(blog.get().getId());
		post.setCommentCount(0);
		post.setCreatedDate(now());
		post.setPostName(postRequest.getPostName());
		post.setDescription(postRequest.getDescription());
		post.setUrl(postRequest.getUrl());
		post.setUserId(authService.getCurrentUser().getUserId());
		post.setVoteCount(0);
		postRepository.save(post);
		blog.get().setNumberOfPosts(blog.get().getNumberOfPosts()+1);
		blogRepository.save(blog.get());
	}

	@Transactional(readOnly = true)
	public List<PostResponse> getPostsBySubreddit(Long id) {
		Blog blog = blogRepository.findById(id).orElseThrow(() -> new BlogNotFoundException(id.toString()));
		List<Post> posts = postRepository.findAllByBlogId(blog.getId());
		List<PostResponse> postResponses = new ArrayList<>();
		if (!CollectionUtils.isEmpty(posts)) {
			posts.forEach(post -> {
				List<Comment> comments = commentRepository.findByPostId(post.getPostId());
				Optional<User> user = userRepository.findById(post.getUserId());
				PostResponse postResponse = new PostResponse();
				postResponse.setPostName(post.getPostName());
				postResponse.setCommentCount(comments.size());
				postResponse.setUserName(user.get().getUsername());
				postResponse.setUrl(post.getUrl());
				postResponse.setDescription(post.getDescription());
				postResponse.setVoteCount(post.getVoteCount());
				postResponse.setId(post.getPostId());
				postResponse.setSubredditName(blog.getName());
				postResponse.setDuration(TimeAgo.using(post.getCreatedDate().toEpochMilli()));
				postResponses.add(postResponse);
			});
		}
		return postResponses;
	}

	@Transactional(readOnly = true)
	public List<PostResponse> getPostsByUsername(String username) {
		List<PostResponse> postResponses = new ArrayList<>();
		Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username)));
		if(user != null) {
		List<Post> posts = postRepository.findAllByUserId(user.get().getUserId());
		if (!CollectionUtils.isEmpty(posts)) {
			posts.forEach(post -> {
				List<Comment> comments = commentRepository.findByPostId(post.getPostId());
				Blog blog = blogRepository.findByid(post.getBlogId());
				PostResponse postResponse = new PostResponse();
				postResponse.setPostName(post.getPostName());
				postResponse.setCommentCount(comments.size());
				postResponse.setUserName(user.get().getUsername());
				postResponse.setUrl(post.getUrl());
				postResponse.setId(post.getPostId());
				postResponse.setDescription(post.getDescription());
				postResponse.setVoteCount(post.getVoteCount());
				postResponse.setSubredditName(blog.getName());
				postResponse.setDuration(TimeAgo.using(post.getCreatedDate().toEpochMilli()));
				postResponses.add(postResponse);
			});
		}
		}
		return postResponses;
	}
}
