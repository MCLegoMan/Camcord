package net.ludocrypt.camcord.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.ludocrypt.camcord.client.config.CamcordConfig;
import net.ludocrypt.camcord.client.data.CamcordClientData;
import net.ludocrypt.camcord.client.registry.CamcordKeybindings;
import net.ludocrypt.camcord.client.shaders.CamcordShaders;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

import java.time.LocalDateTime;

public class CamcordClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		AutoConfig.register(CamcordConfig.class, GsonConfigSerializer::new);
		CamcordShaders.init();
		CamcordKeybindings.init();
		tick();
		overlay();
	}
	private void tick() {
		ClientTickEvents.END.register(client -> {
			CamcordKeybindings.tick();
		});
	}
	private void overlay() {
		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
			if (CamcordConfig.getInstance().dateOverlay) {
				LocalDateTime DATE_TIME = LocalDateTime.now();
				String HOUR = String.valueOf(DATE_TIME.getHour()).length() < 2 ? "0" + (DATE_TIME.getHour() > 12 ? DATE_TIME.getHour() - 12 : DATE_TIME.getHour()) : String.valueOf(DATE_TIME.getHour() > 12 ? DATE_TIME.getHour() - 12 : DATE_TIME.getHour());
				String MINUTE = String.valueOf(DATE_TIME.getMinute()).length() < 2 ? "0" + DATE_TIME.getMinute() : String.valueOf(DATE_TIME.getMinute());
				Text TIME = Text.literal((DATE_TIME.getHour() > 12 ? "PM " : "AM ") + HOUR + ":" + MINUTE).formatted(Formatting.BOLD);
				Text DATE = Text.literal(String.valueOf(DATE_TIME.getDayOfMonth()).length() < 2 ? "0" + DATE_TIME.getDayOfMonth() : DATE_TIME.getDayOfMonth() + " " + DATE_TIME.getMonth().name().substring(0, 3) + ". " + DATE_TIME.getYear()).formatted(Formatting.BOLD);
				drawContext.drawShadowedText(CamcordClientData.CLIENT.textRenderer, TIME, CamcordClientData.CLIENT.getWindow().getScaledWidth() / 2 - CamcordClientData.CLIENT.getWindow().getScaledHeight() / 2, CamcordClientData.CLIENT.getWindow().getScaledHeight() - CamcordClientData.CLIENT.getWindow().getScaledHeight() / 6 - 9, 0xffffff);
				drawContext.drawShadowedText(CamcordClientData.CLIENT.textRenderer, DATE, CamcordClientData.CLIENT.getWindow().getScaledWidth() / 2 - CamcordClientData.CLIENT.getWindow().getScaledHeight() / 2, CamcordClientData.CLIENT.getWindow().getScaledHeight() - CamcordClientData.CLIENT.getWindow().getScaledHeight() / 6, 0xffffff);
			}
		});
	}
}
