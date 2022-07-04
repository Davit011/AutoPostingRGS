package com.example.autopostingrgs.repository;

import com.example.autopostingrgs.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

}
