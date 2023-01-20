package co.reby.rnkustomersdk

import com.facebook.react.bridge.*
import com.kustomer.core.models.*
import com.kustomer.core.models.chat.*
import org.json.JSONObject
import org.json.JSONException
import com.kustomer.ui.Kustomer
import kotlinx.coroutines.runBlocking

class KustomerSDKModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {
        
    override fun getName(): String {
        return "KustomerSDK"
    }

    @ReactMethod
    fun identify(token:String, promise: Promise) {
        Kustomer.getInstance().logIn(token) {
            when (it) {
                is KusResult.Success -> {
                    promise.resolve(it.data)
                }
                is KusResult.Error -> {
                    promise.reject(it.exception.localizedMessage)
                }
            }
        }    
    }

    @ReactMethod
    fun presentSupport() {
        Kustomer.getInstance().open(KusPreferredView.DEFAULT)
    }

    @ReactMethod
    fun openConversationsCount(promise: Promise) {
        promise.resolve(Kustomer.getInstance().observeActiveConversationIds())
    }

    @ReactMethod
    fun resetTracking() {
        Kustomer.getInstance().logOut()
    }

    @ReactMethod
    fun describeCustomer(data: ReadableMap, promise: Promise)  {
        var email: String = ""
        var phone: String = ""
        when (data.hasKey("email")) {
            true -> email = data.getString("email")!!
            false -> promise.reject("No email provided")
        }
        when (data.hasKey("phone") && data.getString("phone") != null) {
            true -> phone = data.getString("phone")!!
            false -> promise.reject("No phone number provided")
        }
        val attributes = KusCustomerDescribeAttributes(
            emails = listOf(KusEmail(email)),
            phones = listOf(KusPhone(phone)),
            custom = toMap(data.getMap("custom")!!)!!
        )

        runBlocking {
           val result =  Kustomer.getInstance().describeCustomer(attributes)
            when (result) {
                is KusResult.Success -> promise.resolve("Success")
                is KusResult.Error -> promise.reject("error")
            }
        }
        promise.resolve(null)
    }

    private fun toMap(readableMap: ReadableMap): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        val iterator: ReadableMapKeySetIterator = readableMap.keySetIterator()
    
        while (iterator.hasNextKey()) {
            val key: String = iterator.nextKey()
            val type: ReadableType = readableMap.getType(key)

            when (type) {
                ReadableType.Boolean -> map[key] = readableMap.getBoolean(key).toString()
                ReadableType.Number -> map[key] = readableMap.getDouble(key).toString()
                ReadableType.String -> {
                    val value: String? = readableMap.getString(key)
                    if (value != null && !value.isEmpty()) {
                        map[key] = value
                    }   
                }
            }
        }
        return map 
    }
}