package com.example.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.JeevanTopHeader
import com.example.ui.components.LargePunchButton
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.JeevanEmeraldSuccess
import com.example.ui.theme.JeevanNavyPrimary
import com.example.ui.theme.JeevanRoseError
import com.example.ui.theme.JeevanTealAccent
import com.example.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToAttendance: () -> Unit,
    onNavigateToLeave: () -> Unit,
    onNavigateToSalary: () -> Unit
) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    val profileState by viewModel.profileState.collectAsState()
    val scrollState = rememberScrollState()

    var showPunchConfirmDialog by remember { mutableStateOf(false) }
    var isPunchInAction by remember { mutableStateOf(true) }
    var punchNotes by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(dashboardState.message) {
        dashboardState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp)
        ) {
            // Header
            JeevanTopHeader(
                employee = profileState.employee,
                companyName = profileState.company?.name ?: "Jeevan Companies",
                onNotificationClick = {}
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Live Digital Server Clock Card
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("server_clock_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(JeevanEmeraldSuccess)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "OFFICIAL SERVER TIME",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dashboardState.serverTimeDisplay,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = JeevanNavyPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = dashboardState.serverDateDisplay,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = JeevanTealAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "HQ Geofence: Active (200m)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "GPS VERIFIED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = JeevanEmeraldSuccess
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Primary Interactive Punch Button Section
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("punch_section_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val statusLabel = when {
                            dashboardState.isPunchedIn && dashboardState.isPunchedOut -> "PUNCHED OUT"
                            dashboardState.isPunchedIn -> "PUNCHED IN"
                            else -> "NOT PUNCHED IN"
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Today's Attendance",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            StatusBadge(status = statusLabel)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        LargePunchButton(
                            isPunchedIn = dashboardState.isPunchedIn,
                            isPunchedOut = dashboardState.isPunchedOut,
                            isLoading = dashboardState.isPunching,
                            onPunchInClick = {
                                isPunchInAction = true
                                punchNotes = ""
                                showPunchConfirmDialog = true
                            },
                            onPunchOutClick = {
                                isPunchInAction = false
                                punchNotes = ""
                                showPunchConfirmDialog = true
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Punch In / Punch Out Time Indicators
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Punch In",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = dashboardState.todayAttendance?.clockIn ?: "--:--",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (dashboardState.isPunchedIn) JeevanEmeraldSuccess else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Punch Out",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = dashboardState.todayAttendance?.clockOut ?: "--:--",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (dashboardState.isPunchedOut) JeevanRoseError else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Working Time",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = dashboardState.elapsedWorkingTime,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = JeevanNavyPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Quick Navigation Shortcuts
                Text(
                    text = "Quick Employee Actions",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Attendance History",
                        value = "History",
                        subtitle = "View Records",
                        icon = Icons.Default.CalendarMonth,
                        accentColor = JeevanNavyPrimary,
                        modifier = Modifier.weight(1f).testTag("quick_attendance_card"),
                        onClick = onNavigateToAttendance
                    )
                    StatCard(
                        title = "Leave Request",
                        value = "Leave",
                        subtitle = "Apply & Track",
                        icon = Icons.AutoMirrored.Filled.EventNote,
                        accentColor = JeevanTealAccent,
                        modifier = Modifier.weight(1f).testTag("quick_leave_card"),
                        onClick = onNavigateToLeave
                    )
                    StatCard(
                        title = "Salary Slips",
                        value = "Salary",
                        subtitle = "View & PDF",
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        accentColor = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f).testTag("quick_salary_card"),
                        onClick = onNavigateToSalary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Shift and Company Notice Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(JeevanTealAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = JeevanTealAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Assigned Shift: General Day Shift",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "09:00 AM – 06:00 PM (Grace period: 15 mins)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 85.dp)
        )
    }

    // Confirmation Dialog for Punch In / Out
    if (showPunchConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showPunchConfirmDialog = false },
            title = {
                Text(
                    text = if (isPunchInAction) "Confirm Punch In" else "Confirm Punch Out",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isPunchInAction)
                            "Are you ready to mark attendance for today? Server timestamp and GPS coordinates will be recorded."
                        else
                            "Are you sure you want to Punch Out? This will finalize your working hours for today.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = punchNotes,
                        onValueChange = { punchNotes = it },
                        label = { Text("Optional Notes (e.g. Client visit, Site punch)") },
                        modifier = Modifier.fillMaxWidth().testTag("punch_notes_input"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPunchConfirmDialog = false
                        if (isPunchInAction) {
                            viewModel.punchIn(punchNotes.ifBlank { null })
                        } else {
                            viewModel.punchOut(punchNotes.ifBlank { null })
                        }
                    },
                    modifier = Modifier.testTag("dialog_confirm_punch")
                ) {
                    Text(
                        text = if (isPunchInAction) "Punch In Now" else "Punch Out Now",
                        fontWeight = FontWeight.Bold,
                        color = if (isPunchInAction) JeevanEmeraldSuccess else JeevanRoseError
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showPunchConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
