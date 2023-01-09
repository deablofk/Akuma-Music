package akuma.music;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class App {

    public static void main(String[] args) {
	JDABuilder builder = JDABuilder.createDefault("token");
	builder.enableCache(CacheFlag.VOICE_STATE);
	builder.setBulkDeleteSplittingEnabled(false);
	builder.setCompression(Compression.NONE);
	builder.setActivity(Activity.watching("FUTABU"));
	builder.addEventListeners(new CommandListener());
	builder.build();
    }

}
