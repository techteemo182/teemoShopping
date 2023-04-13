package com.teemo.shopping.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class TimeRecordEntity implements Serializable {

    @Column(columnDefinition = "timestamp")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "timestamp")
    @CreatedDate
    private LocalDateTime createdAt;
}
