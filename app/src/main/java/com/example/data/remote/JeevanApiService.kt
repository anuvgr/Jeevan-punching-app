package com.example.data.remote

import com.example.data.model.ApiResponse
import com.example.data.model.Attendance
import com.example.data.model.CreateLeaveRequest
import com.example.data.model.Employee
import com.example.data.model.LeaveBalance
import com.example.data.model.LeaveItem
import com.example.data.model.LoginRequest
import com.example.data.model.LoginResponse
import com.example.data.model.MonthlySummary
import com.example.data.model.PunchRequest
import com.example.data.model.PunchResponse
import com.example.data.model.SalarySlip
import com.example.data.model.TodayAttendanceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface JeevanApiService {

    // Authentication endpoints
    @POST("mobile/auth/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @POST("mobile/auth/refresh")
    suspend fun refreshToken(): Response<LoginResponse>

    @POST("mobile/auth/logout")
    suspend fun logout(): Response<ApiResponse<Boolean>>

    // Profile endpoint
    @GET("mobile/profile")
    suspend fun getProfile(): Response<ApiResponse<Employee>>

    // Attendance endpoints
    @GET("mobile/attendance/today")
    suspend fun getTodayAttendance(): Response<TodayAttendanceResponse>

    @POST("mobile/attendance/punch-in")
    suspend fun punchIn(@Body req: PunchRequest): Response<PunchResponse>

    @POST("mobile/attendance/punch-out")
    suspend fun punchOut(@Body req: PunchRequest): Response<PunchResponse>

    @GET("mobile/attendance/history")
    suspend fun getAttendanceHistory(
        @Query("month") month: String? = null,
        @Query("year") year: Int? = null,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<Attendance>>>

    @GET("mobile/attendance/monthly")
    suspend fun getMonthlySummary(
        @Query("month") month: String,
        @Query("year") year: Int
    ): Response<ApiResponse<MonthlySummary>>

    // Leave endpoints
    @GET("mobile/leave/balance")
    suspend fun getLeaveBalance(): Response<ApiResponse<LeaveBalance>>

    @GET("mobile/leave/history")
    suspend fun getLeaveHistory(): Response<ApiResponse<List<LeaveItem>>>

    @POST("mobile/leave")
    suspend fun applyLeave(@Body req: CreateLeaveRequest): Response<ApiResponse<LeaveItem>>

    @GET("mobile/leave/{id}")
    suspend fun getLeaveById(@Path("id") id: String): Response<ApiResponse<LeaveItem>>

    @DELETE("mobile/leave/{id}")
    suspend fun cancelLeave(@Path("id") id: String): Response<ApiResponse<Boolean>>

    // Salary Slips endpoints
    @GET("mobile/salary-slips")
    suspend fun getSalarySlips(): Response<ApiResponse<List<SalarySlip>>>

    @GET("mobile/salary-slips/{id}")
    suspend fun getSalarySlipById(@Path("id") id: String): Response<ApiResponse<SalarySlip>>
}
