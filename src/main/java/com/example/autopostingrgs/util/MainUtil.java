package com.example.autopostingrgs.util;

import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MainUtil {

    public void findElementByText(String text, List<WebElement> elements) {
        for (WebElement button : elements) {
            if (button.getText().equals(text)) {
                button.click();
                break;
            }
        }
    }
}
