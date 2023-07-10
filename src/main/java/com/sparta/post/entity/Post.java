package com.sparta.post.entity;

import com.sparta.post.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "post")
@NoArgsConstructor
public class Post extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username") // 기본적으로는 참조하는 테이블에 PK로 지정이됨 referencedColumnName = "username"를 해줘서 table에 usernmae으로 FK Column형성
    private User user;
    @Column(name = "contents", nullable = false)
    private String contents;
    @Column(name = "title", nullable = false)
    private String title;
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    @OrderBy("createdAt DESC")
    private List<Comment> commentList = new ArrayList<>();



    public Post(PostRequestDto requestDto, User user) {
        this.user = user;
        this.contents = requestDto.getContents();
        this.title = requestDto.getTitle();
    }

    public void update(PostRequestDto requestDto) {
        this.contents = requestDto.getContents();
        this.title = requestDto.getTitle();
    }
}
