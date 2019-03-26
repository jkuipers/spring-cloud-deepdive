package conference;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ReviewService {
    private RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public ReviewService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "reviewsUnavailable")
    public List<Review> reviewsFor(Long talkId) {
        List<Review> reviews = Arrays.asList(restTemplate.getForObject("http://review-service/reviews?talkId={talkId}", Review[].class, talkId));
        logger.info("Returning reviews for talk {}", talkId);
        return reviews;
    }

    List<Review> reviewsUnavailable(Long talkId) {
        logger.info("Review-service unavailable, returning empty list for talk {}", talkId);
        return Collections.emptyList();
    }
}
