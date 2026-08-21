package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Company
import com.example.data.model.Employee
import com.example.data.model.User
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("jeevan_prefs", Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_JSON = "user_json"
        private const val KEY_EMPLOYEE_JSON = "employee_json"
        private const val KEY_COMPANY_JSON = "company_json"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_GEOFENCE_OVERRIDE = "geofence_override"
        private const val KEY_USE_MOCK_BACKEND = "use_mock_backend"

        const val DEFAULT_PROD_URL = "https://jeevansabu.in/api/v1"
    }

    var jwtToken: String?
        get() = prefs.getString(KEY_JWT_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_JWT_TOKEN, value).apply()

    var refreshToken: String?
        get() = prefs.getString(KEY_REFRESH_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_REFRESH_TOKEN, value).apply()

    var serverUrl: String
        get() = prefs.getString(KEY_SERVER_URL, DEFAULT_PROD_URL) ?: DEFAULT_PROD_URL
        set(value) = prefs.edit().putString(KEY_SERVER_URL, value).apply()

    var useMockBackend: Boolean
        get() = prefs.getBoolean(KEY_USE_MOCK_BACKEND, true)
        set(value) = prefs.edit().putBoolean(KEY_USE_MOCK_BACKEND, value).apply()

    var geofenceOverride: Boolean
        get() = prefs.getBoolean(KEY_GEOFENCE_OVERRIDE, true) // enabled by default with company coordinates
        set(value) = prefs.edit().putBoolean(KEY_GEOFENCE_OVERRIDE, value).apply()

    fun isLoggedIn(): Boolean = !jwtToken.isNullOrEmpty()

    fun saveUser(user: User) {
        val adapter = moshi.adapter(User::class.java)
        prefs.edit().putString(KEY_USER_JSON, adapter.toJson(user)).apply()
    }

    fun getUser(): User? {
        val json = prefs.getString(KEY_USER_JSON, null) ?: return null
        return try {
            moshi.adapter(User::class.java).fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    fun saveEmployee(employee: Employee) {
        val adapter = moshi.adapter(Employee::class.java)
        prefs.edit().putString(KEY_EMPLOYEE_JSON, adapter.toJson(employee)).apply()
    }

    fun getEmployee(): Employee? {
        val json = prefs.getString(KEY_EMPLOYEE_JSON, null) ?: return null
        return try {
            moshi.adapter(Employee::class.java).fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    fun saveCompany(company: Company) {
        val adapter = moshi.adapter(Company::class.java)
        prefs.edit().putString(KEY_COMPANY_JSON, adapter.toJson(company)).apply()
    }

    fun getCompany(): Company? {
        val json = prefs.getString(KEY_COMPANY_JSON, null) ?: return null
        return try {
            moshi.adapter(Company::class.java).fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_JWT_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_USER_JSON)
            .remove(KEY_EMPLOYEE_JSON)
            .remove(KEY_COMPANY_JSON)
            .apply()
    }
}
