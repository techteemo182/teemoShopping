package com.teemo.shopping.game;

import com.teemo.shopping.core.BaseEntity;
import com.teemo.shopping.resource.Resource;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
public class GamesHasResources extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "games_id")
    Game game;

    @ManyToOne
    @JoinColumn(name = "resources_id")
    Resource resource;
}
