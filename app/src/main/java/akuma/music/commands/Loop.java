package akuma.music.commands;

import akuma.music.api.ICommand;
import akuma.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Loop
 */
public class Loop implements ICommand {

	@Override
	public void runCommand(SlashCommandInteractionEvent event) {
		var voiceState = event.getMember().getVoiceState();
		var botVoiceState = event.getGuild().getSelfMember().getVoiceState();

		if (!voiceState.inAudioChannel()) {
			event.replyEmbeds(new EmbedBuilder().setDescription("Você deve estar em um canal de voz.").build()).queue();
			return;
		}

		if (botVoiceState.getChannel() != null && voiceState.getChannel() != botVoiceState.getChannel()) {
			event.replyEmbeds(new EmbedBuilder().setDescription("Você deve estar no mesmo canal do bot.").build())
					.queue();
			return;
		}

		final boolean isLoop = PlayerManager.getGuildAudioPlayer(event.getGuild()).scheduler.loop();

		EmbedBuilder eb = new EmbedBuilder();
		eb.setDescription("Loop Setado Para: " + isLoop);

		event.replyEmbeds(eb.build()).queue();
	}

	@Override
	public String description() {
		return "Ativa repetição da queue atual";
	}

	@Override
	public String name() {
		return "loop";
	}

}
