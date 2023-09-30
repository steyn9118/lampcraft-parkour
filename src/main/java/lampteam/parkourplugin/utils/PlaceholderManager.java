package lampteam.parkourplugin.utils;

import lampteam.parkourplugin.Arena;
import lampteam.parkourplugin.ParkourPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderManager extends PlaceholderExpansion {

    // Плэйсхолдер для отображения голограммы в топах и т.д.
    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {

        if (player == null){
            return "";
        }

        // Ищем арену
        for (Arena arena : ParkourPlugin.getPlugin().getArenas()){

            if (!params.contains(arena.getId())){
                continue;
            }

            // Ищем соответсвующее место в топе
            for (int i = 1; i < arena.getTop().size() + 1; i++){
                if (params.endsWith(String.valueOf(i))){
                    return arena.getTopPlace(i).getPlayerName() + " - " + arena.getTopPlace(i).getSeconds();
                }
            }
            return " - ";
        }
        return "";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "parkourse";
    }

    @Override
    public @NotNull String getAuthor() {
        return "steyn91";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }
}
