package io.github.thatsmusic99.headsplus.util.events;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Set;

public abstract class HeadsPlusListener<T> implements Listener {

    private final HashMap<String, String> data;
    protected HeadsPlus hp;

    public HeadsPlusListener() {
        data = new HashMap<>();
        hp = HeadsPlus.getInstance();
    }

    public abstract void onEvent(T event);

    public <D> D addData(String variableName, D data) {
        this.data.put(variableName, String.valueOf(data));
        return data;
    }

    public String getData(String variableName) {
        return data.get(variableName);
    }

    public Set<String> getKeySet() {
        return data.keySet();
    }

    public HashMap<String, String> getData() {
        return data;
    }
}
