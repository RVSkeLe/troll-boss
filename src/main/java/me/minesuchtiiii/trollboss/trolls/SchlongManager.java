package me.minesuchtiiii.trollboss.trolls;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.structures.schlong.SchlongStructure;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.entity.Player;

public class SchlongManager {

    private static boolean canSchlong = true;

    private final Player sender;
    private final Player target;
    private final SchlongStructure structure;

    public SchlongManager(Player sender, Player target) {
        this.sender = sender;
        this.target = target;
        this.structure = new SchlongStructure(target);
    }

    public void execute() {
        if (!canSchlong) return;

        sender.sendMessage(StringManager.PREFIX + "§eSchlonging §7" + target.getName() + "§e!");
        target.sendMessage("§eEnjoy the view!");

        start();
        finish();
    }

    private void start() {
        canSchlong = false;

        TrollManager.activate(target.getUniqueId(), TrollType.SCHLONG);
        TrollBoss.getInstance().addTroll();
        TrollBoss.getInstance().getStats().addStats("Schlong", sender);

        structure.generateAndTeleportToBuild();
    }

    private void finish() {
        structure.removeAndTeleportFromBuild();
    }

    public static boolean canSchlong() {
        return canSchlong;
    }

    public static void setCanSchlong(boolean value) {
        canSchlong = value;
    }
}
