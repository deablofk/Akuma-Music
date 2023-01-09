package akuma.music.lavaplayer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * PlayerManager
 */
public class PlayerManager {

	private static final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
	private static final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

	static {
		AudioSourceManagers.registerRemoteSources(audioPlayerManager);
		AudioSourceManagers.registerLocalSource(audioPlayerManager);
	}

	public static synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
		long guildId = guild.getIdLong();
		GuildMusicManager musicManager = musicManagers.get(guildId);

		if (musicManager == null) {
			musicManager = new GuildMusicManager(audioPlayerManager);
			musicManagers.put(guildId, musicManager);
		}

		guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

		return musicManager;
	}

	public static void loadAndPlay(SlashCommandInteractionEvent event, MessageChannelUnion messageChannelUnion,
			String query, AudioChannel audioChannel) {
		var trackUrl = !isURI(query) ? "ytsearch:" + query : query;
		var musicManager = getGuildAudioPlayer(audioChannel.getGuild());
		musicManager.scheduler.setMessageChannelUnion(messageChannelUnion);

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Musica Adicionada");

		audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				builder.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")");
				builder.setThumbnail("https://img.youtube.com/vi/" + track.getInfo().uri.split("v=")[1] + "/0.jpg");
				event.replyEmbeds(builder.build()).queue();
				play(audioChannel.getGuild(), audioChannel, musicManager, track);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				if (trackUrl.contains("ytsearch:")) {
					var track = playlist.getSelectedTrack() == null ? playlist.getTracks().get(0)
							: playlist.getSelectedTrack();

					builder.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")");
					builder.setThumbnail("https://img.youtube.com/vi/" + track.getInfo().uri.split("v=")[1] + "/0.jpg");
					event.replyEmbeds(builder.build()).queue();
					play(audioChannel.getGuild(), audioChannel, musicManager, track);
				} else {
					var selTrack = playlist.getTracks().get(0);

					builder.setTitle("Playlist Adicionada");
					builder.setDescription("[" + selTrack.getInfo().title + "](" + selTrack.getInfo().uri + ")");
					builder.setThumbnail("https://img.youtube.com/vi/" + selTrack.getInfo().uri.split("v=")[1] + "/0.jpg");
					event.replyEmbeds(builder.build()).queue();

					if (!audioChannel.getGuild().getAudioManager().isConnected())
						audioChannel.getGuild().getAudioManager().openAudioConnection(audioChannel);

					for (AudioTrack track : playlist.getTracks()) {
						musicManager.scheduler.queue(track);
					}
				}
			}

			@Override
			public void noMatches() {
				builder.setTitle(null);
				builder.setDescription("Nada encontrado para: " + trackUrl);
				event.replyEmbeds(builder.build()).queue();
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				builder.setTitle(null);
				builder.setDescription("Não foi possível tocar: " + trackUrl);
				event.replyEmbeds(builder.build()).queue();
			}
		});

	}

	private static void play(Guild guild, AudioChannel audioChannel, GuildMusicManager musicManager, AudioTrack track) {
		if (!guild.getAudioManager().isConnected())
			guild.getAudioManager().openAudioConnection(audioChannel);

		musicManager.scheduler.queue(track);
	}

	private static boolean isURI(String url) {
		try {
			new URI(url);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}
}
