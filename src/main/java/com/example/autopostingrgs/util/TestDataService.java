//package com.example.autopostingrgs.util;
//
//
//import com.example.autopostingrgs.model.Profile;
//import com.example.autopostingrgs.service.ProfileService;
//import lombok.AllArgsConstructor;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.ApplicationListener;
//import org.springframework.stereotype.Component;
//
//@AllArgsConstructor
//@Component
//public class TestDataService implements ApplicationListener<ApplicationReadyEvent> {
//
//    private final ProfileService profileService;
//
//    @Override
//    public void onApplicationEvent(ApplicationReadyEvent event) {
//        fillFullTestData();
//    }
//
//    public void fillFullTestData() {
//        profileService.save(Profile.builder()
//                .username("polyak155")
//                .password("DAVO3032001")
//                .build());
//
//        profileService.save(Profile.builder()
//                .username("esimaman3")
//                .password("DAVO3032001")
//                .build());
//    }
//}
