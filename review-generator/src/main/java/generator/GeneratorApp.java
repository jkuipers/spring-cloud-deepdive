package generator;

import static java.lang.Math.abs;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;

@SpringBootApplication
@EnableBinding(Source.class)
public class GeneratorApp {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(GeneratorApp.class, args);
    }

    private Logger logger = LoggerFactory.getLogger(getClass());

    @InboundChannelAdapter(channel = Source.OUTPUT,
                           poller = @Poller(fixedRate = "1000"))
    public RandomReview submitReview() {
        logger.info("Submitting another review");
        return new RandomReview();
    }

    static class RandomReview {

        static final Random RANDOM = new Random();
        static final String[] COMMENTS = {
                "Loved it!",
                "Blew me away",
                "Can't wait to use this on my own projects",
                "Liked the previous talk better",
                "Was hoping for more XML"
        };
        static final String[] REVIEWERS = {
                "Anonymous",
                "Rod",
                "Rob",
                "Keith",
                "Colin"
        };

        private long talkId;
        private int score;
        private String comments;
        private String reviewer;

        public RandomReview() {
            this.talkId = abs(RANDOM.nextLong()) % 5;
            this.score = (abs(RANDOM.nextInt()) % 5) + 1;
            this.comments = COMMENTS[abs(RANDOM.nextInt()) % COMMENTS.length];
            this.reviewer = REVIEWERS[abs(RANDOM.nextInt()) % REVIEWERS.length];
        }

        public long getTalkId() {
            return talkId;
        }

        public int getScore() {
            return score;
        }

        public String getComments() {
            return comments;
        }

        public String getReviewer() {
            return reviewer;
        }
    }
}
