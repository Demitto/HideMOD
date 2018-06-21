package handler;

import java.awt.List;
import java.util.ArrayList;

import hideMod.HideMod;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import newwork.PacketPlaySound;
import types.inGame.HideSound;
import types.Sound;

/** サーバーからクライアントへサウンドを流すハンドラ */
public class SoundHandler {
	public static final SoundHandler INSTANCE = new SoundHandler();
	/** 1ブロック当たり何tickかかるか */
	private static final float SOUND_SPEAD = 0.2f;

	/** 再生リクエストを送信 サーバーサイドで呼んでください 射撃音など遠距離まで聞こえる必要がある音に使用 */
	public static void broadcastSound(String soundName, Entity e, float range, float vol, float pitch,
			boolean useSoundDelay, boolean useDecay) {
		broadcastSound(e.worldObj, soundName, e.posX, e.posY, e.posZ, range, vol, pitch, useSoundDelay, useDecay);
	}

	/** 再生リクエストを送信 サーバーサイドで呼んでください 射撃音など遠距離まで聞こえる必要がある音に使用 */
	public static void broadcastSound(World w,double x, double y, double z, Sound sound) {
		broadcastSound(w, sound.name, x, y, z, sound.range, sound.vol, sound.pitch, sound.isDelay, sound.isDecay);
	}

	/** 再生リクエストを送信 サーバーサイドで呼んでください 射撃音など遠距離まで聞こえる必要がある音に使用 */
	public static void broadcastSound(World w, String soundName, double x, double y, double z, float range, float vol,
			float pitch, boolean useSoundDelay, boolean useDecay) {
		// 同じワールドのプレイヤーの距離を計算してパケットを送信
		for (EntityPlayer player : (ArrayList<EntityPlayer>) w.playerEntities) {
			double distance = new Vec3(x, y, z).distanceTo(new Vec3(player.posX, player.posY, player.posZ));
			if (distance < range) {
				float playerVol = vol;
				if (useDecay) {
					playerVol = playerVol * (float) (1 - (distance / range));
				}
				int Delay = 0;
				if (useSoundDelay) {
					Delay = (int) (distance * SOUND_SPEAD);
				}
				// TODO そのうちドップラー効果でも
				// パケット
				PacketHandler.INSTANCE.sendTo(new PacketPlaySound(soundName, x, y, z, playerVol, pitch, Delay),
						(EntityPlayerMP) player);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void playSound(double x, double y, double z, Sound sound) {
		playSound(sound.name, x, y, z, sound.range, sound.vol, sound.pitch, sound.isDelay, sound.isDecay);
	}
	@SideOnly(Side.CLIENT)
	public static void playSound(String soundName, double x, double y, double z, float range, float vol, float pitch,
			boolean useSoundDelay, boolean useDecay) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		double distance = new Vec3(x, y, z).distanceTo(new Vec3(player.posX, player.posY, player.posZ));
		if (distance < range) {
			float playerVol = vol;
			if (useDecay) {
				playerVol = playerVol * (float) (1 - (distance / range));
			}
			int Delay = 0;
			if (useSoundDelay) {
				Delay = (int) (distance * SOUND_SPEAD);
			}
			Minecraft.getMinecraft().getSoundHandler().playDelayedSound(new HideSound(soundName, playerVol, pitch, (float)x, (float)y, (float)z), Delay);
		}
	}
}