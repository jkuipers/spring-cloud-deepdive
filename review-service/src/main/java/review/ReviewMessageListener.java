package review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.annotation.MessageEndpoint;

@MessageEndpoint
public class ReviewMessageListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ReviewRepository reviewRepository;

    public ReviewMessageListener(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @StreamListener(Sink.INPUT)
    public void processReview(Review review) {
        logger.info("Processing incoming review from reviewer '{}'", review.getReviewer());
        reviewRepository.save(review);
    }
}
