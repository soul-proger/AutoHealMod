package org.soul.autoheal;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Timer;
import java.util.TimerTask;

public class AutoHeal implements ModInitializer {
	private static final int HEAL_INTERVAL = 65000; // 65 секунд
    private KeyBinding healKeyBind;

	@Override
	public void onInitialize() {
		// Регистрируем обработчик события тика клиента
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (healKeyBind.wasPressed()) {
				executeHealCommand();
			}
		});

		// Создаем и регистрируем привязку клавиши
		healKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.autoheal.heal", // Идентификатор привязки
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_H, // Клавиша "H"
				"category.autoheal.general" // Категория в настройках
		));

		// Запускаем таймер для автоматического выполнения команды
		startHealTimer();
	}

	private void startHealTimer() {
        Timer healTimer = new Timer();
		healTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				executeHealCommand();
			}
		}, HEAL_INTERVAL, HEAL_INTERVAL);
	}

	private void executeHealCommand() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			client.player.sendChatMessage("/heal");
		}
	}
}
