import org.junit.jupiter.api.Assertions.assertEquals
import org.main
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.Test

class MainTest {
    @Test
    fun `Given a normal environment the code should be run thoroughly`() {
        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        val originalOut = System.out
        System.setOut(printStream)
        try {
            main()
            val output = outputStream.toString().trim()
            assertEquals("Sending notification: news, user, news 1\n" +
                    "Sending notification: news, user, news 2\n" +
                    "Sending notification: news, user, news 3\n" +
                    "Sending notification: news, another user, news 1\n" +
                    "Sending notification: update, user, update 1", output)
        } finally {
            System.setOut(originalOut)
        }
    }
}