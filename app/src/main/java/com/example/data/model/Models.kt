package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val id: String = "usr_001_js",
    val username: String = "EMP1008",
    val email: String = "rahul.nair@jeevansabu.in",
    val role: String = "employee",
    @param:Json(name = "employee_id") val employeeId: String? = "emp_1008",
    @param:Json(name = "company_id") val companyId: String? = "cmp_jeevan_01"
)

@JsonClass(generateAdapter = true)
data class Employee(
    val id: String = "emp_1008",
    @param:Json(name = "employee_code") val employeeCode: String = "JEV-1008",
    val name: String = "Rahul M. Nair",
    val email: String = "rahul.nair@jeevansabu.in",
    val phone: String? = "+91 98471 23456",
    val department: String = "Operations & Logistics",
    val designation: String = "Senior Operations Executive",
    @param:Json(name = "company_id") val companyId: String = "cmp_jeevan_01",
    @param:Json(name = "company_name") val companyName: String = "Jeevan Companies India Ltd.",
    @param:Json(name = "joining_date") val joiningDate: String? = "2023-04-10",
    @param:Json(name = "shift_name") val shiftName: String? = "General Day Shift (09:00 AM - 06:00 PM)",
    @param:Json(name = "avatar_url") val avatarUrl: String? = null,
    @param:Json(name = "pan_number") val panNumber: String? = "ABCDE8765F",
    @param:Json(name = "bank_account_masked") val bankAccountMasked: String? = "•••• •••• 8912",
    @param:Json(name = "pf_uan") val pfUan: String? = "100987654321"
)

@JsonClass(generateAdapter = true)
data class Company(
    val id: String = "cmp_jeevan_01",
    val name: String = "Jeevan Companies India Ltd.",
    val code: String = "JEEVAN-HQ",
    val address: String? = "Jeevan Tower, Marine Drive, Kochi, Kerala 682031",
    val phone: String? = "+91 484 2345678",
    @param:Json(name = "office_latitude") val officeLatitude: Double? = 9.9723,
    @param:Json(name = "office_longitude") val officeLongitude: Double? = 76.2784,
    @param:Json(name = "geofence_radius_meters") val geofenceRadiusMeters: Int? = 300,
    @param:Json(name = "geofence_enabled") val geofenceEnabled: Boolean? = true
)

