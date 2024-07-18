package ru.stepup.course.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "logins")
public class Logins {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date access_date;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private Users user;

    @Column(nullable = false)
    private String appl_type;

}
