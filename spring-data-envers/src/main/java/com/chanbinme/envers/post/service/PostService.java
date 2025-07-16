package com.chanbinme.envers.post.service;

import com.chanbinme.envers.post.dto.PostCreateRequestDto;
import com.chanbinme.envers.post.entity.Post;
import com.chanbinme.envers.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public void createPost(PostCreateRequestDto postCreateRequestDto) {
        Post post = Post.builder()
            .title(postCreateRequestDto.title())
            .content(postCreateRequestDto.content())
            .build();
        postRepository.save(post);
    }

    public void updatePost() {
        postRepository.findById(1L)
            .ifPresent(post -> post.updateContent("에어컨 틀었더니 조금 시원하다. 에어컨 최고!"));
    }

    public void deletePost() {
        postRepository.deleteById(1L);
    }
}
