package akuma.music;

import java.util.ArrayList;
import java.util.HashMap;

import akuma.music.api.ICommand;
import akuma.music.commands.Loop;
import akuma.music.commands.NP;
import akuma.music.commands.Play;
import akuma.music.commands.Queue;
import akuma.music.commands.Remove;
import akuma.music.commands.Skip;
import akuma.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

  private static HashMap<String, ICommand> COMMANDS = new HashMap<>();

  static {
    COMMANDS.put("play", new Play());
    COMMANDS.put("queue", new Queue());
    COMMANDS.put("skip", new Skip());
    COMMANDS.put("loop", new Loop());
    COMMANDS.put("rm", new Remove());
    COMMANDS.put("np", new NP());
  }

  @Override
  public void onReady(ReadyEvent event) {
    System.out.println(event.getJDA().getGatewayPing());
    System.out.println("STARTING ON READY");
    for (Guild guild : event.getJDA().getGuilds()) initializeCommands(guild);
    System.out.println("FINISHED ON READY");
  }

  @Override
  public void onGuildJoin(GuildJoinEvent event) {
    initializeCommands(event.getGuild());
  }

  public void initializeCommands(Guild guild) {
    for (ICommand iCommand : new ArrayList<ICommand>(COMMANDS.values())) {
      guild
          .upsertCommand(iCommand.name(), iCommand.description())
          .addOptions(iCommand.data())
          .queue();
      System.out.println("UPSERTING COMMAND " + iCommand.name() + " ON: " + guild.getId());
    }
  }

  @Override
  public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
    var botState = event.getGuild().getSelfMember().getVoiceState();
    var actualChannel = event.getChannelLeft();

    if (!botState.inAudioChannel()
        || actualChannel == null
        || !actualChannel.getId().equals(botState.getChannel().getId())
        || actualChannel.getMembers().size() > 1) return;

    var scheduler = PlayerManager.getGuildAudioPlayer(event.getGuild()).scheduler;
    scheduler.stopAndClearQueue();

    event.getGuild().getAudioManager().closeAudioConnection();
  }

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (event.getGuild().getId() == "272166101025161227"
            && event.getMessageChannel().getId() != "288018689863843841"
        || !COMMANDS.containsKey(event.getName())) return;

    ICommand cmd = COMMANDS.get(event.getName());
    cmd.runCommand(event);
  }
}
