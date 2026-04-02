package dev.harakki.shiftlab.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@Audited // Аудит для отслеживания изменений в сущности, чтобы придерживаться принципов сохранения историчности данных
@SoftDelete
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sellers")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String contactInfo;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime registrationDate;

}
