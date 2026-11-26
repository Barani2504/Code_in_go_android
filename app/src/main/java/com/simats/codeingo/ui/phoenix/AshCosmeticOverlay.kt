package com.simats.codeingo.ui.phoenix

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.simats.codeingo.R

/**
 * Maps any Gem Shop cosmetic or power-up ID to its custom crisp vector drawable.
 */
object AshCosmeticAssets {
    fun drawableForId(id: String): Int = when (id) {
        // Headwear
        "none_head" -> R.drawable.cosmetic_none
        "hat_dev" -> R.drawable.cosmetic_hat_dev
        "cap_code" -> R.drawable.cosmetic_cap_code
        "crown_gold", "crown_tree" -> R.drawable.cosmetic_crown_gold
        "cap_grad" -> R.drawable.cosmetic_cap_grad
        "hat_wizard" -> R.drawable.cosmetic_hat_wizard
        "hat_party" -> R.drawable.cosmetic_hat_party
        "hat_cipher_helm" -> R.drawable.cosmetic_cyber_visor
        "hat_bubble" -> R.drawable.cosmetic_laurels

        // Outfits
        "none_outfit" -> R.drawable.cosmetic_none
        "outfit_hoodie", "outfit_wings" -> R.drawable.cosmetic_outfit_hoodie
        "outfit_cape" -> R.drawable.cosmetic_outfit_cape
        "outfit_vest" -> R.drawable.cosmetic_outfit_vest
        "outfit_belt" -> R.drawable.cosmetic_outfit_belt

        // Accessories
        "none_acc" -> R.drawable.cosmetic_none
        "glasses_nerd" -> R.drawable.cosmetic_glasses_nerd
        "shades_cool" -> R.drawable.cosmetic_shades_cool
        "scarf_red" -> R.drawable.cosmetic_scarf_red
        "bowtie_ruby" -> R.drawable.cosmetic_bowtie_ruby
        "acc_pack" -> R.drawable.cosmetic_shield_wings
        "acc_train" -> R.drawable.cosmetic_aura_quantum

        // Effects / Auras
        "none_aura" -> R.drawable.cosmetic_none
        "aura_ember" -> R.drawable.cosmetic_aura_ember
        "aura_sparkle" -> R.drawable.cosmetic_aura_sparkle
        "aura_lightning" -> R.drawable.cosmetic_aura_lightning
        "aura_rainbow", "aura_quantum" -> R.drawable.cosmetic_aura_quantum
        "aura_frost" -> R.drawable.cosmetic_aura_frost

        // Hearts & Power-Ups
        "heart_pack_full", "heart_pack_5", "heart_pack_1" -> R.drawable.cosmetic_heart_full
        "power_streak_freeze" -> R.drawable.cosmetic_streak_freeze
        "power_xp_boost" -> R.drawable.cosmetic_xp_boost

        else -> R.drawable.cosmetic_none
    }
}

/**
 * 🦅 AshCosmeticOverlay — Renders equipped/previewed Gem Shop cosmetics
 * anatomically positioned and scaled onto Ash's body (head, eyes, neck/chest).
 */
@Composable
fun AshCosmeticOverlay(
    headwearId: String,
    outfitId: String,
    accessoryId: String,
    mascotWidth: Dp,
    mascotHeight: Dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(mascotWidth, mascotHeight)) {
        // 1. Outfit layer (chest / shoulders)
        if (outfitId != "none_outfit") {
            val outfitSizeW = mascotWidth * 0.44f
            val outfitSizeH = mascotHeight * 0.34f
            val yOffset = if (outfitId == "outfit_cape") mascotHeight * 0.24f else mascotHeight * 0.22f
            Image(
                painter = painterResource(id = AshCosmeticAssets.drawableForId(outfitId)),
                contentDescription = "Equipped Outfit",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(outfitSizeW, outfitSizeH)
                    .align(Alignment.Center)
                    .offset(y = yOffset)
            )
        }

        // 2. Accessory layer (eyes for glasses/shades, neck for scarf/bowtie, shoulder for shield)
        if (accessoryId != "none_acc") {
            when (accessoryId) {
                "glasses_nerd", "shades_cool" -> {
                    Image(
                        painter = painterResource(id = AshCosmeticAssets.drawableForId(accessoryId)),
                        contentDescription = "Equipped Eyewear",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(mascotWidth * 0.36f, mascotHeight * 0.16f)
                            .align(Alignment.TopCenter)
                            .offset(y = mascotHeight * 0.24f)
                    )
                }
                "scarf_red", "bowtie_ruby" -> {
                    Image(
                        painter = painterResource(id = AshCosmeticAssets.drawableForId(accessoryId)),
                        contentDescription = "Equipped Neckwear",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(mascotWidth * 0.34f, mascotHeight * 0.20f)
                            .align(Alignment.Center)
                            .offset(y = mascotHeight * 0.08f)
                    )
                }
                else -> {
                    Image(
                        painter = painterResource(id = AshCosmeticAssets.drawableForId(accessoryId)),
                        contentDescription = "Equipped Emblem",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(mascotWidth * 0.22f, mascotHeight * 0.22f)
                            .align(Alignment.CenterEnd)
                            .offset(x = (-mascotWidth * 0.12f), y = mascotHeight * 0.08f)
                    )
                }
            }
        }

        // 3. Headwear layer (seated atop Ash's head / visor over eyes)
        if (headwearId != "none_head") {
            val isVisor = headwearId == "hat_cipher_helm"
            Image(
                painter = painterResource(id = AshCosmeticAssets.drawableForId(headwearId)),
                contentDescription = "Equipped Headwear",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(
                        width = mascotWidth * (if (isVisor) 0.40f else 0.44f),
                        height = mascotHeight * (if (isVisor) 0.20f else 0.30f)
                    )
                    .align(Alignment.TopCenter)
                    .offset(y = if (isVisor) mascotHeight * 0.22f else (-mascotHeight * 0.02f))
            )
        }
    }
}
