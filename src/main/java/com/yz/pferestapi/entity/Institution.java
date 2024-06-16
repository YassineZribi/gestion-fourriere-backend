package com.yz.pferestapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "institutions")
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String logoPath;

    @Column(nullable = false)
    private String address;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 20, nullable = false)
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "chief_executive_id", referencedColumnName = "id")
    private User chiefExecutive;

    @OneToMany(mappedBy = "institution", fetch = FetchType.EAGER)
    private List<User> employees = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}