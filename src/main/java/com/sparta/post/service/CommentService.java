package com.sparta.post.service;

import com.sparta.post.entity.Comment;
import com.sparta.post.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;
}
