package com.Thoughts.io.FizzBuzz.service;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.Thoughts.io.FizzBuzz.dto.BlogDto;
import com.Thoughts.io.FizzBuzz.exception.BlogNotFoundException;
import com.Thoughts.io.FizzBuzz.model.Blog;
import com.Thoughts.io.FizzBuzz.repository.BlogRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BlogService {

	private final BlogRepository blogRepository;
	private final AuthService authService;

	@Transactional(readOnly = true)
	public List<BlogDto> getAll() {
		List<BlogDto> blogDtos = new ArrayList<>();
		List<Blog> blogs = blogRepository.findAll();
		if(!CollectionUtils.isEmpty(blogs)) {
		blogs.forEach(blog->{
			BlogDto blogDto = new BlogDto();
			blogDto.setId(blog.getId());
			blogDto.setDescription(blog.getDescription());
			blogDto.setName(blog.getName());
			blogDto.setNumberOfPosts(blog.getNumberOfPosts());
			blogDtos.add(blogDto);
		});
		}
		return blogDtos;
	}

	@Transactional
	public BlogDto save(BlogDto blogDto) {
    Blog blog = new Blog();
    blog.setUserId(authService.getCurrentUser().getUserId());
    blog.setCreatedDate(now());
    blog.setDescription(blogDto.getDescription());
    blog.setName(blogDto.getName());
    blog.setNumberOfPosts(0);
    blog = blogRepository.save(blog);
		blogDto.setId(blog.getId());
		return blogDto;
	}

	@Transactional(readOnly = true)
	public BlogDto getBlog(Long id) throws Exception{
		BlogDto blogDto = new BlogDto();
		Blog blog = blogRepository.findByid(id);
		if(blog!=null) {
			blogDto.setId(blog.getId());
			blogDto.setDescription(blog.getDescription());
			blogDto.setName(blog.getName());
			blogDto.setNumberOfPosts(blog.getNumberOfPosts());
		}else {
			throw new BlogNotFoundException("Blog is not there !");
		}
		return blogDto;		
	}
}
