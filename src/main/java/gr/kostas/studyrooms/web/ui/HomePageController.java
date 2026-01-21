package gr.kostas.studyrooms.web.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {

    @GetMapping("/")
    public String showHomepage() {
        return "homepage";
    }
}