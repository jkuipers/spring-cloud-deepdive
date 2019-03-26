package talk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/talks")
public class TalkController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private TalkRepository talkRepository;

    public TalkController(TalkRepository talkRepository) {
        this.talkRepository = talkRepository;
    }

    @GetMapping
    List<Talk> findTalks() {
        logger.info("Returning all talks");
        return (List<Talk>) talkRepository.findAll();
    }

    @GetMapping("{talk}")
    Talk talkDetails(@PathVariable Talk talk) {
        if (talk == null) {
            throw new TalkNotFoundException();
        }
        return talk;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    static class TalkNotFoundException extends RuntimeException {}

}
