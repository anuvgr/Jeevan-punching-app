package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_cache WHERE employeeId = :employeeId ORDER BY date DESC")
    fun getAttendanceHistory(employeeId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance_cache WHERE employeeId = :employeeId AND date = :date LIMIT 1")
    suspend fun getTodayAttendance(employeeId: String, date: String): AttendanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(list: List<AttendanceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Query("DELETE FROM attendance_cache WHERE employeeId = :employeeId")
    suspend fun clearAttendance(employeeId: String)
}

@Dao
interface LeaveDao {
    @Query("SELECT * FROM leave_cache WHERE employeeId = :employeeId ORDER BY fromDate DESC")
    fun getLeaveHistory(employeeId: String): Flow<List<LeaveEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaveList(list: List<LeaveEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeave(leave: LeaveEntity)

    @Query("DELETE FROM leave_cache WHERE employeeId = :employeeId")
    suspend fun clearLeaves(employeeId: String)
}

@Dao
interface SalarySlipDao {
    @Query("SELECT * FROM salary_cache WHERE employeeId = :employeeId ORDER BY year DESC, month DESC")
    fun getSalarySlips(employeeId: String): Flow<List<SalarySlipEntity>>

    @Query("SELECT * FROM salary_cache WHERE id = :id LIMIT 1")
    suspend fun getSalarySlipById(id: String): SalarySlipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalarySlips(list: List<SalarySlipEntity>)

    @Query("DELETE FROM salary_cache WHERE employeeId = :employeeId")
    suspend fun clearSalaries(employeeId: String)
}
