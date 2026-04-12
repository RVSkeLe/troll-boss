package me.minesuchtiiii.trollboss.trolls;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.structures.noob.NoobStructure;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.entity.Player;

public class NoobManager {
    private static boolean canNoob = true;
    private final Player sender;
    private final Player target;
    private final NoobStructure noobStructure;

    public NoobManager(Player sender, Player target) {
        this.sender = sender;
        this.target = target;

        noobStructure = new NoobStructure(target);
    }

    public void executeNoobAction() {
        sender.sendMessage(StringManager.PREFIX + "§eOfficially noobing §7" + target.getName() + "§e!");
        startNoob();
        finishNoob();
    }

    private void startNoob() {
        canNoob = false;
        addStats(target);
        TrollManager.activate(target.getUniqueId(), TrollType.NOOB);

        this.noobStructure.generateAndTeleportToBuild();
    }

    private void finishNoob() {
        this.noobStructure.removeAndTeleportFromBuild();
    }

    private void addStats(Player target) {
        TrollBoss.getInstance().addTroll();
        TrollBoss.getInstance().getStats().addStats("Noob", target);
    }

    public static boolean canNoob() {
        return canNoob;
    }

    public static void setCanNoob(boolean canNoob) {
        NoobManager.canNoob = canNoob;
    }
}
