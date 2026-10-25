package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.BrandTeal

data class AvatarInfo(
    val id: String,
    val name: String,
    val quote: String,
    val drawableResId: Int
)

val AVATAR_LIST: List<AvatarInfo> = listOf(
    AvatarInfo(
        id = "avatar_1",
        name = "Rafi Rahman",
        quote = "Keep going, future is bright!",
        drawableResId = R.drawable.avatar_boy_glasses_1792843533499
    ),
    AvatarInfo(
        id = "avatar_2",
        name = "Nabila Islam",
        quote = "Small steps every single day.",
        drawableResId = R.drawable.avatar_girl_hijab_1792843566359
    ),
    AvatarInfo(
        id = "avatar_3",
        name = "Tanvir Ahmed",
        quote = "Master one chapter at a time.",
        drawableResId = R.drawable.avatar_boy_curly_1792843589854
    ),
    AvatarInfo(
        id = "avatar_4",
        name = "Sumaiya Akter",
        quote = "Focus on the goal, not the obstacle.",
        drawableResId = R.drawable.avatar_girl_ponytail_1792843610978
    ),
    AvatarInfo(
        id = "avatar_5",
        name = "Samiul Karim",
        quote = "Consistency beats intensity.",
        drawableResId = R.drawable.avatar_boy_cap_1792843633860
    ),
    AvatarInfo(
        id = "avatar_6",
        name = "Farhana Yasmin",
        quote = "Knowledge is the ultimate superpower.",
        drawableResId = R.drawable.avatar_girl_glasses_1792843655175
    ),
    AvatarInfo(
        id = "avatar_7",
        name = "Ayman Sadek",
        quote = "Strive for excellence every day.",
        drawableResId = R.drawable.avatar_boy_hoodie_1792846437539
    ),
    AvatarInfo(
        id = "avatar_8",
        name = "Anika Tabassum",
        quote = "Dream big, work hard, stay focused.",
        drawableResId = R.drawable.avatar_girl_bob_1792846453306
    ),
    AvatarInfo(
        id = "avatar_9",
        name = "Saad Hasan",
        quote = "Music in ears, focus in mind.",
        drawableResId = R.drawable.avatar_boy_headphones_1792846468284
    ),
    AvatarInfo(
        id = "avatar_10",
        name = "Nusrat Jahan",
        quote = "Believe you can and you're halfway there.",
        drawableResId = R.drawable.avatar_girl_braid_1792846481718
    ),
    AvatarInfo(
        id = "avatar_11",
        name = "Zayan Kabir",
        quote = "Learn with passion and purpose.",
        drawableResId = R.drawable.avatar_boy_smile_1792846495225
    ),
    AvatarInfo(
        id = "avatar_12",
        name = "Mehreen Chowdhury",
        quote = "Success is built on daily habits.",
        drawableResId = R.drawable.avatar_girl_scarf_1792846507998
    )
)

fun getAvatarInfo(avatarId: String): AvatarInfo {
    return AVATAR_LIST.find { it.id == avatarId } ?: AVATAR_LIST.first()
}

@Composable
fun StudentAvatar(
    avatarId: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val info = getAvatarInfo(avatarId)

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (isSelected) {
                    Modifier.border(2.5.dp, BrandTeal, CircleShape)
                } else Modifier
            )
            .padding(if (isSelected) 2.5.dp else 0.dp)
            .clip(CircleShape)
            .then(
                if (onClick != null) {
                    Modifier
                        .clickable(onClick = onClick)
                        .semantics { contentDescription = "Open profile" }
                } else {
                    Modifier.semantics { contentDescription = "Avatar ${info.name}" }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = info.drawableResId),
            contentDescription = info.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Selected check badge
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(size * 0.32f)
                    .background(BrandTeal, CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.22f)
                )
            }
        }
    }
}
