package akuma.music.api;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * ICommand
 */
public interface ICommand {

	void runCommand(SlashCommandInteractionEvent event);

	default int minParameters() {
		return 0;
	}

	String description();

	String name();

	default OptionData[] data() {
		return new OptionData[0];
	}

}
