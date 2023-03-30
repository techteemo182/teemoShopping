package com.teemo.shopping.resource;

import com.teemo.shopping.core.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AttributeOverride(name = "id", column = @Column(name = "resources_id"))
@Table(
    name = "resources"
)
public class Resource extends BaseEntity {

    @Builder
    protected Resource(String path, String type) {
        this.path = path;
        this.type = type;
    }

    @Column
    private String path;

    @Column
    private String type;
}
