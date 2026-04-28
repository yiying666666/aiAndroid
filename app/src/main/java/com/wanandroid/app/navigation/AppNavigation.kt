package com.wanandroid.app.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wanandroid.core.model.Article
import com.wanandroid.feature.article.ArticleDetailScreen
import com.wanandroid.feature.auth.LoginScreen
import com.wanandroid.feature.auth.RegisterScreen
import com.wanandroid.feature.home.HomeScreen
import com.wanandroid.feature.mine.MineScreen
import com.wanandroid.feature.navi.NaviScreen
import com.wanandroid.feature.project.ProjectScreen
import com.wanandroid.feature.search.SearchScreen
import com.wanandroid.feature.wechat.WechatScreen

sealed class AppRoute(val route: String) {
    object Login : AppRoute("auth/login")
    object Register : AppRoute("auth/register")
    object Main : AppRoute("main")
    object Search : AppRoute("search")
    object ArticleDetail : AppRoute("article?url={url}&title={title}") {
        fun createRoute(url: String, title: String) =
            "article?url=${Uri.encode(url)}&title=${Uri.encode(title)}"
    }
}

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    object Home : BottomNavItem("home", "首页", Icons.Outlined.Home)
    object Project : BottomNavItem("project", "项目", Icons.Outlined.Folder)
    object Wechat : BottomNavItem("wechat", "公众号", Icons.Outlined.Article)
    object Navi : BottomNavItem("navi", "导航", Icons.Outlined.Explore)
    object Mine : BottomNavItem("mine", "我的", Icons.Outlined.Person)

    companion object {
        val items = listOf(Home, Project, Wechat, Navi, Mine)
    }
}

@Composable
fun AppNavigation(
    isLoggedIn: Boolean,
    rootNavController: NavHostController = rememberNavController(),
) {
    val startDestination = if (isLoggedIn) AppRoute.Main.route else AppRoute.Login.route

    fun onArticleClick(article: Article) {
        if (article.link.isNotBlank()) {
            rootNavController.navigate(AppRoute.ArticleDetail.createRoute(article.link, article.title))
        }
    }

    NavHost(navController = rootNavController, startDestination = startDestination) {
        composable(AppRoute.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    rootNavController.navigate(AppRoute.Main.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { rootNavController.navigate(AppRoute.Register.route) },
            )
        }
        composable(AppRoute.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    rootNavController.navigate(AppRoute.Main.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                    }
                },
                onBack = { rootNavController.popBackStack() },
            )
        }
        composable(AppRoute.Main.route) {
            MainScaffold(
                onArticleClick = ::onArticleClick,
                onSearchClick = { rootNavController.navigate(AppRoute.Search.route) },
                onLogout = {
                    rootNavController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.Main.route) { inclusive = true }
                    }
                },
            )
        }
        composable(AppRoute.Search.route) {
            SearchScreen(
                onArticleClick = ::onArticleClick,
                onBack = { rootNavController.popBackStack() },
            )
        }
        composable(
            route = AppRoute.ArticleDetail.route,
            arguments = listOf(
                navArgument("url") { type = NavType.StringType; defaultValue = "" },
                navArgument("title") { type = NavType.StringType; defaultValue = "" },
            ),
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            ArticleDetailScreen(
                url = url,
                title = title,
                onBack = { rootNavController.popBackStack() },
            )
        }
    }
}

@Composable
private fun MainScaffold(
    onArticleClick: (Article) -> Unit,
    onSearchClick: () -> Unit,
    onLogout: () -> Unit,
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomNavItem.items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(onArticleClick = onArticleClick, onSearchClick = onSearchClick)
            }
            composable(BottomNavItem.Project.route) {
                ProjectScreen(onArticleClick = onArticleClick)
            }
            composable(BottomNavItem.Wechat.route) {
                WechatScreen(onArticleClick = onArticleClick)
            }
            composable(BottomNavItem.Navi.route) {
                NaviScreen(onArticleClick = onArticleClick)
            }
            composable(BottomNavItem.Mine.route) {
                MineScreen(onLogout = onLogout)
            }
        }
    }
}
