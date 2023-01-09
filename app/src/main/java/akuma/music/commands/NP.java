package akuma.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import akuma.music.api.ICommand;
import akuma.music.lavaplayer.GuildMusicManager;
import akuma.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/** NP */
public class NP implements ICommand {

  @Override
  public void runCommand(SlashCommandInteractionEvent event) {
    GuildMusicManager manager = PlayerManager.getGuildAudioPlayer(event.getGuild());
    AudioTrack track = manager.player.getPlayingTrack();
    EmbedBuilder embed = new EmbedBuilder();
    embed.setTitle("Musíca Atual");

    if (track == null) {
      event.replyEmbeds(embed.setDescription("O bot não esta tocando nenhuma música.").build()).queue();
      return;
    }

    long pos = track.getPosition() / 1000;
    long dur = track.getDuration() / 1000;
    int stage = (int) ((pos * 30) / dur);

    StringBuilder builder = new StringBuilder();
    builder.append("──────────────────────────────");
    builder.insert(stage, "▶");
    String progressBar = pos / 60 + ":" + pos % 60 + builder.toString() + dur / 60 + ":" + dur % 60;

    event.replyEmbeds(embed.setDescription("Tocando atualmente: [" + track.getInfo().title + ")\n" + progressBar).build()).queue();
  }

  @Override
  public String description() {
    return "Show current music information";
  }

  @Override
  public String name() {
    return "np";
  }
}
