package akuma.music.commands;


import akuma.music.api.ICommand;
import akuma.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/** Play */
public class Play implements ICommand {

  @Override
  public void runCommand(SlashCommandInteractionEvent event) {
    var query = event.getOption("query").getAsString();
    var voiceState = event.getMember().getVoiceState();
    var botVoiceState = event.getGuild().getSelfMember().getVoiceState();

    if (!voiceState.inAudioChannel()) {
      event
          .replyEmbeds(
              new EmbedBuilder().setDescription("Você deve estar em um canal de voz.").build())
          .queue();
      return;
    }

    if (botVoiceState.getChannel() != null
        && voiceState.getChannel() != botVoiceState.getChannel()) {
      event
          .replyEmbeds(
              new EmbedBuilder().setDescription("Você deve estar no mesmo canal do bot.").build())
          .queue();
      return;
    }

    PlayerManager.loadAndPlay(event, event.getChannel(), query, voiceState.getChannel());
  }

  @Override
  public String description() {
    return "Play a music using a query filter.";
  }

  @Override
  public String name() {
    return "play";
  }

  @Override
  public OptionData[] data() {
    var opts = new OptionData[1];
    opts[0] = new OptionData(OptionType.STRING, "query", "Search or URL", true);

    return opts;
  }
}
