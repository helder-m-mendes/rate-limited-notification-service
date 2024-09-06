import dtos.RateLimitRule
import infrasctructure.jms.JmsConfig
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.main.main
import org.services.RateLimiterImpl
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class MainTest {
    private lateinit var sqsClient: SqsClient
    private lateinit var queueUrl: String
    private lateinit var rateLimiter: RateLimiterImpl

    @BeforeEach
    fun setUp() {
        queueUrl = JmsConfig.queueUrl()

        // Purge the queue to ensure it's empty before each test
        JmsConfig.sqsClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(queueUrl).build())

        // Define rate limit rules
        val rules = listOf(
            RateLimitRule("status", 2, 1.minutes),
            RateLimitRule("news", 1, 1.days),
            RateLimitRule("marketing", 3, 1.hours),
            RateLimitRule("default", 10, 1.minutes)
        )

        // Initialize RateLimiter
        rateLimiter = RateLimiterImpl(rules)
    }

    @Test
    fun testMainFunction() = runBlocking {
        // Call the main function
        main()

        // Verify the state of the buckets
        val newsBucket = rateLimiter.getBucket("user", "news")
        val anotherUserNewsBucket = rateLimiter.getBucket("another user", "news")
        val updateBucket = rateLimiter.getBucket("user", "update")

        // Assert the tokens remaining in the buckets
        assertEquals(1, newsBucket.getAvailableTokens())
        assertEquals(1, anotherUserNewsBucket.getAvailableTokens())
        assertEquals(10, updateBucket.getAvailableTokens())
    }
}