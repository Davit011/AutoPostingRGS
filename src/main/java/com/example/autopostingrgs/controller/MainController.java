package com.example.autopostingrgs.controller;

import com.example.autopostingrgs.dto.SavePostRequest;
import com.example.autopostingrgs.dto.SaveProfileRequest;
import com.example.autopostingrgs.model.Profile;
import com.example.autopostingrgs.service.ProfileService;
import com.example.autopostingrgs.util.MainUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MainUtil mainUtil;
    private final ProfileService profileService;
    private final ModelMapper modelMapper;

    @GetMapping("/")
    public String addImage() {
        return "add-image";
    }

    @GetMapping("/save/{err}")
    public String saveProfile(@PathVariable(name = "err", required = false) String err, ModelMap modelMap) {
        if (!err.equals("null")) {
            modelMap.addAttribute("error", "Please input username and password");
        }
        return "save-profile";
    }

    @PostMapping("/save")
    public String addProfile(@ModelAttribute SaveProfileRequest profileRequest) {

        if (profileRequest.getUsername() == null || profileRequest.getPassword() == null
        || profileRequest.getUsername().equals("") || profileRequest.getPassword().equals("")) {
            return "redirect:/save/err";
        }
        Profile profile = modelMapper.map(profileRequest, Profile.class);
        profileService.save(profile);
        return "save-profile";
    }

    @PostMapping("/add")
    public String openBrowser(@ModelAttribute SavePostRequest savePostRequest, ModelMap modelMap) {
        //config
        System.setProperty("webdriver.chrome.driver", "C:\\IdeaProjects\\ITSpace\\AutoPostingRGS\\src\\main\\resources\\static\\drivers\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        ChromeDriver driver = new ChromeDriver(options);

        //get image file
        MultipartFile file = savePostRequest.getFile();
        BufferedImage image = null;
        String fileName = System.currentTimeMillis() + file.getOriginalFilename();
        String filePath = "C:\\IdeaProjects\\ITSpace\\AutoPostingRGS\\src\\main\\resources\\static\\images\\" + fileName;
        String format = file.getContentType().split("/")[1];
        if (file.getContentType().contains("image")) {
            try {
                image = ImageIO.read(file.getInputStream());
                File outputfile = new File("C:\\IdeaProjects\\ITSpace\\AutoPostingRGS\\src\\main\\resources\\static\\images\\" + fileName);
                ImageIO.write(image, format, outputfile);
            } catch (IOException e) {

            }
        }

        //open instagam
        driver.get("https://www.instagram.com/");
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        Map<Profile, String> status = new HashMap<>();
        List<Profile> profiles = profileService.findAll();
        for (Profile profile : profiles) {
            //log in
            WebElement username = driver.findElement(By.name("username"));
            WebElement password = driver.findElement(By.name("password"));
            WebElement submitButton = driver.findElement(By.tagName("button"));
            username.sendKeys(profile.getUsername());
            password.sendKeys(profile.getPassword());
            submitButton.submit();
            try {
                WebElement errorAlert = driver.findElement(By.id("slfErrorAlert"));
                System.err.println("Profile " + profile.getUsername() + " with password " + profile.getPassword() + " throw " + errorAlert.getText());
                status.put(profile, errorAlert.getText());
                driver.get("https://www.instagram.com/");
                break;
            } catch (NoSuchElementException e) {
                //wait for loading
                new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.tagName("nav")));

                //open addPost page
                WebElement addPost = driver.findElement(By.className("vZuFV"));
                addPost.click();

                //select image
                WebElement fileInput = driver.findElement(By.className("tb_sK"));
                fileInput.sendKeys(filePath);
                List<WebElement> buttons = driver.findElements(By.tagName("button"));
                mainUtil.findElementByText("Далее", buttons);

                //skip filters
                List<WebElement> secondStepbuttons = driver.findElements(By.tagName("button"));
                mainUtil.findElementByText("Далее", secondStepbuttons);

                //skip cut
                new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.tagName("polyline")));
                List<WebElement> cutStepbuttons = driver.findElements(By.tagName("button"));
                mainUtil.findElementByText("Далее", cutStepbuttons);

                //add text content (if exists)
                List<WebElement> thirdStepButtons = driver.findElements(By.tagName("button"));
                mainUtil.findElementByText("Поделиться", thirdStepButtons);

                //log out
                new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.tagName("h2")));
                driver.manage().deleteAllCookies();
                driver.navigate().refresh();
                status.put(profile, "ok");
            }
        }

        //close the bowser
        driver.quit();

        //redirect to html page
        modelMap.addAttribute("status", status);
        modelMap.addAttribute("profiles", profiles);
        return "show-errors";
    }
}
