package com.Thoughts.io.FizzBuzz.controller;

import java.util.List;

import javax.validation.Valid;

import com.Thoughts.io.FizzBuzz.dto.BlogDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Thoughts.io.FizzBuzz.service.BlogService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/blogs")
@AllArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping
    public List<BlogDto> getAllBlogs() {
        return blogService.getAll();
    }

    @GetMapping("/{id}")
    public BlogDto getBlog(@PathVariable Long id) throws Exception {
        return blogService.getBlog(id);
    }

    @PostMapping
    public BlogDto create(@RequestBody @Valid BlogDto blogDto) {
        return blogService.save(blogDto);
    }
}

