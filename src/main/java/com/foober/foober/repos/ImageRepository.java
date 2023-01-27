package com.foober.foober.repos;

import com.foober.foober.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
