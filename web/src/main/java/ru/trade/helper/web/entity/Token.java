package ru.trade.helper.web.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "tokens", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"token", "user_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Token {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type = Type.NO_TYPE;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "name")
    private String name;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "create_date")
    private Timestamp createDate = new Timestamp(System.currentTimeMillis());

    @Column(name = "is_enable", nullable = false)
    private Boolean isEnable = true;

    @Column(name = "is_valid", nullable = false)
    private Boolean isValid = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private List<Subscription> subscriptions;

    public enum Type {
        READONLY,
        FULL_ACCESS,
        SANDBOX,
        NO_TYPE
    }

    @Override
    public String toString() {
        return String.format("%s[id=%d]: %s, %s",
                name,
                id,

                isEnable ? "Включен" : "Выключен"
        );
    }
}
