package com.example.autopostingrgs.service;

import com.example.autopostingrgs.model.Profile;
import com.example.autopostingrgs.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public List<Profile> findAll(){
       return profileRepository.findAll();
    }

    public Profile save(Profile profile){
       return profileRepository.save(profile);
    }

}
