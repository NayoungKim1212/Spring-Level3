package com.sparta.post.service;

import com.sparta.post.dto.PostRequestDto;
import com.sparta.post.dto.PostResponseDto;
import com.sparta.post.entity.Post;
import com.sparta.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    public PostResponseDto createPost(PostRequestDto requestDto) {
        Post post = new Post(requestDto);

        Post savePost = postRepository.save(post);

        return new PostResponseDto(post);
    }

    public List<PostResponseDto> getPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(PostResponseDto::new).toList();
    }

    public PostResponseDto getPostById(Long id) {
        return new PostResponseDto(findPost(id));
    }

    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto) {

        Post post = findPost(id);
        // 만약 post의 getpassword와 requestDto의 getpassword가 같으면
        if(post.getPassword().equals(requestDto.getPassword())) {
            post.update(requestDto);
        }
        return new PostResponseDto(post);
    }

    public String deletePost(Long id, String password) {

        Post post = findPost(id);
        if(post.getPassword().equals(password)) {
            postRepository.delete(post);
            return "success";

        }else {
            return "Error";
        }
    }

    private Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("헤당 게시물은 존재하지 않습니다.")
        );
    }
}
