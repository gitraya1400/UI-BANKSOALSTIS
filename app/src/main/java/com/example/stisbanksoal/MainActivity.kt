package com.example.stisbanksoal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stisbanksoal.data.local.UserPreferences
import com.example.stisbanksoal.ui.screens.admin.AdminDetailScreen // Pastikan ini di-import
import com.example.stisbanksoal.ui.screens.admin.AdminHomeScreen
import com.example.stisbanksoal.ui.screens.auth.LoginScreen
import com.example.stisbanksoal.ui.screens.auth.RegisterScreen
import com.example.stisbanksoal.ui.screens.common.ProfileScreen
import com.example.stisbanksoal.ui.screens.dosen.DosenHomeScreen
import com.example.stisbanksoal.ui.screens.dosen.PertemuanListScreen
import com.example.stisbanksoal.ui.screens.dosen.SoalListScreen
import com.example.stisbanksoal.ui.theme.StisbanksoalTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StisbanksoalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val userPreferences = UserPreferences(context)
                    val scope = rememberCoroutineScope()

                    NavHost(navController = navController, startDestination = "login") {

                        // 1. HALAMAN LOGIN
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = { role ->
                                    if (role == "ADMIN") {
                                        navController.navigate("admin_home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate("dosen_home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        // 2. HALAMAN REGISTER
                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.popBackStack()
                                },
                                onBackToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 3. HALAMAN ADMIN HOME
                        composable("admin_home") {
                            AdminHomeScreen(
                                onNavigateToProfile = { navController.navigate("profile") }, // Hubungkan ke sini
                                onMataKuliahClick = { id -> navController.navigate("admin_detail/$id") },
                                // onLogout hapus dari parameter AdminHomeScreen karena sudah ada di dalam Profile
                            )
                        }

                        // 4. HALAMAN ADMIN DETAIL MK
                        composable("admin_mk_detail/{mkId}") { backStackEntry ->
                            val mkId = backStackEntry.arguments?.getString("mkId")?.toLongOrNull() ?: 0L
                            AdminDetailScreen(
                                mkId = mkId,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // 5. HALAMAN DOSEN HOME
                        composable("dosen_home") {
                            DosenHomeScreen(
                                onLogout = {
                                    scope.launch {
                                        userPreferences.clearSession()
                                        navController.navigate("login") { popUpTo(0) }
                                    }
                                },
                                onMataKuliahClick = { mkId ->
                                    navController.navigate("pertemuan/$mkId")
                                }
                            )
                        }

                        // 6. HALAMAN LIST PERTEMUAN (Update action klik-nya)
                        composable("pertemuan/{mkId}") { backStackEntry ->
                            val mkId = backStackEntry.arguments?.getString("mkId")?.toLongOrNull() ?: 0L
                            PertemuanListScreen(
                                mataKuliahId = mkId,
                                onBack = { navController.popBackStack() },
                                onPertemuanClick = { pertemuanId ->
                                    // ARUH KE HALAMAN SOAL
                                    navController.navigate("soal/$pertemuanId")
                                }
                            )
                        }

                        // 7. HALAMAN SOAL (BARU)
                        composable("soal/{pertemuanId}") { backStackEntry ->
                            val pId = backStackEntry.arguments?.getString("pertemuanId")?.toLongOrNull() ?: 0L
                            // Pastikan import SoalListScreen sudah ada
                            SoalListScreen(
                                pertemuanId = pId,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("profile") {
                            ProfileScreen(
                                onLogout = {
                                    navController.navigate("login") { popUpTo(0) }
                                },
                                onNavigateHome = {
                                    // Cek role user, jika admin ke admin_home, jika dosen ke dosen_home
                                    // Untuk sementara kita asumsikan Admin dulu atau navigasi mundur
                                    navController.popBackStack()
                                }
                            )
                        }

                    } // <--- Tutup NavHost HARUS DI SINI (Paling Bawah)
                }
            }
        }
    }
}