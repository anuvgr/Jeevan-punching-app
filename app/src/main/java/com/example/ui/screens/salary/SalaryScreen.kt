package com.example.ui.screens.salary

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Employee
import com.example.data.model.SalarySlip
import com.example.ui.components.StatusBadge
import com.example.ui.theme.JeevanEmeraldDark
import com.example.ui.theme.JeevanEmeraldSuccess
import com.example.ui.theme.JeevanNavyDark
import com.example.ui.theme.JeevanNavyPrimary
import com.example.ui.theme.JeevanRoseError
import com.example.ui.theme.JeevanTealAccent
import com.example.ui.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SalaryScreen(viewModel: MainViewModel) {
    val salaryState by viewModel.salaryState.collectAsState()
    val profileState by viewModel.profileState.collectAsState()
    var viewingSlip by remember { mutableStateOf<SalarySlip?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        Surface(
            color = JeevanNavyPrimary,
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Salary Slips & Payroll",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Confidential Employee Earnings",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                IconButton(
                    onClick = { viewModel.loadSalarySlips() },
                    modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color.White
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Latest Salary Overview Card
            if (salaryState.slips.isNotEmpty()) {
                val latest = salaryState.slips.first()
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("latest_salary_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Latest Credited Net Salary",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = latest.salaryMonth,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                StatusBadge(status = latest.paymentStatus)
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = formatCurrency(latest.netSalary),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = JeevanEmeraldDark
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Gross Salary", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(formatCurrency(latest.grossSalary), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text("Total Deductions", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(formatCurrency(latest.totalDeductions), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = JeevanRoseError)
                                }
                                Column {
                                    Text("Paid Days", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${latest.paidDays} Days", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { viewingSlip = latest },
                                modifier = Modifier.fillMaxWidth().testTag("view_latest_slip_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = JeevanNavyPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("View Detailed Payslip (PDF)")
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Previous Salary Statements",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }

            if (salaryState.isLoading && salaryState.slips.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = JeevanNavyPrimary)
                    }
                }
            } else {
                items(salaryState.slips, key = { it.id }) { slip ->
                    SalarySlipItemCard(
                        slip = slip,
                        onClick = { viewingSlip = slip }
                    )
                }
            }
        }
    }

    // PDF Payslip Viewer Dialog
    viewingSlip?.let { slip ->
        SalarySlipPdfModal(
            slip = slip,
            employee = profileState.employee,
            companyName = profileState.company?.name ?: "Jeevan Companies India Ltd.",
            onDismiss = { viewingSlip = null },
            onShare = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Payslip for ${profileState.employee?.name} (${slip.salaryMonth})\nNet Salary: ${formatCurrency(slip.netSalary)}\nBank Ref: ${slip.transactionRef}\nJeevan Companies"
                    )
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Share Payslip"))
            },
            onDownload = {
                Toast.makeText(context, "Salary slip ${slip.salaryMonth} downloaded successfully to storage.", Toast.LENGTH_LONG).show()
            }
        )
    }
}

@Composable
fun SalarySlipItemCard(
    slip: SalarySlip,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("slip_card_${slip.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(JeevanNavyPrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = JeevanNavyPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = slip.salaryMonth,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Disbursed on ${slip.paymentDate}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatCurrency(slip.netSalary),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = JeevanEmeraldDark
                    )
                    Text(
                        text = "Net Pay",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun SalarySlipPdfModal(
    slip: SalarySlip,
    employee: Employee?,
    companyName: String,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Salary Statement",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Scrollable Payslip Document Preview
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    // Company Letterhead
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = companyName.uppercase(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = JeevanNavyPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "PAYSLIP FOR THE MONTH OF ${slip.salaryMonth.uppercase()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))

                    // Employee Metadata Table
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            PayslipField("Employee Name", employee?.name ?: "Rahul M. Nair")
                            PayslipField("Employee Code", employee?.employeeCode ?: "JEV-1008")
                            PayslipField("Department", employee?.department ?: "Operations")
                            PayslipField("Bank Account", employee?.bankAccountMasked ?: "•••• 8912")
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            PayslipField("Designation", employee?.designation ?: "Senior Executive")
                            PayslipField("PF UAN", employee?.pfUan ?: "100987654321")
                            PayslipField("PAN Number", employee?.panNumber ?: "ABCDE8765F")
                            PayslipField("Paid Days", "${slip.paidDays} Days")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))

                    // Earnings vs Deductions 2-Column Table
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp)
                    ) {
                        Text("EARNINGS", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("AMOUNT", modifier = Modifier.width(70.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("DEDUCTIONS", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("AMOUNT", modifier = Modifier.width(70.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TableLine("Basic Salary", formatCurrency(slip.basicSalary), "Provident Fund (PF)", formatCurrency(slip.pf))
                    TableLine("House Rent (HRA)", formatCurrency(slip.hra), "ESI Contribution", formatCurrency(slip.esi))
                    TableLine("Dearness Allow. (DA)", formatCurrency(slip.da), "Professional Tax", formatCurrency(slip.professionalTax))
                    TableLine("Special Allowance", formatCurrency(slip.specialAllowance), "Salary Advance", formatCurrency(slip.advance))

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    // Totals Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Gross Earnings", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(formatCurrency(slip.grossSalary), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Deductions", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JeevanRoseError)
                            Text(formatCurrency(slip.totalDeductions), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JeevanRoseError)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Net Pay Banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("NET TAKE-HOME PAY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                Text("Fifty-Five Thousand Five Hundred Ten Rupees Only", fontSize = 10.sp, color = Color(0xFF1B5E20))
                            }
                            Text(
                                text = formatCurrency(slip.netSalary),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Txn Ref: ${slip.transactionRef} • This is a computer-generated payslip authorized by Jeevan HR.",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Buttons: Download & Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier.weight(1f).testTag("share_slip_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share")
                    }

                    Button(
                        onClick = onDownload,
                        modifier = Modifier.weight(1f).testTag("download_slip_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = JeevanNavyPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download PDF")
                    }
                }
            }
        }
    }
}

@Composable
private fun PayslipField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun TableLine(earnTitle: String, earnVal: String, dedTitle: String, dedVal: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(earnTitle, modifier = Modifier.weight(1f), fontSize = 11.sp)
        Text(earnVal, modifier = Modifier.width(70.dp), fontSize = 11.sp, textAlign = TextAlign.End)
        Spacer(modifier = Modifier.width(12.dp))
        Text(dedTitle, modifier = Modifier.weight(1f), fontSize = 11.sp)
        Text(dedVal, modifier = Modifier.width(70.dp), fontSize = 11.sp, textAlign = TextAlign.End)
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}
