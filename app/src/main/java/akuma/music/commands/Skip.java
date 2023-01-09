package akuma.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import akuma.music.api.ICommand;
import akuma.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * Next
 */
public class Skip implements ICommand {

	@Override
	public void runCommand(SlashCommandInteractionEvent event) {

		var pos = event.getOption("position");

		final AudioTrack track;

		if ( pos != null )
			track = PlayerManager.getGuildAudioPlayer(event.getGuild()).scheduler.skipToPos(pos.getAsInt());
		else 
			track = PlayerManager.getGuildAudioPlayer(event.getGuild()).scheduler.nextTrack();

		var builder = new EmbedBuilder();
		var nextSong = track == null ? "Nenhuma" : track.getInfo().title;

		builder.setTitle("Musica Skipada");
		builder.setDescription("Próxima musica: " + nextSong);
		builder.setFooter(event.getMember().getEffectiveName(), event.getMember().getEffectiveAvatarUrl());
		event.replyEmbeds(builder.build()).queue();
	}

	@Override
	public String description() {
		return "skip current song.";
	}

	@Override
	public String name() {
		return "skip";
	}

	@Override
	public OptionData[] data() {
		var opts = new OptionData[1];
		opts[0] = new OptionData(OptionType.INTEGER, "position", "position to skip", false).setMinValue(1);

		return opts;
	}

}
