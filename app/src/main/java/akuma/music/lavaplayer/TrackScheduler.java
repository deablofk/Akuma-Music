package akuma.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

/** TrackScheduler */
public class TrackScheduler extends AudioEventAdapter {

  private final AudioPlayer player;
  private final BlockingQueue<AudioTrack> queue;
  private MessageChannelUnion messageChannelUnion;
  private boolean isLoopActivated = false;

  public TrackScheduler(AudioPlayer player) {
    this.player = player;
    this.queue = new LinkedBlockingQueue<>();
  }

  public void queue(AudioTrack track) {
    if (!player.startTrack(track, true)) {
      queue.offer(track);
    }
  }

  public AudioTrack nextTrack() {
    var audioTrack = queue.poll();
    player.startTrack(audioTrack, false);
    return audioTrack;
  }

  public AudioTrack skipToPos(int pos) {
    var newQueue = new ArrayList<>(queue);

    if (pos >= queue.size()) return null;

    for (int i = 0; i < pos; i++) {
      queue.remove(newQueue.get(i));
    }

    return nextTrack();
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    if (isLoopActivated) {
      queue(track.makeClone());
    }

    if (endReason.mayStartNext) {
      nextTrack();
    }
  }

  public void setMessageChannelUnion(MessageChannelUnion messageChannelUnion) {
    this.messageChannelUnion = messageChannelUnion;
  }

  @Override
  public void onTrackStart(AudioPlayer player, AudioTrack track) {
    if (messageChannelUnion == null) return;

    var builder = new EmbedBuilder();
    builder.setTitle("Tocando Agora");
    builder.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")");
    builder.setThumbnail(
        "https://img.youtube.com/vi/" + track.getInfo().uri.split("v=")[1] + "/0.jpg");
    messageChannelUnion.sendMessageEmbeds(builder.build()).queue();
  }

  public AudioPlayer getPlayer() {
    return player;
  }

  public BlockingQueue<AudioTrack> getQueue() {
    return queue;
  }

  public AudioTrack getActualSong() {
    return player.getPlayingTrack();
  }

  public void stopAndClearQueue() {
    isLoopActivated = false;
    queue.clear();
    player.stopTrack();
  }

  public boolean loop() {
    isLoopActivated = !isLoopActivated;
    return isLoopActivated;
  }

  public boolean remove(AudioTrack track) {
    return queue.remove(track);
  }
}
