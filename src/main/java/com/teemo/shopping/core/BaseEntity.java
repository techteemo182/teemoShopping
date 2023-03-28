package com.teemo.shopping.core;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalTime;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NoArgsConstructor
@Getter
@MappedSuperclass   // Entity 화 방지
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {
    @Id
    @Generated
    Long id;

    @Column(columnDefinition = "timestamp")
    @LastModifiedDate
    LocalTime updatedAt;

    @Column(columnDefinition = "timestamp") // 2038년 까지 이지만 아직 쓸만할듯
    @CreatedDate
    LocalTime createdAt;
}
