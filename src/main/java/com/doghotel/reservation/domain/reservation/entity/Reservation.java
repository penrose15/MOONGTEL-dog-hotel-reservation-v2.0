package com.doghotel.reservation.domain.reservation.entity;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.room.entity.Room;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reservationId;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate checkInDate;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate checkOutDate;

    @Column
    private int dogCount;


    @Column
    @Enumerated(EnumType.STRING)
    private Status status = Status.RESERVED;


    @Column
    private int totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @OneToMany(mappedBy = "reservation",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservedDogs> reservedDogs = new ArrayList<>();

    @Builder
    public Reservation(LocalDate checkInDate, LocalDate checkOutDate, int dogCount, int totalPrice, Customer customer, Company company, Room room) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.dogCount = dogCount;
        this.totalPrice = totalPrice;
        this.customer = customer;
        this.company = company;
        this.room = room;
    }

    public void designateCustomer(Customer customer) {
        this.customer = customer;
    }

    public void designateCompany(Company company) {
        this.company = company;
    }

    public void designateRoom(Room room) {
        this.room = room;
    }

    public void changeStatus(String status) {
        Status status1 = Status.convertToStatus(status);
        this.status = status1;
    }

    public void addReservedDogs(List<ReservedDogs> reservedDogs) {
        this.reservedDogs = reservedDogs;
    }
}
