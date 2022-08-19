package com.courier.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.courier.android.models.CourierException
import com.courier.android.models.CourierProvider
import com.courier.android.models.CourierPushEvent
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class CourierTests {

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    companion object {
        const val COURIER_USER_ID = "example_user"
        const val COURIER_ACCESS_TOKEN = "pk_prod_X9SHD669JF400NHY56KYPTE639HH"
        const val FIREBASE_API_KEY = "AIzaSyC_9Bq05Ywuy3mjAOkF8rB0LkmyYUoQIrA"
        const val FIREBASE_APP_ID = "1:694725526129:android:77c71528200b105c8811d0"
        const val FIREBASE_PROJECT_ID = "test-fcm-e7ddc"
        const val FIREBASE_GCM_SENDER_ID = "694725526129"
    }

    @Test
    fun test1() = runBlocking {

        print("🔬 Setting FCM Token before User")

        var exception: Exception? = null

        try {
            Courier.instance.setFCMToken(
                token = "something_that_will_fail"
            )
        } catch (e: Exception) {
            exception = e
        }

        assertEquals(exception?.message, CourierException.missingAccessToken.message)
        assertEquals(Courier.instance.userId, null)
        assertEquals(Courier.instance.accessToken, null)

    }

    @Test
    fun test2() = runBlocking {

        print("🔬 Setting credentials before Firebase init")

        var exception: Exception? = null

        try {
            Courier.instance.setCredentials(
                accessToken = COURIER_ACCESS_TOKEN,
                userId = COURIER_USER_ID
            )
        } catch (e: Exception) {
            exception = e
        }

        assert(exception is IllegalStateException)
        assertEquals(Courier.instance.userId, COURIER_USER_ID)
        assertEquals(Courier.instance.accessToken, COURIER_ACCESS_TOKEN)

    }

    @Test
    fun test3() = runBlocking {

        print("🔬 Setting credentials")

        val options = FirebaseOptions.Builder().apply {
            setApiKey(FIREBASE_API_KEY)
            setApplicationId(FIREBASE_APP_ID)
            setProjectId(FIREBASE_PROJECT_ID)
            setGcmSenderId(FIREBASE_GCM_SENDER_ID)
        }.build()

        FirebaseApp.initializeApp(context, options)

        // Firebase is started
        val app = FirebaseApp.getInstance()
        assertEquals(app.options.apiKey, FIREBASE_API_KEY)

        Courier.instance.setCredentials(
            accessToken = COURIER_ACCESS_TOKEN,
            userId = COURIER_USER_ID
        )

        assertEquals(Courier.instance.userId, COURIER_USER_ID)
        assertEquals(Courier.instance.accessToken, COURIER_ACCESS_TOKEN)
        assertNotNull(Courier.instance.fcmToken)

    }

    @Test
    fun test4() = runBlocking {

        print("🔬 Setting FCM Token")

        Courier.instance.setFCMToken(
            token = "something_that_will_succeed"
        )

        assertNotNull(Courier.instance.fcmToken)

    }

    @Test
    fun test5() = runBlocking {

        print("🔬 Sending Push")

        val requestId = Courier.sendPush(
            authKey = COURIER_ACCESS_TOKEN,
            userId = COURIER_USER_ID,
            title = "🐤 Chirp Chirp!",
            body = "Message sent from Xcode tests",
            providers = listOf(CourierProvider.FCM)
        )

        print("Request ID: $requestId")
        assertEquals(requestId.isEmpty(), false)

    }

    @Test
    fun test6() = runBlocking {

        print("🔬 Tracking Message")

        val message = RemoteMessage.Builder(context.packageName)
            .addData("trackingUrl", "https://af6303be-0e1e-40b5-bb80-e1d9299cccff.ct0.app/t/tzgspbr4jcmcy1qkhw96m0034bvy")
            .build()

        Courier.trackNotification(
            message = message,
            event = CourierPushEvent.DELIVERED
        )

        Courier.trackNotification(
            message = message,
            event = CourierPushEvent.CLICKED
        )

        print("Message tracked")

    }

    @Test
    fun test7() = runBlocking {

        print("🔬 Signing Out")

        Courier.instance.signOut()

        assertNotNull(Courier.instance.fcmToken)
        assertEquals(Courier.instance.userId, null)
        assertEquals(Courier.instance.accessToken, null)

    }

}