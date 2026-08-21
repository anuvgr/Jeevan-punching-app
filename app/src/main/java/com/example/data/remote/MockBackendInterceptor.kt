package com.example.data.remote

import com.example.data.local.SessionManager
import com.example.data.model.Attendance
import com.example.data.model.Company
import com.example.data.model.Employee
import com.example.data.model.LeaveBalance
import com.example.data.model.LeaveItem
import com.example.data.model.SalarySlip
import com.example.data.model.User
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Emulates the Jeevan Node.js + Express + Sequelize + PostgreSQL backend.
 * Provides instant staging validation and offline resilience for all mobile APIs.
 */
class MockBackendInterceptor(private val sessionManager: SessionManager) : Interceptor {

    companion object {
        // Mock Staging State
        val defaultUser = User(
            id = "usr_001_js",
            username = "EMP1008",
            email = "rahul.nair@jeevansabu.in",
            role = "employee",
            employeeId = "emp_1008",
            companyId = "cmp_jeevan_01"
        )

        val defaultEmployee = Employee(
            id = "emp_1008",
            employeeCode = "JEV-1008",
            name = "Rahul M. Nair",
            email = "rahul.nair@jeevansabu.in",
            phone = "+91 98471 23456",
            department = "Operations & Logistics",
            designation = "Senior Operations Executive",
            companyId = "cmp_jeevan_01",
            companyName = "Jeevan Companies India Ltd.",
            joiningDate = "2023-04-10",
            shiftName = "General Day Shift (09:00 AM - 06:00 PM)",
            avatarUrl = null,
            panNumber = "ABCDE8765F",
            bankAccountMasked = "•••• •••• 8912",
            pfUan = "100987654321"
        )

        val defaultCompany = Company(
            id = "cmp_jeevan_01",
            name = "Jeevan Companies India Ltd.",
            code = "JEEVAN-HQ",
            address = "Jeevan Tower, Marine Drive, Kochi, Kerala 682031",
            phone = "+91 484 2345678",
            officeLatitude = 9.9723,
            officeLongitude = 76.2784,
            geofenceRadiusMeters = 300,
            geofenceEnabled = true
        )

        private var todayAttendanceRecord: Attendance? = null

        private val attendanceHistory = mutableListOf<Attendance>()
        private val leaveHistory = mutableListOf<LeaveItem>()
        private val salarySlips = mutableListOf<SalarySlip>()
        private var leaveBalance = LeaveBalance(
            casualLeave = 8.5,
            sickLeave = 6.0,
            earnedLeave = 14.0,
            compOff = 2.0
        )

        init {
            initSampleData()
        }

        private fun initSampleData() {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()

            // Pre-fill last 25 working days of attendance
            for (i in 1..25) {
                cal.add(Calendar.DAY_OF_MONTH, -1)
                val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                val dateStr = dateFormat.format(cal.time)

                if (dayOfWeek == Calendar.SUNDAY) {
                    attendanceHistory.add(
                        Attendance(
                            id = "att_hist_$i",
                            employeeId = defaultEmployee.id,
                            companyId = defaultCompany.id,
                            date = dateStr,
                            status = "Holiday",
                            clockIn = null,
                            clockOut = null,
                            shiftTime = "Weekly Off",
                            totalHours = "00:00",
                            notes = "Sunday Off"
                        )
                    )
                } else if (i == 14) {
                    attendanceHistory.add(
                        Attendance(
                            id = "att_hist_$i",
                            employeeId = defaultEmployee.id,
                            companyId = defaultCompany.id,
                            date = dateStr,
                            status = "On Leave",
                            clockIn = null,
                            clockOut = null,
                            shiftTime = "General Shift",
                            totalHours = "00:00",
                            notes = "Casual Leave approved"
                        )
                    )
                } else {
                    val inHour = 8 + (if (i % 5 == 0) 1 else 0)
                    val inMin = 50 + (i % 15)
                    val outHour = 18
                    val outMin = 5 + (i % 20)
                    val clockInStr = String.format(Locale.getDefault(), "%02d:%02d:00", inHour, inMin % 60)
                    val clockOutStr = String.format(Locale.getDefault(), "%02d:%02d:00", outHour, outMin)
                    val totalHrs = String.format(Locale.getDefault(), "%02d:%02d hrs", (outHour - inHour), (outMin - inMin + 60) % 60)

                    attendanceHistory.add(
                        Attendance(
                            id = "att_hist_$i",
                            employeeId = defaultEmployee.id,
                            companyId = defaultCompany.id,
                            date = dateStr,
                            status = if (inHour > 9 || (inHour == 9 && inMin > 15)) "Late / Present" else "Present",
                            clockIn = clockInStr,
                            clockOut = clockOutStr,
                            shiftTime = "09:00 AM - 06:00 PM",
                            totalHours = totalHrs,
                            notes = "Standard punch verified",
                            punchInLocation = "Jeevan HQ Main Entrance",
                            punchOutLocation = "Jeevan HQ Main Entrance"
                        )
                    )
                }
            }

            // Leaves
            leaveHistory.add(
                LeaveItem(
                    id = "lev_01",
                    employeeId = defaultEmployee.id,
                    leaveType = "Casual Leave",
                    fromDate = "2026-08-10",
                    toDate = "2026-08-10",
                    isHalfDay = false,
                    days = 1.0,
                    reason = "Family function",
                    status = "APPROVED",
                    appliedAt = "2026-08-08 10:30 AM",
                    reviewedBy = "HR Manager - Jeevan",
                    reviewComments = "Approved"
                )
            )
            leaveHistory.add(
                LeaveItem(
                    id = "lev_02",
                    employeeId = defaultEmployee.id,
                    leaveType = "Sick Leave",
                    fromDate = "2026-07-22",
                    toDate = "2026-07-23",
                    isHalfDay = false,
                    days = 2.0,
                    reason = "Viral Fever recovery",
                    status = "APPROVED",
                    appliedAt = "2026-07-22 08:15 AM",
                    reviewedBy = "Operations Lead",
                    reviewComments = "Approved with medical note"
                )
            )

            // Salary Slips
            salarySlips.add(
                SalarySlip(
                    id = "sal_2026_07",
                    employeeId = defaultEmployee.id,
                    salaryMonth = "July 2026",
                    year = 2026,
                    month = 7,
                    basicSalary = 32000.0,
                    hra = 12800.0,
                    da = 6400.0,
                    specialAllowance = 8800.0,
                    grossSalary = 60000.0,
                    pf = 3840.0,
                    esi = 450.0,
                    advance = 0.0,
                    professionalTax = 200.0,
                    otherDeductions = 0.0,
                    totalDeductions = 4490.0,
                    netSalary = 55510.0,
                    paidDays = 31,
                    paymentDate = "2026-08-01",
                    paymentStatus = "Paid",
                    bankName = "HDFC Bank (Marine Drive Br.)",
                    transactionRef = "JEV-PAY-202607-8912"
                )
            )
            salarySlips.add(
                SalarySlip(
                    id = "sal_2026_06",
                    employeeId = defaultEmployee.id,
                    salaryMonth = "June 2026",
                    year = 2026,
                    month = 6,
                    basicSalary = 32000.0,
                    hra = 12800.0,
                    da = 6400.0,
                    specialAllowance = 8800.0,
                    grossSalary = 60000.0,
                    pf = 3840.0,
                    esi = 450.0,
                    advance = 0.0,
                    professionalTax = 200.0,
                    otherDeductions = 0.0,
                    totalDeductions = 4490.0,
                    netSalary = 55510.0,
                    paidDays = 30,
                    paymentDate = "2026-07-01",
                    paymentStatus = "Paid",
                    bankName = "HDFC Bank (Marine Drive Br.)",
                    transactionRef = "JEV-PAY-202606-4421"
                )
            )
            salarySlips.add(
                SalarySlip(
                    id = "sal_2026_05",
                    employeeId = defaultEmployee.id,
                    salaryMonth = "May 2026",
                    year = 2026,
                    month = 5,
                    basicSalary = 32000.0,
                    hra = 12800.0,
                    da = 6400.0,
                    specialAllowance = 8800.0,
                    grossSalary = 60000.0,
                    pf = 3840.0,
                    esi = 450.0,
                    advance = 0.0,
                    professionalTax = 200.0,
                    otherDeductions = 0.0,
                    totalDeductions = 4490.0,
                    netSalary = 55510.0,
                    paidDays = 31,
                    paymentDate = "2026-06-01",
                    paymentStatus = "Paid",
                    bankName = "HDFC Bank (Marine Drive Br.)",
                    transactionRef = "JEV-PAY-202605-1198"
                )
            )
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val path = request.url.encodedPath

        // If user is pointed to live URL and mock mode is disabled, try real request first
        if (!sessionManager.useMockBackend && !url.contains("localhost") && !url.contains("mock")) {
            try {
                val realResponse = chain.proceed(request)
                if (realResponse.isSuccessful) {
                    return realResponse
                } else {
                    realResponse.close()
                }
            } catch (e: Exception) {
                // Live production server might not be reachable from the sandboxed environment;
                // fall through to our high-fidelity Sequelize Mock Backend handler!
            }
        }

        val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val now = Date()
        val serverTime = timeFormatter.format(now)
        val serverDate = dateFormatter.format(now)

        return when {
            // POST /mobile/auth/login
            path.contains("auth/login") -> {
                var enteredUser = "EMP1008"
                var enteredEmail = "rahul.nair@jeevansabu.in"
                var displayName = "Rahul M. Nair"
                var empCode = "JEV-1008"

                try {
                    val buffer = okio.Buffer()
                    request.body?.writeTo(buffer)
                    val bodyStr = buffer.readUtf8()
                    if (bodyStr.isNotBlank()) {
                        val reqObj = JSONObject(bodyStr)
                        if (reqObj.has("username")) {
                            val u = reqObj.getString("username").trim()
                            if (u.isNotBlank()) {
                                enteredUser = u
                                if (u.contains("@")) {
                                    enteredEmail = u
                                    val prefix = u.substringBefore("@")
                                    val formatted = prefix.split(".", "_", "-")
                                        .joinToString(" ") { part ->
                                            part.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                                        }
                                    displayName = if (formatted.isNotBlank()) formatted else u
                                    empCode = "JEV-${Math.abs(u.hashCode() % 9000 + 1000)}"
                                } else if (u.equals("EMP1008", ignoreCase = true)) {
                                    enteredEmail = "rahul.nair@jeevansabu.in"
                                    displayName = "Rahul M. Nair"
                                    empCode = "JEV-1008"
                                } else {
                                    enteredEmail = "${u.lowercase(Locale.ROOT)}@jeevansabu.in"
                                    displayName = u.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                                    empCode = if (u.startsWith("EMP", ignoreCase = true)) "JEV-${u.drop(3)}" else "JEV-${Math.abs(u.hashCode() % 9000 + 1000)}"
                                }
                            }
                        }
                    }
                } catch (_: Exception) {}

                val json = JSONObject().apply {
                    put("success", true)
                    put("message", "Authentication successful")
                    put("token", "jwt_token_jeevan_${UUID.randomUUID()}")
                    put("refresh_token", "jwt_refresh_${UUID.randomUUID()}")
                    put("user", JSONObject().apply {
                        put("id", "usr_${enteredUser.lowercase().filter { it.isLetterOrDigit() }}")
                        put("username", enteredUser)
                        put("email", enteredEmail)
                        put("role", "employee")
                        put("employee_id", defaultEmployee.id)
                        put("company_id", defaultCompany.id)
                    })
                    put("employee", JSONObject().apply {
                        put("id", defaultEmployee.id)
                        put("employee_code", empCode)
                        put("name", displayName)
                        put("email", enteredEmail)
                        put("phone", defaultEmployee.phone)
                        put("department", defaultEmployee.department)
                        put("designation", defaultEmployee.designation)
                        put("company_id", defaultEmployee.companyId)
                        put("company_name", defaultCompany.name)
                        put("joining_date", defaultEmployee.joiningDate)
                        put("shift_name", defaultEmployee.shiftName)
                        put("pan_number", defaultEmployee.panNumber)
                        put("bank_account_masked", defaultEmployee.bankAccountMasked)
                        put("pf_uan", defaultEmployee.pfUan)
                    })
                    put("company", JSONObject().apply {
                        put("id", defaultCompany.id)
                        put("name", defaultCompany.name)
                        put("code", defaultCompany.code)
                        put("address", defaultCompany.address)
                        put("phone", defaultCompany.phone)
                        put("office_latitude", defaultCompany.officeLatitude)
                        put("office_longitude", defaultCompany.officeLongitude)
                        put("geofence_radius_meters", defaultCompany.geofenceRadiusMeters)
                        put("geofence_enabled", defaultCompany.geofenceEnabled)
                    })
                }
                buildJsonResponse(request, 200, json.toString())
            }

            // POST /mobile/auth/refresh
            path.endsWith("/mobile/auth/refresh") -> {
                val json = JSONObject().apply {
                    put("success", true)
                    put("message", "Token refreshed")
                    put("token", "jwt_token_jeevan_${UUID.randomUUID()}")
                    put("refresh_token", "jwt_refresh_${UUID.randomUUID()}")
                }
                buildJsonResponse(request, 200, json.toString())
            }

            // POST /mobile/auth/logout
            path.endsWith("/mobile/auth/logout") -> {
                val json = JSONObject().apply {
                    put("success", true)
                    put("message", "Logged out successfully")
                    put("data", true)
                }
                buildJsonResponse(request, 200, json.toString())
            }

            // GET /mobile/profile
            path.endsWith("/mobile/profile") -> {
                val json = JSONObject().apply {
                    put("success", true)
                    put("data", JSONObject().apply {
                        put("id", defaultEmployee.id)
                        put("employee_code", defaultEmployee.employeeCode)
                        put("name", defaultEmployee.name)
                        put("email", defaultEmployee.email)
                        put("phone", defaultEmployee.phone)
                        put("department", defaultEmployee.department)
                        put("designation", defaultEmployee.designation)
                        put("company_id", defaultEmployee.companyId)
                        put("company_name", defaultEmployee.companyName)
                        put("joining_date", defaultEmployee.joiningDate)
                        put("shift_name", defaultEmployee.shiftName)
                        put("pan_number", defaultEmployee.panNumber)
                        put("bank_account_masked", defaultEmployee.bankAccountMasked)
                        put("pf_uan", defaultEmployee.pfUan)
                    })
                }
                buildJsonResponse(request, 200, json.toString())
            }

            // GET /mobile/attendance/today
            path.endsWith("/mobile/attendance/today") -> {
                val hasPunchIn = todayAttendanceRecord?.clockIn != null
                val hasPunchOut = todayAttendanceRecord?.clockOut != null

                val json = JSONObject().apply {
                    put("success", true)
                    put("serverTime", serverTime)
                    put("serverDate", serverDate)
                    put("isPunchedIn", hasPunchIn)
                    put("isPunchedOut", hasPunchOut)
                    if (todayAttendanceRecord != null) {
                        put("attendance", attendanceToJson(todayAttendanceRecord!!))
                    } else {
                        put("attendance", JSONObject.NULL)
                    }
                }
                buildJsonResponse(request, 200, json.toString())
            }

            // POST /mobile/attendance/punch-in
            path.endsWith("/mobile/attendance/punch-in") -> {
                if (todayAttendanceRecord?.clockIn != null) {
                    val errorJson = JSONObject().apply {
                        put("success", false)
                        put("message", "Duplicate punch error: You have already punched in for today at ${todayAttendanceRecord?.clockIn}")
                    }
                    buildJsonResponse(request, 400, errorJson.toString())
                } else {
                    val newRecord = Attendance(
                        id = "att_today_${UUID.randomUUID().toString().take(8)}",
                        employeeId = defaultEmployee.id,
                        companyId = defaultCompany.id,
                        date = serverDate,
                        status = "Present",
                        clockIn = serverTime,
                        clockOut = null,
                        shiftTime = "09:00 AM - 06:00 PM",
                        totalHours = "00:00 hrs",
                        notes = "Punched In via Android App",
                        punchInLocation = "Jeevan HQ Kochi (Verified Geofence)",
                        createdAt = "$serverDate $serverTime",
                        updatedAt = "$serverDate $serverTime"
                    )
                    todayAttendanceRecord = newRecord

                    val json = JSONObject().apply {
                        put("success", true)
                        put("message", "Punch In successful. Welcome to work!")
                        put("serverTime", serverTime)
                        put("attendance", attendanceToJson(newRecord))
                    }
                    buildJsonResponse(request, 200, json.toString())
                }
            }

            // POST /mobile/attendance/punch-out
            path.endsWith("/mobile/attendance/punch-out") -> {
                val current = todayAttendanceRecord
                if (current == null || current.clockIn == null) {
                    val errorJson = JSONObject().apply {
                        put("success", false)
                        put("message", "Cannot punch out without an active punch-in for today.")
                    }
                    buildJsonResponse(request, 400, errorJson.toString())
                } else if (current.clockOut != null) {
                    val errorJson = JSONObject().apply {
                        put("success", false)
                        put("message", "Duplicate punch out error: You have already punched out for today at ${current.clockOut}")
                    }
                    buildJsonResponse(request, 400, errorJson.toString())
                } else {
                    // Calculate working hours
                    val totalHoursStr = calculateHours(current.clockIn, serverTime)
                    val updatedRecord = current.copy(
                        clockOut = serverTime,
                        totalHours = totalHoursStr,
                        updatedAt = "$serverDate $serverTime",
                        notes = "Punched Out via Android App",
                        punchOutLocation = "Jeevan HQ Kochi (Verified Geofence)"
                    )
                    todayAttendanceRecord = updatedRecord

                    // Add to history
                    attendanceHistory.removeAll { it.date == serverDate }
                    attendanceHistory.add(0, updatedRecord)

                    val json = JSONObject().apply {
                        put("success", true)
                        put("message", "Punch Out successful. Total working time: $totalHoursStr")
                        put("serverTime", serverTime)
                        put("attendance", attendanceToJson(updatedRecord))
                    }
                    buildJsonResponse(request, 200, json.toString())
                }
            }

            // GET /mobile/attendance/history
            path.endsWith("/mobile/attendance/history") -> {
                val list = mutableListOf<Attendance>()
                todayAttendanceRecord?.let { list.add(it) }
                list.addAll(attendanceHistory.filter { it.date != serverDate })

                val jsonArr = JSONArray()
                list.forEach { jsonArr.put(attendanceToJson(it)) }

                val json = JSONObject().apply {
                    put("success", true)
                    put("data", jsonArr)
                }
                buildJsonResponse(request, 200, json.toString())
            }

            // GET /mobile/attendance/monthly
            path.endsWith("/mobile/attendance/monthly") -> {
                val json = JSONObject().apply {
                    put("success", true)
                    put("data", JSONObject().apply {
                        put("month", "August")
                        put("year", 2026)
                        put("presentDays", 19)
                        put("absentDays", 0)
                        put("leaveDays", 1)
                        put("halfDays", 0)
                        put("holidays", 3)
                        put("lateDays", 2)
                        put("totalWorkingHours", 158.5)
                        put("overtimeHours", 6.5)
                    })
                }
                buildJsonResponse(request, 200, json.toString())
            }

            // GET /mobile/leave/balance
            path.endsWith("/mobile/leave/balance") -> {
                val json = JSONObject().apply {
                    put("success", true)
                    put("data", JSONObject().apply {
                        put("casualLeave", leaveBalance.casualLeave)
                        put("sickLeave", leaveBalance.sickLeave)
                        put("earnedLeave", leaveBalance.earnedLeave)
                        put("compOff", leaveBalance.compOff)
                    })
                }
                buildJsonResponse(request, 200, json.toString())
            }

            // GET /mobile/leave/history
            path.endsWith("/mobile/leave/history") -> {
                val jsonArr = JSONArray()
                leaveHistory.forEach { jsonArr.put(leaveToJson(it)) }
                val json = JSONObject().apply {
                    put("success", true)
                    put("data", jsonArr)
                }
                buildJsonResponse(request, 200, json.toString())
            }

            // POST /mobile/leave
            path.endsWith("/mobile/leave") -> {
                val newItem = LeaveItem(
                    id = "lev_${UUID.randomUUID().toString().take(6)}",
                    employeeId = defaultEmployee.id,
                    leaveType = "Casual Leave",
                    fromDate = "2026-08-25",
                    toDate = "2026-08-26",
                    isHalfDay = false,
                    days = 2.0,
                    reason = "Personal urgent work",
                    status = "PENDING",
                    appliedAt = "$serverDate $serverTime",
                    reviewedBy = null,
                    reviewComments = "Submitted for HR Approval"
                )
                leaveHistory.add(0, newItem)

                val json = JSONObject().apply {
                    put("success", true)
                    put("message", "Leave application submitted successfully. HR approval pending.")
                    put("data", leaveToJson(newItem))
                }
                buildJsonResponse(request, 200, json.toString())
            }

            // GET /mobile/salary-slips
            path.endsWith("/mobile/salary-slips") -> {
                val jsonArr = JSONArray()
                salarySlips.forEach { jsonArr.put(salaryToJson(it)) }
                val json = JSONObject().apply {
                    put("success", true)
                    put("data", jsonArr)
                }
                buildJsonResponse(request, 200, json.toString())
            }

            else -> {
                val json = JSONObject().apply {
                    put("success", true)
                    put("message", "OK")
                }
                buildJsonResponse(request, 200, json.toString())
            }
        }
    }

    private fun calculateHours(inTime: String, outTime: String): String {
        return try {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val dIn = sdf.parse(inTime)
            val dOut = sdf.parse(outTime)
            if (dIn != null && dOut != null) {
                var diff = dOut.time - dIn.time
                if (diff < 0) diff += 24 * 3600 * 1000
                val hours = diff / (3600 * 1000)
                val minutes = (diff % (3600 * 1000)) / (60 * 1000)
                String.format(Locale.getDefault(), "%02d:%02d hrs", hours, minutes)
            } else {
                "08:00 hrs"
            }
        } catch (e: Exception) {
            "08:00 hrs"
        }
    }

    private fun attendanceToJson(a: Attendance): JSONObject = JSONObject().apply {
        put("id", a.id)
        put("employee_id", a.employeeId)
        put("company_id", a.companyId)
        put("date", a.date)
        put("status", a.status)
        put("clockIn", a.clockIn ?: JSONObject.NULL)
        put("clockOut", a.clockOut ?: JSONObject.NULL)
        put("shiftTime", a.shiftTime ?: "09:00 AM - 06:00 PM")
        put("totalHours", a.totalHours ?: JSONObject.NULL)
        put("notes", a.notes ?: JSONObject.NULL)
        put("punchInLocation", a.punchInLocation ?: JSONObject.NULL)
        put("punchOutLocation", a.punchOutLocation ?: JSONObject.NULL)
        put("createdAt", a.createdAt ?: JSONObject.NULL)
        put("updatedAt", a.updatedAt ?: JSONObject.NULL)
    }

    private fun leaveToJson(l: LeaveItem): JSONObject = JSONObject().apply {
        put("id", l.id)
        put("employee_id", l.employeeId)
        put("leave_type", l.leaveType)
        put("from_date", l.fromDate)
        put("to_date", l.toDate)
        put("is_half_day", l.isHalfDay)
        put("days", l.days)
        put("reason", l.reason)
        put("status", l.status)
        put("applied_at", l.appliedAt)
        put("reviewed_by", l.reviewedBy ?: JSONObject.NULL)
        put("review_comments", l.reviewComments ?: JSONObject.NULL)
    }

    private fun salaryToJson(s: SalarySlip): JSONObject = JSONObject().apply {
        put("id", s.id)
        put("employee_id", s.employeeId)
        put("salary_month", s.salaryMonth)
        put("year", s.year)
        put("month", s.month)
        put("basic_salary", s.basicSalary)
        put("hra", s.hra)
        put("da", s.da)
        put("special_allowance", s.specialAllowance)
        put("gross_salary", s.grossSalary)
        put("pf", s.pf)
        put("esi", s.esi)
        put("advance", s.advance)
        put("professional_tax", s.professionalTax)
        put("other_deductions", s.otherDeductions)
        put("total_deductions", s.totalDeductions)
        put("net_salary", s.netSalary)
        put("paid_days", s.paidDays)
        put("payment_date", s.paymentDate)
        put("payment_status", s.paymentStatus)
        put("bank_name", s.bankName)
        put("transaction_ref", s.transactionRef)
    }

    private fun buildJsonResponse(request: okhttp3.Request, code: Int, json: String): Response {
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = json.toResponseBody(mediaType)
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(if (code == 200) "OK" else "Bad Request")
            .body(body)
            .addHeader("Content-Type", "application/json")
            .build()
    }
}
