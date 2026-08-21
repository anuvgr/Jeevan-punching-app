package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SessionManager
import com.example.data.model.Attendance
import com.example.data.model.SalarySlip
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Jeevan Companies Employee App", appName)
  }

  @Test
  fun `test salary calculations consistency`() {
    val basic = 32000.0
    val hra = 12800.0
    val da = 6400.0
    val special = 8800.0
    val gross = basic + hra + da + special
    assertEquals(60000.0, gross, 0.01)

    val pf = 3840.0
    val esi = 450.0
    val profTax = 200.0
    val totalDeductions = pf + esi + profTax
    assertEquals(4490.0, totalDeductions, 0.01)

    val netSalary = gross - totalDeductions
    assertEquals(55510.0, netSalary, 0.01)
  }

  @Test
  fun `test session manager operations`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val session = SessionManager(context)
    session.jwtToken = "test_token_123"
    assertTrue(session.isLoggedIn())
    assertEquals("test_token_123", session.jwtToken)

    session.clearSession()
    assertEquals(null, session.jwtToken)
  }
}
