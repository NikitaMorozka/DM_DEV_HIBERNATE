package com.dmdev.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "users_chat")
public class UsersChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "crested_at")
    private Instant crestedAt;

    @Column(name = "created_by")
    private String createdBy;

    public void setUser(User user){
        this.user = user;
        this.user.getUsersChats().add(this);
    }

    public void setChat(Chat chat){
        this.chat = chat;
        this.chat.getUsersChats().add(this);
    }
}
