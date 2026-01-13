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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                                onMataKuliahClick = { id -> navController.navigate("admin_mk_detail/$id") }                                // onLogout hapus dari parameter AdminHomeScreen karena sudah ada di dalam Profile
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

                        composable("pertemuan/{mkId}") { backStackEntry ->
                            val mkId = backStackEntry.arguments?.getString("mkId")?.toLongOrNull() ?: 0L
                            PertemuanListScreen(
                                mataKuliahId = mkId,
                                onBack = { navController.popBackStack() },
                                onPertemuanClick = { pertemuanId ->
                                    // PERBAIKAN: Gunakan "soal_list" sesuai dengan rute yang didefinisikan di bawah
                                    navController.navigate("soal_list/$pertemuanId")
                                }
                            )
                        }

                        // 7. HALAMAN SOAL
                        composable(
                            route = "soal_list/{pertemuanId}",
                            arguments = listOf(navArgument("pertemuanId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val pertemuanId = backStackEntry.arguments?.getLong("pertemuanId") ?: 0L

                            SoalListScreen(
                                pertemuanId = pertemuanId,
                                onBack = { navController.popBackStack() },
                                onNavigateHome = {
                                    navController.navigate("dosen_home") {
                                        popUpTo("dosen_home") { inclusive = true }
                                    }
                                },
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo(0)
                                    }
                                }
                            )
                        }

                        composable("profile") {
                            ProfileScreen(
                                onLogout = {
                                    navController.navigate("login") { popUpTo(0) }
                                },
                                onNavigateHome = {
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