@JsonClass(generateAdapter = true)
data class Attendance(
    val id: String = "att_001",
    @param:Json(name = "employee_id") val employeeId: String = "emp_1008",
    @param:Json(name = "company_id") val companyId: String = "cmp_jeevan_01",
    val date: String = "2026-08-21",
    val status: String = "Present", // "Present", "Absent", "Half Day", "On Leave", "Holiday"
    val clockIn: String? = null,
    val clockOut: String? = null,
    val shiftTime: String? = "09:00 AM - 06:00 PM",
    val totalHours: String? = null,
    val notes: String? = null,
    val punchInLocation: String? = null,
    val punchOutLocation: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class PunchRequest(
    @param:Json(name = "device_id") val deviceId: String = "DEV_9842",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val notes: String? = null
)

@JsonClass(generateAdapter = true)
data class PunchResponse(
    val success: Boolean = true,
    val message: String = "Punch recorded successfully",
    val attendance: Attendance? = null,
    val serverTime: String? = null
)

@JsonClass(generateAdapter = true)
data class TodayAttendanceResponse(
    val success: Boolean = true,
    val attendance: Attendance? = null,
    val serverTime: String = "09:30:00",
    val serverDate: String = "2026-08-21",
    val isPunchedIn: Boolean = false,
    val isPunchedOut: Boolean = false
)

@JsonClass(generateAdapter = true)
data class MonthlySummary(
    val month: String = "August",
    val year: Int = 2026,
    val presentDays: Int = 19,
    val absentDays: Int = 0,
    val leaveDays: Int = 1,
    val halfDays: Int = 0,
    val holidays: Int = 3,
    val lateDays: Int = 2,
    val totalWorkingHours: Double = 158.5,
    val overtimeHours: Double = 6.5
)

@JsonClass(generateAdapter = true)
data class LeaveBalance(
    val casualLeave: Double = 8.5,
    val sickLeave: Double = 6.0,
    val earnedLeave: Double = 14.0,
    val compOff: Double = 2.0
)

@JsonClass(generateAdapter = true)
data class LeaveItem(
    val id: String = "lev_001",
    @param:Json(name = "employee_id") val employeeId: String = "emp_1008",
    @param:Json(name = "leave_type") val leaveType: String = "Casual Leave",
    @param:Json(name = "from_date") val fromDate: String = "2026-08-10",
    @param:Json(name = "to_date") val toDate: String = "2026-08-10",
    @param:Json(name = "is_half_day") val isHalfDay: Boolean = false,
    val days: Double = 1.0,
    val reason: String = "Family event",
    val status: String = "APPROVED", // "PENDING", "APPROVED", "REJECTED", "CANCELLED"
    @param:Json(name = "applied_at") val appliedAt: String = "2026-08-08 10:30 AM",
    @param:Json(name = "reviewed_by") val reviewedBy: String? = null,
    @param:Json(name = "review_comments") val reviewComments: String? = null
)

@JsonClass(generateAdapter = true)
data class CreateLeaveRequest(
    @param:Json(name = "leave_type") val leaveType: String = "Casual Leave",
    @param:Json(name = "from_date") val fromDate: String = "",
    @param:Json(name = "to_date") val toDate: String = "",
    @param:Json(name = "is_half_day") val isHalfDay: Boolean = false,
    val reason: String = ""
)

@JsonClass(generateAdapter = true)
data class SalarySlip(
    val id: String = "sal_001",
    @param:Json(name = "employee_id") val employeeId: String = "emp_1008",
    @param:Json(name = "salary_month") val salaryMonth: String = "July 2026",
    val year: Int = 2026,
    val month: Int = 7,
    @param:Json(name = "basic_salary") val basicSalary: Double = 32000.0,
    val hra: Double = 12800.0,
    val da: Double = 6400.0,
    @param:Json(name = "special_allowance") val specialAllowance: Double = 8800.0,
    @param:Json(name = "gross_salary") val grossSalary: Double = 60000.0,
    val pf: Double = 3840.0,
    val esi: Double = 450.0,
    val advance: Double = 0.0,
    @param:Json(name = "professional_tax") val professionalTax: Double = 200.0,
    @param:Json(name = "other_deductions") val otherDeductions: Double = 0.0,
    @param:Json(name = "total_deductions") val totalDeductions: Double = 4490.0,
    @param:Json(name = "net_salary") val netSalary: Double = 55510.0,
    @param:Json(name = "paid_days") val paidDays: Int = 31,
    @param:Json(name = "payment_date") val paymentDate: String = "2026-08-01",
    @param:Json(name = "payment_status") val paymentStatus: String = "Paid",
    @param:Json(name = "bank_name") val bankName: String = "HDFC Bank (Marine Drive Br.)",
    @param:Json(name = "transaction_ref") val transactionRef: String = "JEV-PAY-202607-8912"
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val username: String,
    val password: String,
    @param:Json(name = "device_id") val deviceId: String? = null
)

@JsonClass(generateAdapter = true)
data class LoginData(
    val token: String? = null,
    @param:Json(name = "refresh_token") val refreshToken: String? = null,
    val user: User? = null,
    val employee: Employee? = null,
    val company: Company? = null
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val success: Boolean = true,
    val message: String? = null,
    val token: String? = null,
    @param:Json(name = "refresh_token") val refreshToken: String? = null,
    val user: User? = null,
    val employee: Employee? = null,
    val company: Company? = null,
    val data: LoginData? = null
)

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val success: Boolean = true,
    val message: String? = null,
    val data: T? = null
)
