package com.vaaskel.domain.common;

import com.vaaskel.domain.security.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractEntityEqualsHashCodeTest {

    @Test
    void newEntitiesWithoutIdAreNotEqual() {
        User u1 = new User("testuser", "password");
        User u2 = new User("testuser", "password");

        assertThat(u1).isNotEqualTo(u2);
        assertThat(u1).isNotEqualTo(null);
    }

    @Test
    void entitiesWithSameIdAreEqual() {
        User u1 = new User("testuser", "password");
        User u2 = new User("testuser", "password");

        u1.setId(1L);
        u2.setId(1L);

        assertThat(u1).isEqualTo(u2);
        assertThat(u1.hashCode()).isEqualTo(u2.hashCode());
    }

    @Test
    void entitiesWithDifferentIdsAreNotEqual() {
        User u1 = new User("testuser", "password");
        User u2 = new User("testuser", "password");

        u1.setId(1L);
        u2.setId(2L);

        assertThat(u1).isNotEqualTo(u2);
    }

    @Test
    void entitiesOfDifferentSubclassAreNotEqualEvenWithSameId() {
        User user = new User("testuser", "password");
        user.setId(1L);

        class OtherEntity extends AbstractEntity {}
        OtherEntity other = new OtherEntity();
        other.setId(1L);

        assertThat(user)
                .as("Different subclasses with same id must never be equal")
                .isNotEqualTo(other);

        assertThat(other)
                .as("Equals must be symmetric across types")
                .isNotEqualTo(user);
    }
}