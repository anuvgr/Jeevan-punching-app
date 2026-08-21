package com.example.ui.viewmodel

import android.app.Application
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Attendance
import com.example.data.model.Company
import com.example.data.model.CreateLeaveRequest
import com.example.data.model.Employee
import com.example.data.model.LeaveBalance
import com.example.data.model.LeaveItem
import com.example.data.model.MonthlySummary
import com.example.data.model.SalarySlip
import com.example.data.model.User
import com.example.data.remote.NetworkClient
import com.example.data.repository.JeevanRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DashboardUiState(
    val isLoading: Boolean = false,
    val isPunching: Boolean = false,
    val todayAttendance: Attendance? = null,
    val serverTimeDisplay: String = "--:--:--",
    val serverDateDisplay: String = "Today",
    val isPunchedIn: Boolean = false,
    val isPunchedOut: Boolean = false,
    val elapsedWorkingTime: String = "--:--",
    val locationStatusText: String = "HQ Geofence: Ready",
    val message: String? = null,
    val isErrorMessage: Boolean = false
)

data class AttendanceUiState(
    val isLoading: Boolean = false,
    val history: List<Attendance> = emptyList(),
    val monthlySummary: MonthlySummary? = null,
    val selectedMonth: String = "August",
    val selectedYear: Int = 2026,
    val statusFilter: String = "ALL",
    val errorMessage: String? = null
)

data class LeaveUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val balance: LeaveBalance = LeaveBalance(8.5, 6.0, 14.0, 2.0),
    val history: List<LeaveItem> = emptyList(),
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null
)

data class SalaryUiState(
    val isLoading: Boolean = false,
    val slips: List<SalarySlip> = emptyList(),
    val selectedSlip: SalarySlip? = null,
    val errorMessage: String? = null
)

