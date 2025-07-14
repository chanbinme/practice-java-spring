package com.chanbinme.envers.post.repository;

import com.chanbinme.envers.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
