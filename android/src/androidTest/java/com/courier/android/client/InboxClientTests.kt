package com.courier.android.client

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.courier.android.Env
import com.courier.android.ExampleServer
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class InboxClientTests {

    private lateinit var client: CourierClient
    private val connectionId = UUID.randomUUID().toString()

    @Before
    fun setup() = runBlocking {
        client = ClientBuilder.build(
            connectionId = connectionId
        )
    }

    private suspend fun sendMessage(): String {
        return ExampleServer.sendTest(
            authKey = Env.COURIER_AUTH_KEY,
            userId = Env.COURIER_USER_ID,
            channel = "inbox"
        )
    }

    @Test
    fun getMessage() = runBlocking {

        val messageId = sendMessage()

        delay(5000) // Pipeline delay

        val res = client.inbox.getMessage(
            messageId = messageId
        )

        val message = res.data?.message

        assertNotNull(message)

    }

    @Test
    fun getMessages() = runBlocking {

        val limit = 24

        val res = client.inbox.getMessages(
            paginationLimit = limit,
            startCursor = null,
        )

        assertTrue(res.data?.messages?.nodes?.size!! <= limit)

    }

    @Test
    fun getUnreadMessageCount() = runBlocking {

        sendMessage()

        delay(5000) // Pipeline delay

        val count = client.inbox.getUnreadMessageCount()

        assertTrue(count >= 0)

    }

    // TODO: This response object is botched. Need identical objects.
    @Test
    fun trackClick() = runBlocking {

//        val messageId = sendMessage()
//
//        delay(5000) // Pipeline delay
//
//        val res = client.inbox.getMessage(
//            messageId = messageId
//        )
//
//        val message = res.data?.message
//
//        assertNotNull(message)
//
//        val trackingId = message!!.clickTrackingId
//
//        assertNotNull(trackingId)
//
//        client.inbox.trackClick(
//            messageId = message.messageId,
//            trackingId = trackingId!!
//        )

    }

    @Test
    fun trackOpen() = runBlocking {

        val messageId = sendMessage()

        client.inbox.trackOpened(
            messageId = messageId,
        )

    }

    @Test
    fun trackRead() = runBlocking {

        val messageId = sendMessage()

        client.inbox.trackRead(
            messageId = messageId,
        )

    }

    @Test
    fun trackUnread() = runBlocking {

        val messageId = sendMessage()

        client.inbox.trackUnread(
            messageId = messageId,
        )

    }

    @Test
    fun trackArchive() = runBlocking {

        val messageId = sendMessage()

        client.inbox.trackArchive(
            messageId = messageId,
        )

    }

    @Test
    fun trackReadAll() = runBlocking {

        client.inbox.trackAllRead()

    }

    @Test
    fun socketConnectionTest() = runBlocking {

        var hold1 = true
        var hold2 = true

        // Open the first socket connection
        val client1 = ClientBuilder.build(connectionId = UUID.randomUUID().toString()).apply {

            val socket = inbox.socket

            socket.onOpen = {
                println("Socket Opened")
            }

            socket.onClose = { code, reason ->
                println("Socket closed: $code, $reason")
            }

            socket.onError = { error ->
                assertNull(error)
            }

            socket.receivedMessageEvent = { event ->
                println(event)
            }

            socket.receivedMessage = { message ->
                println(message)
                hold1 = false
            }

            socket.connect()
            socket.sendSubscribe()

        }

        // Open the second socket connection
        val client2 = ClientBuilder.build(connectionId = UUID.randomUUID().toString()).apply {

            val socket = inbox.socket

            socket.onOpen = {
                println("Socket Opened")
            }

            socket.onClose = { code, reason ->
                println("Socket closed: $code, $reason")
            }

            socket.onError = { error ->
                assertNull(error)
            }

            socket.receivedMessageEvent = { event ->
                println(event)
            }

            socket.receivedMessage = { message ->
                println(message)
                hold2 = false
            }

            socket.connect()
            socket.sendSubscribe()

        }

        val messageId = sendMessage()

        print(messageId)

        while (hold1 && hold2) {
            // Wait for the message to be received in the sockets
        }

        client1.inbox.socket.disconnect()
        client2.inbox.socket.disconnect()

    }

}