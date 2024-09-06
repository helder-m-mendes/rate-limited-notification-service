package infrasctructure.jms

import jakarta.jms.MessageProducer
import jakarta.jms.Queue
import jakarta.jms.Session
import jakarta.jms.TextMessage

class JmsProducer(
    private val session: Session,
    queue: Queue
) {
    private val producer: MessageProducer = session.createProducer(queue)

    fun sendMessage(message: String, delaySeconds: Int) {
        val textMessage: TextMessage = session.createTextMessage(message)
        producer.deliveryDelay = (delaySeconds * 1000).toLong()
        producer.send(textMessage)
    }
}