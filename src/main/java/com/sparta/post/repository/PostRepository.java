package com.sparta.post.repository;

import com.sparta.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select p from Post p " +
            "left join fetch p.commentList c " +
            "left join fetch p.user " +
            "left join fetch c.user " +
            "order by p.createdAt desc")
    List<Post> findAllPostsWithCommentsOrderByCreatedAtDesc();

}
