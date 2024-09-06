package infrasctructure.jms

import com.amazon.sqs.javamessaging.SQSConnectionFactory
import com.amazon.sqs.javamessaging.ProviderConfiguration
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import java.net.URI

object JmsConfig {
    val queueName = "notification-queue"
    val sqsClient: SqsClient = SqsClient.builder()
        .endpointOverride(URI.create("http://localhost:4566")) // LocalStack endpoint
        .region(Region.US_EAST_1)
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create("accessKey", "secretKey")
            )
        )
        .build()

    fun createConnectionFactory(): SQSConnectionFactory {
        return SQSConnectionFactory(ProviderConfiguration(), sqsClient)
    }

    fun queueUrl(): String {
        return try {
            sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl()
        } catch (e: Exception) {
            try {
                sqsClient.createQueue(CreateQueueRequest.builder().queueName(queueName).build()).queueUrl()
            } catch (
                e: Exception
            ) {
                throw RuntimeException("Failed to get queue URL for $queueName", e)
            }
        }
    }
}