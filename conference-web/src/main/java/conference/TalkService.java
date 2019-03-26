package conference;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TalkService {

    private RestTemplate restTemplate;
    private ReviewService reviewService;

    private Logger logger = LoggerFactory.getLogger(getClass());
    private volatile Talk[] talks = new Talk[0];

    @Autowired
    public TalkService(@LoadBalanced RestTemplate restTemplate, ReviewService reviewService) {
        this.restTemplate = restTemplate;
        this.reviewService = reviewService;
    }

    @HystrixCommand(fallbackMethod = "cachedTalks")
    public Talk[] allTalks() {
        talks = restTemplate.getForObject("http://talk-service/talks", Talk[].class);
        logger.info("Returning latest talks");
        return talks;
    }

    Talk[] cachedTalks() {
        logger.info("Talk-service unavailable, returning cached talks");
        return talks;
    }

    public Talk talkDetails(Long talkId) {
        Talk talk = restTemplate.getForObject("http://talk-service/talks/{id}", Talk.class, talkId);
        talk.setReviews(reviewService.reviewsFor(talkId));
        return talk;
    }
}
