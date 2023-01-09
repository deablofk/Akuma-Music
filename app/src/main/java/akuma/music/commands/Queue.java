package akuma.music.commands;

import java.util.ArrayList;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import akuma.music.api.ICommand;
import akuma.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Queue
 */
public class Queue implements ICommand {

	@Override
	public void runCommand(SlashCommandInteractionEvent event) {
		var scheduler = PlayerManager.getGuildAudioPlayer(event.getGuild()).scheduler;
		var acTrack = scheduler.getActualSong();
		var songs = new ArrayList<AudioTrack>(scheduler.getQueue());
		var stringBuilder = new StringBuilder("**Musica Atual: **");
		var embed = new EmbedBuilder();

		if (acTrack != null) {
			stringBuilder.append(acTrack.getInfo().title);
			embed.setThumbnail("https://img.youtube.com/vi/" + acTrack.getInfo().uri.split("v=")[1] + "/0.jpg");
		} else {
			stringBuilder.append("Nenhuma");
		}
		stringBuilder.append("\n\n");

		for (int i = 0; i < songs.size(); i++) {
			var audioTrack = songs.get(i);
			stringBuilder.append("**" + i + "** - [" + audioTrack.getInfo().title + "](" + audioTrack.getInfo().uri + ")" + "\n");
		}

		embed.setDescription(stringBuilder.toString());

		event.replyEmbeds(embed.build()).queue();
	}

	@Override
	public String description() {
		return "show current music list.";
	}

	@Override
	public String name() {
		return "queue";
	}

}
