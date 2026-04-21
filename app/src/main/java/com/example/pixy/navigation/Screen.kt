package com.example.pixy.navigation

sealed class Screen(val route: String) {
    object RoleSelect : Screen("role_select")
    object LoginMethod : Screen("login_method")
    object Login : Screen("login")
    object Home : Screen("home")
    object Report : Screen("report")
    object UserIssues : Screen("user_issues")
    object MyIssues : Screen("my_issues")
    object Admin : Screen("admin")
    object Settings : Screen("settings")
    object Account : Screen("account")
    object HelpSupport : Screen("help_support")
    object AppFeedback : Screen("app_feedback")
    object Community : Screen("community")
    object ReportFake : Screen("report_fake/{issueId}") {
        fun createRoute(issueId: String) = "report_fake/$issueId"
    }
    object AdminStatusIssues : Screen("admin_status_issues/{status}") {
        fun createRoute(status: String) = "admin_status_issues/$status"
    }
    object IssueDetail : Screen("issue_detail/{issueId}") {
        fun createRoute(issueId: String) = "issue_detail/$issueId"
    }
}