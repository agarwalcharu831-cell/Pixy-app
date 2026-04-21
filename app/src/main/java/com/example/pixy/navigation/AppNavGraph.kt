package com.example.pixy.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pixy.model.IssueStatus
import com.example.pixy.screens.AccountScreen
import com.example.pixy.screens.AdminScreen
import com.example.pixy.screens.AdminStatusIssuesScreen
import com.example.pixy.screens.AppFeedbackScreen
import com.example.pixy.screens.CommunityScreen
import com.example.pixy.screens.HelpSupportScreen
import com.example.pixy.screens.HomeScreen
import com.example.pixy.screens.IssueDetailScreen
import com.example.pixy.screens.LoginMethodScreen
import com.example.pixy.screens.LoginScreen
import com.example.pixy.screens.MyIssuesScreen
import com.example.pixy.screens.ReportFakeIssueScreen
import com.example.pixy.screens.ReportIssueScreen
import com.example.pixy.screens.RoleSelectScreen
import com.example.pixy.screens.SettingsScreen
import com.example.pixy.screens.UserIssuesScreen
import com.example.pixy.viewmodel.IssueViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    issueViewModel: IssueViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.RoleSelect.route
    ) {
        composable(Screen.RoleSelect.route) {
            RoleSelectScreen(
                onRoleSelected = { role ->
                    issueViewModel.setRole(role)
                    navController.navigate(Screen.LoginMethod.route)
                }
            )
        }

        composable(Screen.LoginMethod.route) {
            LoginMethodScreen(
                onManual = { navController.navigate(Screen.Login.route) },
                onGoogle = {
                    issueViewModel.loginWithGoogle()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.RoleSelect.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { name, email ->
                    issueViewModel.loginManual(name, email)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.RoleSelect.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                issueViewModel = issueViewModel,
                onReportClick = { navController.navigate(Screen.Report.route) },
                onIssueClick = { issueId ->
                    navController.navigate(Screen.IssueDetail.createRoute(issueId))
                },
                onDashboardClick = { navController.navigate(Screen.Admin.route) },
                onViewAllIssues = { navController.navigate(Screen.UserIssues.route) },
                onMyIssuesClick = { navController.navigate(Screen.MyIssues.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onCommunityClick = { navController.navigate(Screen.Community.route) }
            )
        }

        composable(Screen.Report.route) {
            ReportIssueScreen(
                issueViewModel = issueViewModel,
                onSubmitted = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.UserIssues.route) {
            UserIssuesScreen(
                issueViewModel = issueViewModel,
                onIssueClick = { navController.navigate(Screen.IssueDetail.createRoute(it)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MyIssues.route) {
            MyIssuesScreen(
                issueViewModel = issueViewModel,
                onBack = { navController.popBackStack() },
                onIssueClick = { navController.navigate(Screen.IssueDetail.createRoute(it)) }
            )
        }

        composable(Screen.Community.route) {
            CommunityScreen(
                issueViewModel = issueViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.IssueDetail.route,
            arguments = listOf(navArgument("issueId") { type = NavType.StringType })
        ) { backStack ->
            val issueId = backStack.arguments?.getString("issueId").orEmpty()
            IssueDetailScreen(
                issueId = issueId,
                issueViewModel = issueViewModel,
                onBack = { navController.popBackStack() },
                onReportFake = { navController.navigate(Screen.ReportFake.createRoute(issueId)) }
            )
        }

        composable(
            route = Screen.ReportFake.route,
            arguments = listOf(navArgument("issueId") { type = NavType.StringType })
        ) { backStack ->
            val issueId = backStack.arguments?.getString("issueId").orEmpty()
            ReportFakeIssueScreen(
                issueId = issueId,
                issueViewModel = issueViewModel,
                onBack = { navController.popBackStack() },
                onSubmitted = { navController.popBackStack() }
            )
        }

        composable(Screen.Admin.route) {
            AdminScreen(
                issueViewModel = issueViewModel,
                onBack = { navController.popBackStack() },
                onIssueClick = { navController.navigate(Screen.IssueDetail.createRoute(it)) },
                onStatusClick = { status ->
                    navController.navigate(Screen.AdminStatusIssues.createRoute(status.name))
                }
            )
        }

        composable(
            route = Screen.AdminStatusIssues.route,
            arguments = listOf(navArgument("status") { type = NavType.StringType })
        ) { backStack ->
            val status = IssueStatus.valueOf(
                backStack.arguments?.getString("status") ?: IssueStatus.OPEN.name
            )
            AdminStatusIssuesScreen(
                issueViewModel = issueViewModel,
                status = status,
                onBack = { navController.popBackStack() },
                onIssueClick = { navController.navigate(Screen.IssueDetail.createRoute(it)) }
            )
        }

        composable(
            route = Screen.Settings.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            SettingsScreen(
                issueViewModel = issueViewModel,
                onBack = { navController.popBackStack() },
                onLogout = {
                    issueViewModel.logout()
                    navController.navigate(Screen.RoleSelect.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onOpenAccount = { navController.navigate(Screen.Account.route) },
                onOpenHelp = { navController.navigate(Screen.HelpSupport.route) },
                onOpenFeedback = { navController.navigate(Screen.AppFeedback.route) }
            )
        }

        composable(
            route = Screen.Account.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            AccountScreen(
                issueViewModel = issueViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.HelpSupport.route) {
            HelpSupportScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.AppFeedback.route) {
            AppFeedbackScreen(onBack = { navController.popBackStack() })
        }
    }
}