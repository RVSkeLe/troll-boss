package me.minesuchtiiii.trollboss.trolls;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RunforrestManager {
    private final HashMap<String, Integer> fiveSecondTimerTask = new HashMap<>();
    private final HashMap<String, Integer> sixtySecondTimerTask = new HashMap<>();
    private final HashMap<String, Integer> tasks2 = new HashMap<>();
    private final HashMap<String, Location> rfloc = new HashMap<>();
    public HashMap<String, Integer> rftime = new HashMap<>();
    public HashMap<String, Integer> warnTime = new HashMap<>();
    public HashMap<String, String> rfmsg = new HashMap<>();
    public HashMap<String, Boolean> rf = new HashMap<>();

    /**
     * Cancels a scheduled task associated with the specified player and removes it from the task map.
     *
     * @param task a {@code HashMap<String, Integer>} mapping player names to their respective task IDs
     * @param p    the {@code Player} whose task is to be canceled
     */
    private void cancelTask(HashMap<String, Integer> task, Player p) {

        if (!task.containsKey(p.getName())) {
            return;
        }
        final int tid = task.get(p.getName());
        Bukkit.getScheduler().cancelTask(tid);
        task.remove(p.getName());

    }

    private void editRFTime(Player p) {

        if (this.rftime.containsKey(p.getName())) {

            this.rftime.replace(p.getName(), this.rftime.get(p.getName()) - 1);

        }

    }

    private int getRFTime(Player p) {

        return this.rftime.getOrDefault(p.getName(), 0);

    }

    private void editWarnTime(Player p) {

        if (this.warnTime.containsKey(p.getName())) {

            this.warnTime.replace(p.getName(), this.warnTime.get(p.getName()) - 1);

        }

    }

    private int getWarnTime(Player p) {
        return this.warnTime.getOrDefault(p.getName(), 0);
    }

    public void start5SecRunTimer(Player player) {

        this.warnTime.put(player.getName(), 11);
        sendInitialMessages(player);

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TrollBoss.getInstance(), () -> handleWarningTimer(player), 0L, 20L);
        fiveSecondTimerTask.put(player.getName(), taskId);
    }

    private void sendInitialMessages(Player player) {
        player.sendMessage(String.format(StringManager.ENGLISH_INITIAL_MSG, getRFTime(player)));
        player.sendMessage(StringManager.ENGLISH_DEATH_MSG);

    }

    private void handleWarningTimer(Player player) {
        editWarnTime(player);
        int warnTime = getWarnTime(player);

        if (warnTime > 0) {
            player.sendMessage(String.format(StringManager.ENGLISH_WARNING_MSG, warnTime));
        } else {
            player.sendMessage(StringManager.ENGLISH_WARNING_FINAL);

            this.warnTime.remove(player.getName());
            this.rf.put(player.getName(), true);
            start60SekRunTimer(player);
            cancelTask(fiveSecondTimerTask, player);
        }
    }


    private void check4movement(Player p) {

        if (!rf.containsKey(p.getName())) {
            return;
        }
        final int checki = Bukkit.getScheduler().scheduleSyncRepeatingTask(TrollBoss.getInstance(), () -> {

            if (this.rfloc.containsKey(p.getName())) {
                if (this.rfloc.get(p.getName()).equals(p.getLocation())) {

                    p.sendMessage("§7[§4INFO§7] §cAll you had to do was to damn move CJ!");

                    this.rf.replace(p.getName(), false);
                    this.cancelTask(tasks2, p);

                    final String trollername = this.rfmsg.get(p.getName());
                    final Player troller = Bukkit.getPlayer(trollername);

                    if (troller != null) {

                        troller.sendMessage(StringManager.PREFIX + "§eSucessfully trolled §7" + p.getName() + "§e!");
                        this.rfmsg.remove(p.getName());

                    }

                } else {

                    rf.replace(p.getName(), true);

                }

            }
        }, 40L, 20L);
        tasks2.put(p.getName(), checki);

    }

    private void start60SekRunTimer(Player p) {

        rfloc.put(p.getName(), p.getLocation());
        check4movement(p);

        final int m2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(TrollBoss.getInstance(), () -> {

            if (getRFTime(p) > 0 && rf.get(p.getName())) {

                editRFTime(p);
                rfloc.put(p.getName(), p.getLocation());

                p.setLevel(getRFTime(p));

            } else if (getRFTime(p) > 0 && !rf.get(p.getName())) {

                rftime.remove(p.getName());
                rf.remove(p.getName());
                rfloc.remove(p.getName());
                p.setLevel(0);
                p.setHealth(0D);
                cancelTask(sixtySecondTimerTask, p);

            } else if (getRFTime(p) == 0 && rf.get(p.getName())) {

                rf.remove(p.getName());
                rftime.remove(p.getName());
                rfloc.remove(p.getName());
                p.setLevel(0);
                cancelTask(sixtySecondTimerTask, p);

                final String targetname = rfmsg.get(p.getName());
                final Player troller = Bukkit.getPlayer(targetname);

                p.sendMessage("§7[§4INFO§7] §aYou survived!");

                if (troller != null) {

                    troller.sendMessage(StringManager.PREFIX + "§ePlayer §7" + p.getName() + " §esurvived the troll!");
                    rfmsg.remove(p.getName());

                }

            }

        }, 1L, 20L);

        sixtySecondTimerTask.put(p.getName(), m2);

    }
}
