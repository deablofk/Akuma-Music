package akuma.music.commands;

import java.util.ArrayList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import akuma.music.api.ICommand;
import akuma.music.lavaplayer.GuildMusicManager;
import akuma.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/** Remove */
public class Remove implements ICommand {

  @Override
  public void runCommand(SlashCommandInteractionEvent event) {
		var pos = event.getOption("position");

    GuildMusicManager playerManager = PlayerManager.getGuildAudioPlayer(event.getGuild());
    List<AudioTrack> songs = new ArrayList<>(playerManager.scheduler.getQueue());
	  AudioTrack track = null;

    if(pos.getAsInt() <= songs.size()) {
      track = songs.get(pos.getAsInt());
      playerManager.scheduler.remove(track);
    }

		var builder = new EmbedBuilder();
		var nextSong = track == null ? "Nenhuma" : track.getInfo().title;

		builder.setTitle("Musica Remove");
		builder.setDescription(nextSong);
		builder.setFooter(event.getMember().getEffectiveName(), event.getMember().getEffectiveAvatarUrl());
		event.replyEmbeds(builder.build()).queue();
    
    
  }

  @Override
  public String description() {
    return "remove actual song or specified position";
  }

  @Override
  public String name() {
    return "rm";
  }

  @Override
  public OptionData[] data() {
		var opts = new OptionData[1];
		opts[0] = new OptionData(OptionType.INTEGER, "position", "position to remove", true).setMinValue(1);

		return opts;
  }
}
