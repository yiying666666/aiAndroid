package com.wanandroid.app

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0A0E1A),
        Color(0xFF0D1B2A),
        Color(0xFF1A2E4A),
    )
)
private val LogoGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF4A90E2), Color(0xFF2B6CB0))
)
private val AccentBlue = Color(0xFF4A90E2)

@Composable
fun SplashScreen(
    isLoggedIn: Boolean?,
    onSplashComplete: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var navigated by remember { mutableStateOf(false) }

    fun navigate() {
        if (navigated) return
        navigated = true
        scope.launch {
            val loggedIn = snapshotFlow { isLoggedIn }.filterNotNull().first()
            onSplashComplete(loggedIn)
        }
    }

    // 入场动画状态
    var visible by remember { mutableStateOf(false) }
    var showSkip by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "logoScale",
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(700),
        label = "contentAlpha",
    )
    val textOffsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 30f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "textOffsetY",
    )
    val skipAlpha by animateFloatAsState(
        targetValue = if (showSkip) 1f else 0f,
        animationSpec = tween(500),
        label = "skipAlpha",
    )

    // 脉冲光晕
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.50f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha",
    )
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1.00f,
        targetValue = 1.18f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowScale",
    )

    // 倒计时进度
    val progress = remember { Animatable(0f) }
    val countdown = (3 - (progress.value * 3).toInt()).coerceAtLeast(1)

    LaunchedEffect(Unit) {
        visible = true
        launch { progress.animateTo(1f, animationSpec = tween(3000, easing = LinearEasing)) }
        delay(1000)
        showSkip = true
        delay(2000)
        navigate()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient),
        contentAlignment = Alignment.Center,
    ) {
        // 跳过按钮（1秒后淡入）
        Text(
            text = "跳过",
            color = Color.White.copy(alpha = 0.55f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 56.dp, end = 24.dp)
                .alpha(skipAlpha)
                .clickable(enabled = showSkip) { navigate() },
        )

        // 主体
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(contentAlignment = Alignment.Center) {
                // 脉冲光晕
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(glowScale * logoScale)
                        .clip(CircleShape)
                        .background(AccentBlue.copy(alpha = glowAlpha * logoScale)),
                )
                // 图标容器
                Box(
                    modifier = Modifier
                        .scale(logoScale)
                        .size(110.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(LogoGradient),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Android,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(62.dp),
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            Text(
                text = "玩 Android",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .alpha(contentAlpha)
                    .offset(y = textOffsetY.dp),
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "发现精彩 Android 世界",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.60f),
                modifier = Modifier
                    .alpha(contentAlpha)
                    .offset(y = textOffsetY.dp),
            )
        }

        // 底部倒计时
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier.size(56.dp),
                    color = AccentBlue,
                    trackColor = Color.White.copy(alpha = 0.15f),
                    strokeWidth = 3.dp,
                )
                Text(
                    text = "$countdown",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
            Text(
                text = "即将进入...",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.40f),
            )
        }
    }
}
