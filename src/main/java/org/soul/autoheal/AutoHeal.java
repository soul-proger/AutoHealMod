package org.soul.autoheal;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

import java.util.Timer;
import java.util.TimerTask;

public class AutoHeal implements ModInitializer {
	private static final int HEAL_INTERVAL = 65000; // 65 секунд
	private Timer healTimer;
	private boolean isTimerRunning = false;
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

		// Регистрируем команду /autoheal
		ClientCommandManager.DISPATCHER.register(
				ClientCommandManager.literal("autoheal")
					.executes(context -> {
						if (isTimerRunning) {
							stopHealTimer();
							context.getSource().sendFeedback(new LiteralText("Авто хилка отключена."));
						} else {
							startHealTimer();
							context.getSource().sendFeedback(new LiteralText("Авто хилка запущена."));
						}
						return 1;
					}
				)
		);
	}

	private void startHealTimer() {
		if (healTimer == null) {
			healTimer = new Timer();
		}
		healTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				executeHealCommand();
			}
		}, 0, HEAL_INTERVAL);
		isTimerRunning = true;
	}

	private void stopHealTimer() {
		if (healTimer != null) {
			healTimer.cancel();
			healTimer.purge();
			healTimer = null;
		}
		isTimerRunning = false;
	}

	private void executeHealCommand() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			client.player.sendChatMessage("/heal");
			client.player.sendMessage(new LiteralText("Здоровье пополнено."), false);
		}
	}
}