data class ProfileUiState(
    val employee: Employee? = null,
    val user: User? = null,
    val company: Company? = null,
    val deviceId: String = "",
    val deviceModel: String = "",
    val osVersion: String = "",
    val appVersion: String = "1.0.0 (Build 100)",
    val serverUrl: String = "",
    val useMockMode: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = JeevanRepository(application)

    private val _isLoggedIn = MutableStateFlow(repository.sessionManager.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _dashboardState = MutableStateFlow(DashboardUiState())
    val dashboardState: StateFlow<DashboardUiState> = _dashboardState.asStateFlow()

    private val _attendanceState = MutableStateFlow(AttendanceUiState())
    val attendanceState: StateFlow<AttendanceUiState> = _attendanceState.asStateFlow()

    private val _leaveState = MutableStateFlow(LeaveUiState())
    val leaveState: StateFlow<LeaveUiState> = _leaveState.asStateFlow()

    private val _salaryState = MutableStateFlow(SalaryUiState())
    val salaryState: StateFlow<SalaryUiState> = _salaryState.asStateFlow()

    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    private var clockTickerJob: Job? = null

    init {
        initDeviceInfo()
        if (_isLoggedIn.value) {
            loadInitialData()
        } else {
            // Auto login with default staging credentials for preview convenience
            login("EMP1008", "jeevan@2026")
        }
        startClockTicker()
    }

    private fun initDeviceInfo() {
        val context = getApplication<Application>()
        val androidId = try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "DEVICE_9842"
        } catch (e: Exception) {
            "DEVICE_9842"
        }
        val model = "${Build.MANUFACTURER} ${Build.MODEL}"
        val os = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

        _profileState.update {
            it.copy(
                user = repository.sessionManager.getUser(),
                employee = repository.sessionManager.getEmployee(),
                company = repository.sessionManager.getCompany(),
                deviceId = androidId,
                deviceModel = model,
                osVersion = os,
                serverUrl = repository.sessionManager.serverUrl,
                useMockMode = repository.sessionManager.useMockBackend
            )
        }
    }

    fun login(username: String, pass: String) {
        viewModelScope.launch {
            _dashboardState.update { it.copy(isLoading = true, message = null, isErrorMessage = false) }
            val deviceId = _profileState.value.deviceId
            val result = repository.login(username, pass, deviceId)
            result.onSuccess {
                _isLoggedIn.value = true
                _profileState.update { s ->
                    s.copy(
                        user = repository.sessionManager.getUser(),
                        employee = repository.sessionManager.getEmployee(),
                        company = repository.sessionManager.getCompany()
                    )
                }
                loadInitialData()
                _dashboardState.update { it.copy(isLoading = false, message = "Welcome back, ${it.todayAttendance?.employeeId ?: "Employee"}", isErrorMessage = false) }
            }.onFailure { err ->
                _dashboardState.update { it.copy(isLoading = false, message = err.message ?: "Authentication failed", isErrorMessage = true) }
            }
        }
    }

    fun clearLoginError() {
        _dashboardState.update { it.copy(isErrorMessage = false, message = null) }
    }

    fun loginDemo(username: String = "EMP1008", pass: String = "jeevan@2026") {
        login(username, pass)
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _isLoggedIn.value = false
        }
    }

    fun loadInitialData() {
        refreshDashboardToday()
        loadAttendanceHistory()
        loadLeaveData()
        loadSalarySlips()
    }

    fun refreshDashboardToday() {
        viewModelScope.launch {
            _dashboardState.update { it.copy(isLoading = true) }
            val res = repository.getTodayAttendance()
            res.onSuccess { todayRes ->
                val hasPunchIn = todayRes.isPunchedIn || todayRes.attendance?.clockIn != null
                val hasPunchOut = todayRes.isPunchedOut || todayRes.attendance?.clockOut != null

                _dashboardState.update {
                    it.copy(
                        isLoading = false,
                        todayAttendance = todayRes.attendance,
                        isPunchedIn = hasPunchIn,
                        isPunchedOut = hasPunchOut,
                        serverDateDisplay = todayRes.serverDate
                    )
                }
                updateWorkingHoursDisplay(todayRes.attendance)
            }.onFailure { err ->
                _dashboardState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun punchIn(notes: String? = null) {
        viewModelScope.launch {
            _dashboardState.update { it.copy(isPunching = true, message = null) }
            delay(400) // Brief simulation of secure TLS handshake
            val devId = _profileState.value.deviceId
            val company = _profileState.value.company
            val lat = company?.officeLatitude ?: 9.9723
            val lng = company?.officeLongitude ?: 76.2784

            val result = repository.punchIn(devId, lat, lng, notes)
            result.onSuccess { res ->
                _dashboardState.update {
                    it.copy(
                        isPunching = false,
                        isPunchedIn = true,
                        isPunchedOut = false,
                        todayAttendance = res.attendance,
                        message = res.message,
                        isErrorMessage = false
                    )
                }
                updateWorkingHoursDisplay(res.attendance)
                loadAttendanceHistory()
            }.onFailure { err ->
                _dashboardState.update {
                    it.copy(
                        isPunching = false,
                        message = err.message ?: "Punch in failed",
                        isErrorMessage = true
                    )
                }
            }
        }
    }

    fun punchOut(notes: String? = null) {
        viewModelScope.launch {
            _dashboardState.update { it.copy(isPunching = true, message = null) }
            delay(400)
            val devId = _profileState.value.deviceId
            val company = _profileState.value.company
            val lat = company?.officeLatitude ?: 9.9723
            val lng = company?.officeLongitude ?: 76.2784

            val result = repository.punchOut(devId, lat, lng, notes)
            result.onSuccess { res ->
                _dashboardState.update {
                    it.copy(
                        isPunching = false,
                        isPunchedIn = true,
                        isPunchedOut = true,
                        todayAttendance = res.attendance,
                        message = res.message,
                        isErrorMessage = false
                    )
                }
                updateWorkingHoursDisplay(res.attendance)
                loadAttendanceHistory()
            }.onFailure { err ->
                _dashboardState.update {
                    it.copy(
                        isPunching = false,
                        message = err.message ?: "Punch out failed",
                        isErrorMessage = true
                    )
                }
            }
        }
    }

    private fun startClockTicker() {
        clockTickerJob?.cancel()
        clockTickerJob = viewModelScope.launch {
            val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
            val dateSdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
            while (isActive) {
                val now = Date()
                _dashboardState.update {
                    it.copy(
                        serverTimeDisplay = sdf.format(now),
                        serverDateDisplay = dateSdf.format(now)
                    )
                }
                delay(1000)
            }
        }
    }

    private fun updateWorkingHoursDisplay(att: Attendance?) {
        val workingTime = if (att != null) {
            if (!att.totalHours.isNullOrBlank() && att.totalHours != "00:00 hrs" && att.totalHours != "--:--") {
                att.totalHours
            } else {
                calculateWorkingTime(att.clockIn, att.clockOut)
            }
        } else {
            "--:--"
        }
        _dashboardState.update { it.copy(elapsedWorkingTime = workingTime) }
    }

    fun calculateWorkingTime(clockIn: String?, clockOut: String?): String {
        if (clockIn.isNullOrBlank()) return "--:--"
        val inMinutes = parseTimeToMinutes(clockIn) ?: return "--:--"

        val outMinutes = if (!clockOut.isNullOrBlank()) {
            parseTimeToMinutes(clockOut)
        } else {
            val nowCal = Calendar.getInstance()
            nowCal.get(Calendar.HOUR_OF_DAY) * 60 + nowCal.get(Calendar.MINUTE)
        } ?: return "--:--"

        var diff = outMinutes - inMinutes
        if (diff < 0) diff += 24 * 60
        val hrs = diff / 60
        val mins = diff % 60
        return String.format(Locale.getDefault(), "%02d:%02d hrs", hrs, mins)
    }

    private fun parseTimeToMinutes(timeStr: String): Int? {
        val formats = listOf(
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()),
            SimpleDateFormat("HH:mm", Locale.getDefault()),
            SimpleDateFormat("hh:mm:ss a", Locale.getDefault()),
            SimpleDateFormat("hh:mm a", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        )
        for (sdf in formats) {
            try {
                val date = sdf.parse(timeStr)
                if (date != null) {
                    val cal = Calendar.getInstance().apply { time = date }
                    return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
                }
            } catch (_: Exception) {}
        }
        try {
            val parts = timeStr.trim().split(":", " ")
            if (parts.size >= 2) {
                var hour = parts[0].toInt()
                val min = parts[1].toInt()
                val isPm = timeStr.contains("PM", ignoreCase = true)
                val isAm = timeStr.contains("AM", ignoreCase = true)
                if (isPm && hour < 12) hour += 12
                if (isAm && hour == 12) hour = 0
                return hour * 60 + min
            }
        } catch (_: Exception) {}
        return null
    }

    fun loadAttendanceHistory() {
        viewModelScope.launch {
            _attendanceState.update { it.copy(isLoading = true) }
            val emp = repository.sessionManager.getEmployee()
            val historyRes = repository.refreshAttendanceHistory()
            val summaryRes = repository.getMonthlySummary("August", 2026)

            _attendanceState.update {
                it.copy(
                    isLoading = false,
                    history = historyRes.getOrNull() ?: emptyList(),
                    monthlySummary = summaryRes.getOrNull()
                )
            }
        }
    }

    fun setAttendanceStatusFilter(status: String) {
        _attendanceState.update { it.copy(statusFilter = status) }
    }

    fun loadLeaveData() {
        viewModelScope.launch {
            _leaveState.update { it.copy(isLoading = true) }
            val balRes = repository.getLeaveBalance()
            val histRes = repository.refreshLeaveHistory()
            _leaveState.update {
                it.copy(
                    isLoading = false,
                    balance = balRes.getOrNull() ?: it.balance,
                    history = histRes.getOrNull() ?: emptyList()
                )
            }
        }
    }

    fun applyLeave(leaveType: String, fromDate: String, toDate: String, isHalfDay: Boolean, reason: String) {
        viewModelScope.launch {
            _leaveState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val req = CreateLeaveRequest(
                leaveType = leaveType,
                fromDate = fromDate,
                toDate = toDate,
                isHalfDay = isHalfDay,
                reason = reason
            )
            val result = repository.applyLeave(req)
            result.onSuccess {
                _leaveState.update { s -> s.copy(isSubmitting = false, submitSuccess = true) }
                loadLeaveData()
            }.onFailure { err ->
                _leaveState.update { s -> s.copy(isSubmitting = false, errorMessage = err.message) }
            }
        }
    }

    fun resetLeaveSubmitStatus() {
        _leaveState.update { it.copy(submitSuccess = false, errorMessage = null) }
    }

    fun loadSalarySlips() {
        viewModelScope.launch {
            _salaryState.update { it.copy(isLoading = true) }
            val res = repository.refreshSalarySlips()
            _salaryState.update {
                it.copy(
                    isLoading = false,
                    slips = res.getOrNull() ?: emptyList(),
                    selectedSlip = res.getOrNull()?.firstOrNull()
                )
            }
        }
    }

    fun selectSalarySlip(slip: SalarySlip) {
        _salaryState.update { it.copy(selectedSlip = slip) }
    }

    fun updateServerConfig(url: String, mockMode: Boolean) {
        repository.sessionManager.serverUrl = url
        repository.sessionManager.useMockBackend = mockMode
        NetworkClient.resetClient()
        _profileState.update { it.copy(serverUrl = url, useMockMode = mockMode) }
        loadInitialData()
    }

    fun clearMessage() {
        _dashboardState.update { it.copy(message = null) }
    }
}
