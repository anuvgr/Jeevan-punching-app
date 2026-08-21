package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Attendance
import com.example.data.model.LeaveItem
import com.example.data.model.SalarySlip

@Entity(tableName = "attendance_cache")
data class AttendanceEntity(
    @PrimaryKey val id: String,
    val employeeId: String,
    val companyId: String,
    val date: String,
    val status: String,
    val clockIn: String?,
    val clockOut: String?,
    val shiftTime: String?,
    val totalHours: String?,
    val notes: String?,
    val punchInLocation: String?,
    val punchOutLocation: String?,
    val createdAt: String?,
    val updatedAt: String?
) {
    fun toModel() = Attendance(
        id = id,
        employeeId = employeeId,
        companyId = companyId,
        date = date,
        status = status,
        clockIn = clockIn,
        clockOut = clockOut,
        shiftTime = shiftTime,
        totalHours = totalHours,
        notes = notes,
        punchInLocation = punchInLocation,
        punchOutLocation = punchOutLocation,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromModel(m: Attendance) = AttendanceEntity(
            id = m.id,
            employeeId = m.employeeId,
            companyId = m.companyId,
            date = m.date,
            status = m.status,
            clockIn = m.clockIn,
            clockOut = m.clockOut,
            shiftTime = m.shiftTime,
            totalHours = m.totalHours,
            notes = m.notes,
            punchInLocation = m.punchInLocation,
            punchOutLocation = m.punchOutLocation,
            createdAt = m.createdAt,
            updatedAt = m.updatedAt
        )
    }
}

@Entity(tableName = "leave_cache")
data class LeaveEntity(
    @PrimaryKey val id: String,
    val employeeId: String,
    val leaveType: String,
    val fromDate: String,
    val toDate: String,
    val isHalfDay: Boolean,
    val days: Double,
    val reason: String,
    val status: String,
    val appliedAt: String,
    val reviewedBy: String?,
    val reviewComments: String?
) {
    fun toModel() = LeaveItem(
        id = id,
        employeeId = employeeId,
        leaveType = leaveType,
        fromDate = fromDate,
        toDate = toDate,
        isHalfDay = isHalfDay,
        days = days,
        reason = reason,
        status = status,
        appliedAt = appliedAt,
        reviewedBy = reviewedBy,
        reviewComments = reviewComments
    )

    companion object {
        fun fromModel(m: LeaveItem) = LeaveEntity(
            id = m.id,
            employeeId = m.employeeId,
            leaveType = m.leaveType,
            fromDate = m.fromDate,
            toDate = m.toDate,
            isHalfDay = m.isHalfDay,
            days = m.days,
            reason = m.reason,
            status = m.status,
            appliedAt = m.appliedAt,
            reviewedBy = m.reviewedBy,
            reviewComments = m.reviewComments
        )
    }
}

@Entity(tableName = "salary_cache")
data class SalarySlipEntity(
    @PrimaryKey val id: String,
    val employeeId: String,
    val salaryMonth: String,
    val year: Int,
    val month: Int,
    val basicSalary: Double,
    val hra: Double,
    val da: Double,
    val specialAllowance: Double,
    val grossSalary: Double,
    val pf: Double,
    val esi: Double,
    val advance: Double,
    val professionalTax: Double,
    val otherDeductions: Double,
    val totalDeductions: Double,
    val netSalary: Double,
    val paidDays: Int,
    val paymentDate: String,
    val paymentStatus: String,
    val bankName: String,
    val transactionRef: String
) {
    fun toModel() = SalarySlip(
        id = id,
        employeeId = employeeId,
        salaryMonth = salaryMonth,
        year = year,
        month = month,
        basicSalary = basicSalary,
        hra = hra,
        da = da,
        specialAllowance = specialAllowance,
        grossSalary = grossSalary,
        pf = pf,
        esi = esi,
        advance = advance,
        professionalTax = professionalTax,
        otherDeductions = otherDeductions,
        totalDeductions = totalDeductions,
        netSalary = netSalary,
        paidDays = paidDays,
        paymentDate = paymentDate,
        paymentStatus = paymentStatus,
        bankName = bankName,
        transactionRef = transactionRef
    )

    companion object {
        fun fromModel(m: SalarySlip) = SalarySlipEntity(
            id = m.id,
            employeeId = m.employeeId,
            salaryMonth = m.salaryMonth,
            year = m.year,
            month = m.month,
            basicSalary = m.basicSalary,
            hra = m.hra,
            da = m.da,
            specialAllowance = m.specialAllowance,
            grossSalary = m.grossSalary,
            pf = m.pf,
            esi = m.esi,
            advance = m.advance,
            professionalTax = m.professionalTax,
            otherDeductions = m.otherDeductions,
            totalDeductions = m.totalDeductions,
            netSalary = m.netSalary,
            paidDays = m.paidDays,
            paymentDate = m.paymentDate,
            paymentStatus = m.paymentStatus,
            bankName = m.bankName,
            transactionRef = m.transactionRef
        )
    }
}
