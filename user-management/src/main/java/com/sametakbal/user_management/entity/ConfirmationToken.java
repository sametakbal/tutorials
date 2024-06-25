package com.sametakbal.user_management.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "confirmation_token")
@EqualsAndHashCode(callSuper = true)
public class ConfirmationToken extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "expires_at",nullable = false)
    private LocalDateTime expiresAt;
}
