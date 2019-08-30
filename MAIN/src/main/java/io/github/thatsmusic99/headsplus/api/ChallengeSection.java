package io.github.thatsmusic99.headsplus.api;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ChallengeSection {

    private List<Challenge> challenges = new ArrayList<>();
    private Material material;
    private byte materialData;
    private String displayName;
    private String name;
    private List<String> lore;

    public ChallengeSection(Material mat, byte data, String displayName, List<String> lore, String name) {
        this.material = mat;
        this.materialData = data;
        this.displayName = displayName;
        this.name = name;
        this.lore = new ArrayList<>();
        for (String str : lore) {
            this.lore.add(ChatColor.translateAlternateColorCodes('&', str));
        }
    }

    public byte getMaterialData() {
        return materialData;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getName() {
        return name;
    }

    public void addChallenge(Challenge c) {
        challenges.add(c);
    }
}
