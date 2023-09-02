package org.caja.idea.entity.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.caja.idea.utils.Message;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments",  schema = "public")
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @NotBlank(message = Message.BODY_REQUIRED)
    private String body;
    @Column(name = "created")
    @CreationTimestamp
    private LocalDateTime created;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, targetEntity = Users.class)
    @JoinColumn(name = "user_id")
    private Users users;
    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, targetEntity = Post.class)
    private Post post;

    public Comment() {}

    public Comment(Long id, String body, LocalDateTime created, Users users, Post post) {
        this.id = id;
        this.body = body;
        this.created = created;
        this.users = users;
        this.post = post;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
