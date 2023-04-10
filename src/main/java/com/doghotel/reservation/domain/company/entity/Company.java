package com.doghotel.reservation.domain.company.entity;

import com.doghotel.reservation.domain.company.dto.CompanyUpdateDto;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.role.Roles;
import com.doghotel.reservation.global.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long companyId;

    @Column(nullable = false, updatable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String detailAddress;

    @Column(nullable = false)
    private String representativeNumber;

    @Column
    private String fileName;

    @Column
    private String imgUrl;

    @OneToOne(mappedBy = "company", fetch = FetchType.LAZY)
    private Posts posts;

    @Enumerated(value = EnumType.STRING)
    private Roles roles;

    @Builder
    public Company(String email, String password, String companyName, String address, String detailAddress, String representativeNumber) {
        this.email = email;
        this.password = password;
        this.companyName = companyName;
        this.address = address;
        this.detailAddress = detailAddress;
        this.representativeNumber = representativeNumber;
        this.roles = Roles.COMPANY;
    }

    public Company updateCompany(CompanyUpdateDto dto) {
        if(dto.getCompanyName() != null) {
            this.companyName = dto.getCompanyName();
        }
        if(dto.getAddress() != null) {
            this.address = dto.getAddress();
        }
        if(dto.getDetailAddress() != null) {
            this.detailAddress = dto.getDetailAddress();
        }
        if(dto.getRepresentativeNumber() != null) {
            this.representativeNumber = dto.getRepresentativeNumber();
        }
        return this;
    }
    public void updatePassword(String password) {
        if(password != null) {
            this.password = password;
        }
    }

    public void updateCompanyImg(String fileName, String url) {
        if(fileName != null) {
            this.fileName = fileName;
        }
        if(url != null) {
            this.imgUrl = url;
        }
    }
    public void addPosts(Posts posts) {
        this.posts = posts;
    }


}
