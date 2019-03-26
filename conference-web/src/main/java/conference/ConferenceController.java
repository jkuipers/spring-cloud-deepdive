package conference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ConferenceController {

    private TalkService talkService;

    @Autowired
    public ConferenceController(TalkService talkService) {
        this.talkService = talkService;
    }

    @GetMapping("/")
    String allTalks(Model model) {
        model.addAttribute("talks", talkService.allTalks());
        return "index";
    }

    @GetMapping("/talks/{talkId}")
    String allTalks(Model model, @PathVariable Long talkId) {
        model.addAttribute(talkService.talkDetails(talkId));
        return "talk";
    }
}
