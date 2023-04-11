package com.teemo.shopping.game.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.resource.domain.Resource;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    indexes = {
        @Index(columnList = "games_id, resources_id")
    }
)
@AttributeOverride(name = "id", column = @Column(name = "games_resources_id"))
public class GamesResources extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "games_id")
    @NotNull
    private Game game;

    @ManyToOne
    @JoinColumn(name = "resources_id")
    @NotNull
    private Resource resource;
}
