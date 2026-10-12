import os
import math
import random
from PIL import Image, ImageFilter, ImageEnhance, ImageChops, ImageDraw

def create_wing_mask(img):
    """Create masks for left wing, right wing, and center body."""
    w, h = img.size
    cx, cy = w / 2, h / 2
    
    # Body is roughly center strip
    # Left wing is x < cx - 40
    # Right wing is x > cx + 40
    return cx, cy

def synthesize_flapping_frame(base_img, frame_idx, total_frames, stage_num):
    """
    Synthesizes a single flapping frame (512x512 RGBA) from the base phoenix artwork
    using segmented wing transformation, body bobbing, flame pulse, and ember particles.
    """
    w, h = base_img.size
    t = (frame_idx / total_frames) * 2 * math.pi
    
    # Flap dynamics
    # flap_factor ranges from -1.0 (wings high) to +1.0 (wings low)
    flap_factor = math.sin(t)
    # Derivative / velocity for tip curl (air resistance)
    flap_velocity = math.cos(t)
    
    # Body bobbing (opposite to wing flap: downstroke lifts body up)
    body_bob_y = int(-14 * flap_factor)
    
    # Stage scaling (growth across chapters)
    # Stage 2: 1.05x, Stage 3: 1.15x, Stage 4: 1.25x, Stage 5: 1.35x
    scale_multiplier = 1.0 + (stage_num - 2) * 0.10
    
    # Create canvas
    frame = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    
    # For Stage 5: "massive red flames by blurring the background"
    if stage_num == 5:
        # Generate dynamic massive red flame aura in background
        flame_bg = Image.new("RGBA", (w, h), (0, 0, 0, 0))
        draw_flame = ImageDraw.Draw(flame_bg)
        
        flame_pulse = 0.85 + 0.25 * math.sin(t * 2)
        flame_cx, flame_cy = w // 2, h // 2 + body_bob_y
        
        # Draw multi-layered raging red/crimson/orange flame tongues
        num_tongues = 16
        for i in range(num_tongues):
            angle = (i / num_tongues) * 2 * math.pi + (t * 0.5)
            r_outer = int((190 + 35 * math.sin(t * 3 + i)) * flame_pulse)
            r_mid = int((140 + 25 * math.cos(t * 2 + i * 2)) * flame_pulse)
            
            px_outer = flame_cx + int(r_outer * math.cos(angle))
            py_outer = flame_cy + int(r_outer * math.sin(angle) * 0.85)
            
            px_mid = flame_cx + int(r_mid * math.cos(angle + 0.15))
            py_mid = flame_cy + int(r_mid * math.sin(angle + 0.15) * 0.85)
            
            # Intense deep red flame spikes
            draw_flame.polygon(
                [(flame_cx, flame_cy), (px_mid, py_mid), (px_outer, py_outer)],
                fill=(255, 30, 10, int(140 * flame_pulse))
            )
            # Fiery orange-gold core bursts
            draw_flame.polygon(
                [(flame_cx, flame_cy), (px_mid, py_mid), (flame_cx + int(r_mid * 0.7 * math.cos(angle)), flame_cy + int(r_mid * 0.7 * math.sin(angle)))],
                fill=(255, 120, 20, int(180 * flame_pulse))
            )
        
        # Heavy radial/Gaussian blur to create the massive blurred atmospheric flame background
        flame_bg = flame_bg.filter(ImageFilter.GaussianBlur(radius=22))
        
        # Additional hot glow layer
        glow_core = Image.new("RGBA", (w, h), (0, 0, 0, 0))
        draw_glow = ImageDraw.Draw(glow_core)
        glow_radius = int(160 * flame_pulse)
        draw_glow.ellipse(
            [flame_cx - glow_radius, flame_cy - glow_radius, flame_cx + glow_radius, flame_cy + glow_radius],
            fill=(255, 60, 20, int(110 * flame_pulse))
        )
        glow_core = glow_core.filter(ImageFilter.GaussianBlur(radius=35))
        
        # Composite flame background
        frame.alpha_composite(glow_core)
        frame.alpha_composite(flame_bg)
    
    # ── Wings and Body Transformation ──────────────────────────────────────────
    # Split image into left wing, right wing, and center body
    left_wing = base_img.crop((0, 0, w // 2, h))
    right_wing = base_img.crop((w // 2, 0, w, h))
    center_body = base_img.copy()
    
    # Wing deformation:
    # During downstroke (flap_factor > 0), wings rotate downwards around shoulder anchor
    # During upstroke (flap_factor < 0), wings rotate upwards
    wing_angle_deg = -flap_factor * 11.0 # degrees
    
    # Transform left wing (anchor near shoulder at w/2, h*0.55)
    # We use affine / rotate with expand
    lw_rotated = left_wing.rotate(
        wing_angle_deg,
        resample=Image.Resampling.BICUBIC,
        center=(left_wing.width, int(h * 0.52))
    )
    
    # Transform right wing (anchor at 0, h*0.52)
    rw_rotated = right_wing.rotate(
        -wing_angle_deg,
        resample=Image.Resampling.BICUBIC,
        center=(0, int(h * 0.52))
    )
    
    # Assemble phoenix figure
    phoenix_fig = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    phoenix_fig.paste(lw_rotated, (0, 0), lw_rotated)
    phoenix_fig.paste(rw_rotated, (w // 2, 0), rw_rotated)
    
    # Overlay center body to preserve razor-sharp facial features, beak, and chest
    body_mask = Image.new("L", (w, h), 0)
    b_draw = ImageDraw.Draw(body_mask)
    # Smooth elliptical feather mask for body center
    body_cx, body_cy = w // 2, int(h * 0.50)
    b_draw.ellipse([body_cx - 85, body_cy - 120, body_cx + 85, body_cy + 130], fill=255)
    body_mask = body_mask.filter(ImageFilter.GaussianBlur(radius=12))
    phoenix_fig.paste(center_body, (0, 0), body_mask)
    
    # Apply vertical bobbing and subtle breathing scale
    breath_scale = 1.0 + 0.025 * math.cos(t)
    final_scale = scale_multiplier * breath_scale
    
    new_w = int(w * final_scale)
    new_h = int(h * final_scale)
    phoenix_scaled = phoenix_fig.resize((new_w, new_h), Image.Resampling.BICUBIC)
    
    # Paste centered with bobbing offset
    offset_x = (w - new_w) // 2
    offset_y = (h - new_h) // 2 + body_bob_y
    
    frame.paste(phoenix_scaled, (offset_x, offset_y), phoenix_scaled)
    
    # ── Trailing Fiery Embers & Sparks ─────────────────────────────────────────
    ember_layer = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    ember_draw = ImageDraw.Draw(ember_layer)
    
    # Generate 18 deterministic orbiting sparks
    random.seed(frame_idx * 137 + stage_num * 997)
    num_sparks = 14 + stage_num * 4
    for sp_idx in range(num_sparks):
        # Orbit phase
        sp_t = t + (sp_idx * 0.35)
        spark_r = 110 + (sp_idx * 9) % 130
        spark_x = int(w / 2 + spark_r * math.cos(sp_t) * 1.1)
        spark_y = int(h / 2 + body_bob_y + spark_r * math.sin(sp_t) * 0.75 - (sp_idx * 3))
        
        if 0 <= spark_x < w and 0 <= spark_y < h:
            s_size = 2 + (sp_idx % 4)
            if stage_num == 5:
                # Stage 5: Massive bright crimson & radiant gold sparks
                s_color = (255, random.randint(40, 180), 20, random.randint(180, 255))
            elif stage_num == 4:
                # Solar gold & white sparks
                s_color = (255, random.randint(200, 255), random.randint(50, 120), random.randint(160, 255))
            else:
                # Flame orange & ember red sparks
                s_color = (255, random.randint(100, 190), 30, random.randint(160, 240))
            
            ember_draw.ellipse(
                [spark_x - s_size, spark_y - s_size, spark_x + s_size, spark_y + s_size],
                fill=s_color
            )
            
    ember_blurred = ember_layer.filter(ImageFilter.GaussianBlur(radius=1.5))
    frame.alpha_composite(ember_blurred)
    frame.alpha_composite(ember_layer)
    
    return frame

print("Script template ready.")
