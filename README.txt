Vetka v1 — Minecraft 1.21.1 / Fabric / Java 21

ClickGUI: Right Shift. Left click toggles modules, right click opens settings, middle click binds a key.

Background image:
Replace src/main/resources/assets/mycheat/textures/gui/vetka_bg.png with your own PNG image.
Recommended size: 1920x1080 pixels (16:9). Keep the filename vetka_bg.png.
The image is used on the Minecraft title screen and behind the ClickGUI.

PhotoMath reads simple arithmetic expressions from incoming chat/system messages and sends the result as a normal chat message.
NameTags enables the vanilla player-name label renderer on this client.

ThunderHack Aura adaptation notices and GPL-3.0 license are included in the project.
\n\nVetka HUD additions in this build:\n- Keystrokes: WASD, SPACE and mouse CPS overlay.\n- PotionHUD: active effects and remaining duration.\n- Ping: server latency in milliseconds.\n- Zoom: hold C to temporarily zoom (restores the previous FOV on release).\nExisting Coordinates, FPS, ArmorStatus, Crosshair and TargetHUD modules remain available.\n\nHUD editor: press F8 in-game, then drag HUD widgets with the left mouse button. Press ESC to save and close.\nMain menu background is solid black; the Minecraft Realms slot is replaced with Account Manager.\nESP rendering is registered after entities for the 1.21.1 world-render stage.\n
HUD dragging: open the in-game chat (T), place the cursor over an enabled HUD widget, hold RIGHT MOUSE BUTTON and drag it. Release to auto-save its position. The F8 HUD editor remains available as an optional alternative.

Combat additions:
- Aura: AutoAim enables/disables automatic aiming; AimSpeed controls the smooth rotation step when RotationMode=Track (0).
- TargetStrafe: Direction 0=Auto (switches side after collision), 1=Right, 2=Left, 3=Toward target, 4=Away from target. Speed and Radius control orbit movement.
- TargetStrafe follows the active Aura/ElytraAura target. Aura only publishes a target while it passes its configured FOV and range checks, so strafing stops when no valid target remains.

Combat and HUD controls
-----------------------
Aura (Combat): enable the module, then leave AutoAim at 1 (On) to rotate toward
the selected valid target. Target categories, FOV, range, aim speed and rotation
mode can be adjusted by right-clicking Aura in the ClickGUI. AutoAim only works
while Aura is enabled and a valid target is selected.

TargetStrafe (Combat): enable Aura and TargetStrafe together. Open TargetStrafe
settings and set Direction to Auto, Right, Left, Toward or Away. Right and Left
force a consistent orbit direction; Auto reverses when colliding with a wall.
Speed and Radius control the orbit.

Movable HUD: press F8 to open the HUD Editor, then left-click and drag a widget.
Press ESC to save and close. PotionHUD's editor area is sized for up to eight
effect rows. Alternatively, while the chat screen is open, right-click and drag
a visible HUD widget. Positions are saved in the Vetka config file.
